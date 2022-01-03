package ee.ria.tara.service;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import ee.ria.tara.configuration.providers.CertificateServiceConfigurationProvider;
import ee.ria.tara.controllers.exception.FatalApiException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Slf4j
@Service
public class CertificateService {
    private static final String BASE_DN = "c=EE";
    private static final String SN_QUERY = "serialNumber=PNOEE-%s";
    private static final String CERT_BINARY_ATTR = "userCertificate;binary";

    public static final CertificateFactory X509_FACTORY;

    private final CertificateServiceConfigurationProvider configurationProvider;

    static {
        try {
            X509_FACTORY = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new IllegalStateException(e);
        }
    }

    public CertificateService(CertificateServiceConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public List<X509Certificate> findAuthenticationCertificates(String idCode) throws FatalApiException {
        log.info("Requesting certificate from LDAP for user: " + idCode);
        try (LDAPConnection connection = new LDAPConnection(getSslSocketFactory())) {
            connection.connect(configurationProvider.getUrl(), configurationProvider.getPort());
            return executeSearch(connection, idCode);
        } catch (GeneralSecurityException | LDAPException e) {
            log.error(String.format("Failed to find authentication certificate for user: %s.", idCode), e);
            throw new FatalApiException(e);
        }
    }

    private SSLSocketFactory getSslSocketFactory() throws GeneralSecurityException {
        SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
        return sslUtil.createSSLSocketFactory();
    }

    private List<X509Certificate> executeSearch(LDAPConnection connection, String idCode) throws LDAPSearchException {
        SearchResult result = connection.search(BASE_DN, SearchScope.SUB, format(SN_QUERY, idCode), CERT_BINARY_ATTR);
        return result.getSearchEntries().stream()
                .map(SearchResultEntry::getAttributes)
                .flatMap(Collection::stream)
                .filter(this::isSuitableAuthCertificateForEncryption)
                .map(this::toX09Certificate)
                .collect(Collectors.toList());
    }

    private boolean isSuitableAuthCertificateForEncryption(Attribute userCertificate) {
        X509CertificateHolder certificate = getCertificate(userCertificate);
        return isAuthCertificate(certificate) && hasIdCardOrDigiIdPolicyIdentifier(certificate);
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

    private boolean hasIdCardOrDigiIdPolicyIdentifier(X509CertificateHolder certificate) {
        CertificatePolicies certificatePolicies = CertificatePolicies.fromExtensions(certificate.getExtensions());
        return Stream.of(certificatePolicies.getPolicyInformation())
                .map(PolicyInformation::getPolicyIdentifier)
                .map(ASN1ObjectIdentifier::getId)
                .anyMatch(this::isIdCardOrDigiIdPolicyIdentifier);
    }

    private boolean isIdCardOrDigiIdPolicyIdentifier(String policyIdentifier) {
        //older ID-card and Digi-ID policies (https://www.sk.ee/upload/files/SK-CPR-ESTEID-EN-v8_3-20190605.pdf)
        return policyIdentifier.equals("1.3.6.1.4.1.10015.1.1")
                || policyIdentifier.equals("1.3.6.1.4.1.10015.1.2")
                //newer ID-card and Digi-ID policies (https://www.sk.ee/upload/files/SK-CPR-ESTEID2018-EN-v1_1_20190503.pdf)
                || policyIdentifier.equals("1.3.6.1.4.1.51361.1.1.1")
                || policyIdentifier.equals("1.3.6.1.4.1.51361.1.1.3");
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
