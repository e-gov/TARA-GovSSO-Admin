/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.0.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ee.ria.tara.api;

import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientImportResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "clients", description = "OpenID Connect client details")
public interface ClientsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /clients : Get all registered clients
     *
     * @param filterBy Filters search results by client_id or name (optional)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "getAllClients",
        summary = "Get all registered clients",
        tags = { "clients" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Client.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/clients",
        produces = { "application/json" }
    )
    default ResponseEntity<List<Client>> getAllClients(
        @Parameter(name = "filter_by", description = "Filters search results by client_id or name", in = ParameterIn.QUERY) @Valid @RequestParam(value = "filter_by", required = false) String filterBy
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"client_url\" : \"https://client.example.com/\", \"post_logout_redirect_uris\" : [ \"https://logout-redirect-uri.test.ee/callback\", \"https://logout-redirect-uri.test.ee/callback\" ], \"institution_metainfo\" : { \"name\" : \"Example Institution\", \"type\" : { \"type\" : \"public\" }, \"registry_code\" : \"12345678\" }, \"skip_user_consent_client_ids\" : [ \"openIdDemo\", \"openIdDemo\" ], \"smartid_settings\" : { \"should_use_additional_verification_code_check\" : true, \"relying_party_name\" : \"relying_party_name\", \"relying_party_UUID\" : \"relying_party_UUID\" }, \"description\" : \"description\", \"sla_notification_emails\" : [ \"katkestused@test.ee\", \"katkestused@test.ee\" ], \"created_at\" : \"2019-08-24T14:15:22Z\", \"redirect_uris\" : [ \"https://redirect-uri.test.ee/callback\", \"https://redirect-uri.test.ee/callback\" ], \"secret\" : \"secret\", \"client_id\" : \"openIdDemo\", \"is_user_consent_required\" : false, \"eidas_requester_id\" : \"urn:uuid:33ca0ae1-a5fb-4885-80d7-6af6bf6e0e5f\", \"mid_settings\" : { \"relying_party_name\" : \"relying_party_name\", \"relying_party_UUID\" : \"relying_party_UUID\" }, \"client_secret_export_settings\" : { \"recipient_id_code\" : \"60001019906\", \"recipient_name_in_ldap\" : \"Mari-Liis Männik\", \"recipient_email\" : \"60001019906@eesti.ee\" }, \"updated_at\" : \"2019-08-24T14:15:22Z\", \"client_short_name\" : { \"ru\" : \"ru\", \"en\" : \"en\", \"et\" : \"et\" }, \"scope\" : [ \"scope\", \"scope\" ], \"client_contacts\" : [ { \"phone\" : \"+3726630200\", \"name\" : \"test\", \"department\" : \"test\", \"email\" : \"test@example.com\" }, { \"phone\" : \"+3726630200\", \"name\" : \"test\", \"department\" : \"test\", \"email\" : \"test@example.com\" } ], \"id\" : \"id\", \"backchannel_logout_uri\" : \"https://example.com/\", \"client_name\" : { \"ru\" : \"ru\", \"en\" : \"en\", \"et\" : \"et\" }, \"client_logo\" : \"client_logo\", \"info_notification_emails\" : [ \"teavitused@test.ee\", \"teavitused@test.ee\" ] }, { \"client_url\" : \"https://client.example.com/\", \"post_logout_redirect_uris\" : [ \"https://logout-redirect-uri.test.ee/callback\", \"https://logout-redirect-uri.test.ee/callback\" ], \"institution_metainfo\" : { \"name\" : \"Example Institution\", \"type\" : { \"type\" : \"public\" }, \"registry_code\" : \"12345678\" }, \"skip_user_consent_client_ids\" : [ \"openIdDemo\", \"openIdDemo\" ], \"smartid_settings\" : { \"should_use_additional_verification_code_check\" : true, \"relying_party_name\" : \"relying_party_name\", \"relying_party_UUID\" : \"relying_party_UUID\" }, \"description\" : \"description\", \"sla_notification_emails\" : [ \"katkestused@test.ee\", \"katkestused@test.ee\" ], \"created_at\" : \"2019-08-24T14:15:22Z\", \"redirect_uris\" : [ \"https://redirect-uri.test.ee/callback\", \"https://redirect-uri.test.ee/callback\" ], \"secret\" : \"secret\", \"client_id\" : \"openIdDemo\", \"is_user_consent_required\" : false, \"eidas_requester_id\" : \"urn:uuid:33ca0ae1-a5fb-4885-80d7-6af6bf6e0e5f\", \"mid_settings\" : { \"relying_party_name\" : \"relying_party_name\", \"relying_party_UUID\" : \"relying_party_UUID\" }, \"client_secret_export_settings\" : { \"recipient_id_code\" : \"60001019906\", \"recipient_name_in_ldap\" : \"Mari-Liis Männik\", \"recipient_email\" : \"60001019906@eesti.ee\" }, \"updated_at\" : \"2019-08-24T14:15:22Z\", \"client_short_name\" : { \"ru\" : \"ru\", \"en\" : \"en\", \"et\" : \"et\" }, \"scope\" : [ \"scope\", \"scope\" ], \"client_contacts\" : [ { \"phone\" : \"+3726630200\", \"name\" : \"test\", \"department\" : \"test\", \"email\" : \"test@example.com\" }, { \"phone\" : \"+3726630200\", \"name\" : \"test\", \"department\" : \"test\", \"email\" : \"test@example.com\" } ], \"id\" : \"id\", \"backchannel_logout_uri\" : \"https://example.com/\", \"client_name\" : { \"ru\" : \"ru\", \"en\" : \"en\", \"et\" : \"et\" }, \"client_logo\" : \"client_logo\", \"info_notification_emails\" : [ \"teavitused@test.ee\", \"teavitused@test.ee\" ] } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /clients/import : Import clients
     *
     * @param file  (optional)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "importClients",
        summary = "Import clients",
        tags = { "clients" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ClientImportResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/clients/import",
        produces = { "application/json" },
        consumes = { "multipart/form-data" }
    )
    default ResponseEntity<ClientImportResponse> importClients(
        @Parameter(name = "file", description = "") @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"clients_import_success_count\" : 6, \"clients_count\" : 0, \"clients_import_failed_count\" : 1, \"clients_not_imported\" : [ \"clients_not_imported\", \"clients_not_imported\" ], \"status\" : \"status\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
