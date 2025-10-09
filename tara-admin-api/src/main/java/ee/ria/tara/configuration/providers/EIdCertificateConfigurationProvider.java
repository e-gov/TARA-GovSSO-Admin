package ee.ria.tara.configuration.providers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("tara.admin.eid-certificates")
@Valid
@Builder
public record EIdCertificateConfigurationProvider(
        @NotEmpty @Singular List<LdapSource> ldapSources
) {
    public record LdapSource(
            @NonNull @NotBlank String host,
            @NonNull @Positive Integer port,
            @NonNull String baseDn
    ) {}
}
