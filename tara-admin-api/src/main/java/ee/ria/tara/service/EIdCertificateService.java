package ee.ria.tara.service;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import ee.ria.tara.configuration.providers.EIdCertificateConfigurationProvider;
import ee.ria.tara.configuration.providers.EIdCertificateConfigurationProvider.LdapSource;
import ee.ria.tara.controllers.exception.FatalApiException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class EIdCertificateService {

    /* Blacklist Mobile-ID policy identifiers as Mobile-ID cannot be used for decrypting.  */
    static final Set<String> POLICY_IDENTIFIER_BLACKLIST = Set.of(
            //Mobile-ID (https://www.sk.ee/upload/files/SK-CPR-ESTEID-EN-v8_3-20190605.pdf)
            "1.3.6.1.4.1.10015.1.3",
            //Mobile-ID (https://www.skidsolutions.eu/wp-content/uploads/2025/01/Certificate_and_OCSP_Profile_for_Mobile-ID_v_2_3.pdf)
            "1.3.6.1.4.1.10015.18.1"
    );


    static final String SN_QUERY = "serialNumber=PNOEE-%s";
    static final String CERT_BINARY_ATTR = "userCertificate;binary";
    static final Pattern ID_CODE_PATTERN = Pattern.compile("^[0-9]+$");

    private static final CertificateFactory X509_FACTORY;

    private final EIdCertificateConfigurationProvider eIdCertificateConfiguration;
    private final EIdLdapConnectionFactory eIdLdapConnectionFactory;

    static {
        try {
            X509_FACTORY = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<X509Certificate> findEncryptionCertificates(@NonNull String idCode) throws FatalApiException {
        if (!ID_CODE_PATTERN.matcher(idCode).matches()) {
            throw new IllegalArgumentException("`idCode` should be numeric");
        }
        return eIdCertificateConfiguration.ldapSources().stream()
                // Could do it in parallel but sequential execution is good enough
                .map(ldapSource -> findEncryptionCertificates(idCode, ldapSource))
                .flatMap(Collection::stream)
                .toList();
    }

    private List<X509Certificate> findEncryptionCertificates(String idCode, LdapSource ldapSource) {
        log.info("Requesting certificate from LDAP for user \"{}\" from LDAP server {}:{}",
                idCode, ldapSource.host(), ldapSource.port());
        try (LDAPConnection connection = eIdLdapConnectionFactory.connect(ldapSource)) {
            SearchResult result = connection.search(
                    ldapSource.baseDn(), SearchScope.SUB, format(SN_QUERY, idCode), CERT_BINARY_ATTR);
            return extractEncryptionCertificates(result);
        } catch (LDAPException e) {
            log.error(String.format("Failed to find authentication certificate for user %s.", idCode), e);
            throw new FatalApiException(e);
        }
    }

    private List<X509Certificate> extractEncryptionCertificates(SearchResult result) {
        return result.getSearchEntries().stream()
                .map(SearchResultEntry::getAttributes)
                .flatMap(Collection::stream)
                .filter(this::isSuitableAuthCertificateForEncryption)
                .map(this::toX09Certificate)
                .collect(Collectors.toList());
    }

    private boolean isSuitableAuthCertificateForEncryption(Attribute userCertificate) {
        X509CertificateHolder certificate = getCertificate(userCertificate);
        return isAuthCertificate(certificate) && !isPolicyIdentifierBlacklisted(certificate);
    }

    private X509CertificateHolder getCertificate(Attribute userCertificate) {
        try {
            return new X509CertificateHolder(userCertificate.getValueByteArray());
        } catch (IOException e) {
            log.error("Failed to convert to X509CertificateHolder.", e);
            throw new RuntimeException(e);
        }
    }

    private boolean isAuthCertificate(X509CertificateHolder certificate) {
        KeyUsage keyUsage = KeyUsage.fromExtensions(certificate.getExtensions());
        return keyUsage.hasUsages(KeyUsage.keyEncipherment) || keyUsage.hasUsages(KeyUsage.keyAgreement);
    }

    private boolean isPolicyIdentifierBlacklisted(X509CertificateHolder certificate) {
        CertificatePolicies certificatePolicies = CertificatePolicies.fromExtensions(certificate.getExtensions());
        return Stream.of(certificatePolicies.getPolicyInformation())
                .map(PolicyInformation::getPolicyIdentifier)
                .map(ASN1ObjectIdentifier::getId)
                .anyMatch(POLICY_IDENTIFIER_BLACKLIST::contains);
    }

    private X509Certificate toX09Certificate(Attribute userCertificate) {
        try {
            return (X509Certificate) X509_FACTORY.generateCertificate(new ByteArrayInputStream(userCertificate.getValueByteArray()));
        } catch (CertificateException e) {
            log.error("Failed to generate X509Certificate.", e);
            throw new RuntimeException(e);
        }
    }

}
