package ee.ria.tara.service;

import com.unboundid.ldap.sdk.LDAPConnection;
import ee.ria.tara.configuration.providers.EIdCertificateConfigurationProvider.LdapSource;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;

import static ee.ria.tara.util.MockitoUtil.ANSWER_THROW_EXCEPTION;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


public class MockEIdLdapConnectionFactory extends EIdLdapConnectionFactory {

    private final Map<LdapSource, MockConnectionState> mockConnections = new HashMap<>();

    public MockEIdLdapConnectionFactory() {
        super(mock(SSLContext.class));
    }

    public LDAPConnection mockConnection(LdapSource ldapSource) {
        LDAPConnection connection = mock(LDAPConnection.class, ANSWER_THROW_EXCEPTION);
        MockConnectionState state = new MockConnectionState(ldapSource, connection);

        doAnswer(invocation -> {
            if (state.isClosed()) {
                fail("Connection to LDAP source " + state.getLdapSource() + " was already closed");
            }
            state.setClosed(true);
            return null;
        }).when(connection).close();
        mockConnections.put(ldapSource, state);
        return connection;
    }

    public void verifyNoOpenConnections() {
        for (MockConnectionState state : mockConnections.values()) {
            if (state.isOpened() && !state.isClosed()) {
                fail("Connection to LDAP source " + state.getLdapSource() + " was opened but never closed");
            }
        }
    }

    @Override
    public LDAPConnection connect(LdapSource ldapSource) {
        MockConnectionState mockConnectionState = mockConnections.get(ldapSource);
        if(mockConnectionState == null) {
            throw new IllegalStateException("Connection to LDAP source " + format(ldapSource) + " not mocked");
        }
        if (mockConnectionState.isOpened()) {
            throw new IllegalStateException("Connection to LDAP source " + format(ldapSource) + " already opened");
        }
        mockConnectionState.setOpened(true);
        return mockConnectionState.getMock();
    }

    private String format(LdapSource source) {
        return String.format("%s:%d", source.host(), source.port());
    }

    @Data
    @RequiredArgsConstructor
    private static class MockConnectionState {

        private final LdapSource ldapSource;
        private final LDAPConnection mock;
        private boolean isOpened = false;
        private boolean isClosed = false;

    }

}
