package ee.ria.tara.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@Profile("exportFromCas")
@ConfigurationProperties("cas-export")
public class CasExportConfiguration {

    @NotNull
    private String dbUrl;
    @NotNull
    private String user;
    @NotNull
    private String password;

    private String getCasClientsSql = "select \t\n" +
            "\tr1.id as id, \n" +
            "\t(select lo_get(p2.property_values) from \n" +
            "\t \t\tregisteredserviceimpl_props p1 \t \t\n" +
            "\t \t\tjoin regexregisteredserviceproperty p2 on p2.id = p1.properties_id\n" +
            "\t \twhere p1.abstractregisteredservice_id = r1.id \n" +
            "\t \tand p1.properties_key = 'service.name') as servicename_et,\n" +
            "\t(select lo_get(p2.property_values) from \n" +
            "\t \t\tregisteredserviceimpl_props p1 \t \t\n" +
            "\t \t\tjoin regexregisteredserviceproperty p2 on p2.id = p1.properties_id\n" +
            "\t \twhere p1.abstractregisteredservice_id = r1.id \n" +
            "\t \tand p1.properties_key = 'service.name.en') as servicename_en,\n" +
            "\t(select lo_get(p2.property_values) from \n" +
            "\t \t\tregisteredserviceimpl_props p1 \t \t\n" +
            "\t \t\tjoin regexregisteredserviceproperty p2 on p2.id = p1.properties_id\n" +
            "\t \twhere p1.abstractregisteredservice_id = r1.id \n" +
            "\t \tand p1.properties_key = 'service.name.ru') as servicename_ru,\n" +
            "\t(select lo_get(p2.property_values) from \n" +
            "\t \t\tregisteredserviceimpl_props p1 \t \t\n" +
            "\t \t\tjoin regexregisteredserviceproperty p2 on p2.id = p1.properties_id\n" +
            "\t \twhere p1.abstractregisteredservice_id = r1.id \n" +
            "\t \tand p1.properties_key = 'service.shortName') as service_shortname_et,\n" +
            "\t(select lo_get(p2.property_values) from \n" +
            "\t \t\tregisteredserviceimpl_props p1 \t \t\n" +
            "\t \t\tjoin regexregisteredserviceproperty p2 on p2.id = p1.properties_id\n" +
            "\t \twhere p1.abstractregisteredservice_id = r1.id \n" +
            "\t \tand p1.properties_key = 'service.shortName.en') as service_shortname_en,\n" +
            "\t(select lo_get(p2.property_values) from \n" +
            "\t \t\tregisteredserviceimpl_props p1 \t \t\n" +
            "\t \t\tjoin regexregisteredserviceproperty p2 on p2.id = p1.properties_id\n" +
            "\t \twhere p1.abstractregisteredservice_id = r1.id \n" +
            "\t \tand p1.properties_key = 'service.shortName.ru') as service_shortname_ru,\n" +
            "\t\t(select string_agg( concat(c2.email, ';', c2.phone, ';', c2.name, ';' ,c2.department)  , '|') from registeredservice_contacts c1 \n" +
            "\t join registeredserviceimplcontact c2 on c2.id = c1.contacts_id\n" +
            "\t where c1.abstractregisteredservice_id = r1.id) as contacts,\n" +
            "\t'N/A' as institution,\n" +
            "\tr1.clientid as client_id,\n" +
            "\tr1.clientsecret as client_secret,\n" +
            "\tr1.serviceid as redirect_url,\n" +
            "\tr1.informationurl as legacy_return_url,\n" +
            "\tr1.description as description \n" +
            "from regexregisteredservice r1 where r1.expression_type in ('oidc', 'oauth')";
}
