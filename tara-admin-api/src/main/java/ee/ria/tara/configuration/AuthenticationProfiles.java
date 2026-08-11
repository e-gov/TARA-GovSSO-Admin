package ee.ria.tara.configuration;

public final class AuthenticationProfiles {

    public static final String IN_MEMORY_AUTH = "inMemoryAuth";
    public static final String OIDC_AUTH = "oidcAuth";
    public static final String LDAP_AUTH = "!" + IN_MEMORY_AUTH + " & !" + OIDC_AUTH;

    private AuthenticationProfiles() {
    }
}
