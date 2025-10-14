package ee.ria.tara.service;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import ee.ria.tara.configuration.providers.EIdCertificateConfigurationProvider;
import ee.ria.tara.configuration.providers.EIdCertificateConfigurationProvider.LdapSource;
import ee.ria.tara.util.EidCertificateTestUtil;
import ee.ria.tara.util.EidCertificateTestUtil.CertificateParams;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.presentation.Representation;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

import static ee.ria.tara.service.EIdCertificateService.CERT_BINARY_ATTR;
import static ee.ria.tara.service.EIdCertificateService.POLICY_IDENTIFIER_BLACKLIST;
import static ee.ria.tara.util.EidCertificateTestUtil.createCertificate;
import static ee.ria.tara.util.EidCertificateTestUtil.simplifiedDescription;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@Slf4j
class EIdCertificateServiceTest {

    private static final LdapSource FIRST_LDAP_SOURCE = new LdapSource("first-ldap-source.ee", 1, "dc=first-baseDn");
    private static final LdapSource SECOND_LDAP_SOURCE = new LdapSource("second-ldap-source.ee", 2, "dc=second-baseDn");
    private static final String ID_CODE = "30303039914";
    private static final String SN_QUERY = "serialNumber=PNOEE-" + ID_CODE;
    private static final X509Certificate FIRST_VALID_CERTIFICATE = createCertificate(CertificateParams.builder()
            .idCode(ID_CODE)
            .policyIdentifier("1.3.6.1.4.1.10015.1.1")
            .build());
    private static final X509Certificate SECOND_VALID_CERTIFICATE = createCertificate(CertificateParams.builder()
            .idCode(ID_CODE)
            .policyIdentifier("1.3.6.1.4.1.51361.1.1.1")
            .build());


    private static final Representation CERTIFICATE_SIMPLE_REPRESENTATION = new StandardRepresentation() {
        @Override
        public String toStringOf(Object object) {
            if (object instanceof X509Certificate certificate) {
                return simplifiedDescription(certificate);
            }
            return super.toStringOf(object);
        }
    };

    private EIdCertificateService service;

    private EIdCertificateConfigurationProvider configuration;
    private MockEIdLdapConnectionFactory eIdLdapConnectionFactory;

    @AfterEach
    void tearDown() {
        eIdLdapConnectionFactory.verifyNoOpenConnections();
        eIdLdapConnectionFactory = null;
        configuration = null;
        service = null;
    }

    @SneakyThrows
    void initService(EIdCertificateConfigurationProvider configuration) {
        this.configuration = configuration;
        this.eIdLdapConnectionFactory = new MockEIdLdapConnectionFactory();
        this.service = new EIdCertificateService(this.configuration, this.eIdLdapConnectionFactory);
    }

    @SneakyThrows
    void mockLdapSearchResult(LdapSource ldapSource, List<List<X509Certificate>> entries) {
        LDAPConnection mockConnection = eIdLdapConnectionFactory.mockConnection(ldapSource);
        String simplifiedSearchResultDescription = entries.stream()
                .map(entry -> entry.stream()
                        .map(EidCertificateTestUtil::simplifiedDescription)
                        .collect(Collectors.joining(", ")))
                .map(simplifiedEntryDescription -> "[" + simplifiedEntryDescription + "]")
                .collect(Collectors.joining(", "));
        log.info("Mocking LDAP source {}:{} to return the following entries: {}",
                ldapSource.host(), ldapSource.port(), simplifiedSearchResultDescription);
        List<SearchResultEntry> searchResultEntries = entries.stream()
                .map(this::toSearchResultEntry)
                .toList();
        SearchResult searchResult = createSearchResult(searchResultEntries);
        doReturn(searchResult).when(mockConnection).search(
                ldapSource.baseDn(),
                SearchScope.SUB,
                SN_QUERY,
                CERT_BINARY_ATTR
        );
    }

    @SneakyThrows
    void mockLdapSearchResult(LdapSource ldapSource, Exception e) {
        LDAPConnection mockConnection = eIdLdapConnectionFactory.mockConnection(ldapSource);
        log.info("Mocking LDAP source {}:{} to throw exception: {}",
                ldapSource.host(), ldapSource.port(), e.getMessage());
        doThrow(e).when(mockConnection).search(
                ldapSource.baseDn(),
                SearchScope.SUB,
                SN_QUERY,
                CERT_BINARY_ATTR
        );
    }

    @SneakyThrows
    SearchResult createSearchResult(List<SearchResultEntry> entries) {
        return new SearchResult(
                299, ResultCode.SUCCESS, null, null, null, entries, null, entries.size(), 0, null);
    }

    private SearchResultEntry toSearchResultEntry(List<X509Certificate> certificates) {
        Attribute[] attributes = certificates.stream()
                .map(this::toSearchResultEntryAttribute)
                .toArray(Attribute[]::new);
        return new SearchResultEntry("dn", attributes);
    }

    @SneakyThrows
    private Attribute toSearchResultEntryAttribute(X509Certificate certificate) {
        return new Attribute(certificate.getSerialNumber().toString(), certificate.getEncoded());
    }

    @Nested
    class FindEncryptionCertificates {

        @Test
        void whenNullIdCodeProvided_exceptionThrown() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);

            //noinspection DataFlowIssue: Passing null as a @NonNull parameter is by design
            assertThatException()
                    .isThrownBy(() -> service.findEncryptionCertificates(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void whenNonNumericIdCodeProvided_exceptionThrown() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);

            assertThatException()
                    .isThrownBy(() -> service.findEncryptionCertificates("ID_CODE"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void whenNoLdapSourcesConfigured_emptyResultReturned() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .build();
            initService(configuration);

            List<X509Certificate> result = service.findEncryptionCertificates(ID_CODE);

            assertThat(result).isEmpty();
        }

        @Test
        void whenEmptySearchResult_emptyResultReturned() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);
            mockLdapSearchResult(FIRST_LDAP_SOURCE, List.of());

            List<X509Certificate> result = service.findEncryptionCertificates(ID_CODE);

            assertThat(result).isEmpty();
        }

        @Test
        void whenSearchResultContainsValidCertificate_theCertificateReturned() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);
            mockLdapSearchResult(FIRST_LDAP_SOURCE, List.of(List.of(FIRST_VALID_CERTIFICATE)));

            List<X509Certificate> result = service.findEncryptionCertificates(ID_CODE);

            assertThat(result)
                    .withRepresentation(CERTIFICATE_SIMPLE_REPRESENTATION)
                    .isEqualTo(List.of(FIRST_VALID_CERTIFICATE));
        }

        @Test
        void whenSearchResultContainsValidCertificatesFromMultipleSources_allCertificatesReturned() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .ldapSource(SECOND_LDAP_SOURCE)
                    .build();
            initService(configuration);
            mockLdapSearchResult(FIRST_LDAP_SOURCE, List.of(List.of(FIRST_VALID_CERTIFICATE)));
            mockLdapSearchResult(SECOND_LDAP_SOURCE, List.of(List.of(SECOND_VALID_CERTIFICATE)));

            List<X509Certificate> result = service.findEncryptionCertificates(ID_CODE);

            assertThat(result)
                    .withRepresentation(CERTIFICATE_SIMPLE_REPRESENTATION)
                    .isEqualTo(List.of(FIRST_VALID_CERTIFICATE, SECOND_VALID_CERTIFICATE));
        }

        @Test
        void whenSingleSearchResultEntryContainsMultipleValidCertificates_allCertificatesReturned() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);
            mockLdapSearchResult(FIRST_LDAP_SOURCE, List.of(
                    List.of(FIRST_VALID_CERTIFICATE, SECOND_VALID_CERTIFICATE)));

            List<X509Certificate> result = service.findEncryptionCertificates(ID_CODE);

            assertThat(result)
                    .withRepresentation(CERTIFICATE_SIMPLE_REPRESENTATION)
                    .isEqualTo(List.of(FIRST_VALID_CERTIFICATE, SECOND_VALID_CERTIFICATE));
        }

        @Test
        void whenSearchResultContainsMultipleEntriesOfValidCertificates_allCertificatesReturned() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);
            mockLdapSearchResult(FIRST_LDAP_SOURCE, List.of(
                    List.of(FIRST_VALID_CERTIFICATE),
                    List.of(SECOND_VALID_CERTIFICATE)));

            List<X509Certificate> result = service.findEncryptionCertificates(ID_CODE);

            assertThat(result)
                    .withRepresentation(CERTIFICATE_SIMPLE_REPRESENTATION)
                    .isEqualTo(List.of(FIRST_VALID_CERTIFICATE, SECOND_VALID_CERTIFICATE));
        }

        @Test
        void whenSearchResultContainsCertificateWithUnknownPolicyIdentifier_thatCertificateIncludedInResult() {
            String unknownPolicyIdentifier = "1.2.3";
            X509Certificate unknownPolicyIdentifierCertificate = createCertificate(CertificateParams.builder()
                    .idCode(ID_CODE)
                    .policyIdentifier(unknownPolicyIdentifier)
                    .build());
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);
            mockLdapSearchResult(FIRST_LDAP_SOURCE, List.of(
                    List.of(FIRST_VALID_CERTIFICATE),
                    List.of(unknownPolicyIdentifierCertificate)));

            List<X509Certificate> result = service.findEncryptionCertificates(ID_CODE);

            assertThat(result)
                    .withRepresentation(CERTIFICATE_SIMPLE_REPRESENTATION)
                    .isEqualTo(List.of(FIRST_VALID_CERTIFICATE, unknownPolicyIdentifierCertificate));
        }

        @Test
        void whenSearchResultContainsCertificateWithBlacklistedPolicyIdentifier_onlyValidCertificatesReturned() {
            String blacklistedPolicyIdentifier = POLICY_IDENTIFIER_BLACKLIST.iterator().next();
            X509Certificate blacklistedPolicyIdentifierCertificate = createCertificate(CertificateParams.builder()
                    .idCode(ID_CODE)
                    .policyIdentifier(blacklistedPolicyIdentifier)
                    .build());
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);
            mockLdapSearchResult(FIRST_LDAP_SOURCE, List.of(
                    List.of(FIRST_VALID_CERTIFICATE),
                    List.of(blacklistedPolicyIdentifierCertificate)));

            List<X509Certificate> result = service.findEncryptionCertificates(ID_CODE);

            assertThat(result)
                    .withRepresentation(CERTIFICATE_SIMPLE_REPRESENTATION)
                    .isEqualTo(List.of(FIRST_VALID_CERTIFICATE));
        }

        @Test
        void whenSearchFails_exceptionPropagated() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .build();
            initService(configuration);
            RuntimeException exception = new RuntimeException("Oh no!");
            mockLdapSearchResult(FIRST_LDAP_SOURCE, exception);

            assertThatException()
                    .isThrownBy(() -> service.findEncryptionCertificates(ID_CODE))
                    .isEqualTo(exception);
        }

        @Test
        void whenSearchFailsOnFirstSource_exceptionPropagated() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .ldapSource(SECOND_LDAP_SOURCE)
                    .build();
            initService(configuration);
            RuntimeException exception = new RuntimeException("Oh no!");
            mockLdapSearchResult(FIRST_LDAP_SOURCE, exception);
            mockLdapSearchResult(SECOND_LDAP_SOURCE, List.of());

            assertThatException()
                    .isThrownBy(() -> service.findEncryptionCertificates(ID_CODE))
                    .isEqualTo(exception);
        }

        @Test
        void whenSearchFailsOnSecondSource_exceptionPropagated() {
            EIdCertificateConfigurationProvider configuration = EIdCertificateConfigurationProvider.builder()
                    .ldapSource(FIRST_LDAP_SOURCE)
                    .ldapSource(SECOND_LDAP_SOURCE)
                    .build();
            initService(configuration);
            RuntimeException exception = new RuntimeException("Oh no!");
            mockLdapSearchResult(FIRST_LDAP_SOURCE, List.of());
            mockLdapSearchResult(SECOND_LDAP_SOURCE, exception);

            assertThatException()
                    .isThrownBy(() -> service.findEncryptionCertificates(ID_CODE))
                    .isEqualTo(exception);
        }

    }

}
