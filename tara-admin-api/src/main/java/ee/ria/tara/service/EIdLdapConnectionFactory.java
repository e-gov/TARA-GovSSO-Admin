package ee.ria.tara.service;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import ee.ria.tara.configuration.providers.EIdCertificateConfigurationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;

@Service
@RequiredArgsConstructor
public class EIdLdapConnectionFactory {

    private final SSLContext sslContext;

    public LDAPConnection connect(EIdCertificateConfigurationProvider.LdapSource ldapSource) throws LDAPException {
        LDAPConnection ldapConnection = new LDAPConnection(sslContext.getSocketFactory());
        ldapConnection.connect(ldapSource.host(), ldapSource.port());
        return ldapConnection;
    }

}
