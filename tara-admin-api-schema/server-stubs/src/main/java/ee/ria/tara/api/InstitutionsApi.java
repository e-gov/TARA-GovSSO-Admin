/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.5.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ee.ria.tara.api;

import ee.ria.tara.model.Client;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.5.0")
@Validated
@Tag(name = "clients", description = "OpenID Connect client details")
public interface InstitutionsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /institutions/{registry_code}/clients : Add new client for existing institution
     *
     * @param registryCode Registry code of the institution (required)
     * @param client Client object that needs to be added (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "addClientToInstitution",
        summary = "Add new client for existing institution",
        tags = { "clients" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/institutions/{registry_code}/clients",
        consumes = { "application/json" }
    )
    
    default ResponseEntity<Void> addClientToInstitution(
        @Parameter(name = "registry_code", description = "Registry code of the institution", required = true, in = ParameterIn.PATH) @PathVariable("registry_code") String registryCode,
        @Parameter(name = "Client", description = "Client object that needs to be added", required = true) @Valid @RequestBody Client client
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /institutions : Add a new institution
     *
     * @param institution Institution object that needs to be added (required)
     * @return Invalid input (status code 405)
     */
    @Operation(
        operationId = "addInstitution",
        summary = "Add a new institution",
        tags = { "institutions" },
        responses = {
            @ApiResponse(responseCode = "405", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/institutions",
        consumes = { "application/json" }
    )
    
    default ResponseEntity<Void> addInstitution(
        @Parameter(name = "Institution", description = "Institution object that needs to be added", required = true) @Valid @RequestBody Institution institution
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /institutions/{registry_code}/clients/{client_id} : Delete existing client for given institution
     *
     * @param registryCode Registry code of the institution (required)
     * @param clientId OpenID Connect client_id (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "deleteClient",
        summary = "Delete existing client for given institution",
        tags = { "clients" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/institutions/{registry_code}/clients/{client_id}"
    )
    
    default ResponseEntity<Void> deleteClient(
        @Parameter(name = "registry_code", description = "Registry code of the institution", required = true, in = ParameterIn.PATH) @PathVariable("registry_code") String registryCode,
        @Parameter(name = "client_id", description = "OpenID Connect client_id", required = true, in = ParameterIn.PATH) @PathVariable("client_id") String clientId
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /institutions/{registry_code} : Deletes an existing institution
     *
     * @param registryCode Registry code of the institution (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "deleteInstitution",
        summary = "Deletes an existing institution",
        tags = { "institutions" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/institutions/{registry_code}"
    )
    
    default ResponseEntity<Void> deleteInstitution(
        @Parameter(name = "registry_code", description = "Registry code of the institution", required = true, in = ParameterIn.PATH) @PathVariable("registry_code") String registryCode
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /institutions/{registry_code}/clients : Get all registered clients for existing institution
     *
     * @param registryCode Registry code of the institution (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "getAllInstitutionClients",
        summary = "Get all registered clients for existing institution",
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
        value = "/institutions/{registry_code}/clients",
        produces = { "application/json" }
    )
    
    default ResponseEntity<List<Client>> getAllInstitutionClients(
        @Parameter(name = "registry_code", description = "Registry code of the institution", required = true, in = ParameterIn.PATH) @PathVariable("registry_code") String registryCode
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"client_url\" : \"https://client.example.com/\", \"skip_user_consent_client_ids\" : [ \"openIdDemo\", \"openIdDemo\" ], \"description\" : \"description\", \"created_at\" : \"2019-08-24T14:15:22Z\", \"secret\" : \"secret\", \"client_id\" : \"openIdDemo\", \"token_endpoint_auth_method\" : \"client_secret_basic\", \"client_secret_export_settings\" : { \"recipient_id_code\" : \"60001019906\", \"recipient_name_in_ldap\" : \"Mari-Liis Männik\", \"recipient_email\" : \"60001019906@eesti.ee\" }, \"updated_at\" : \"2019-08-24T14:15:22Z\", \"client_short_name\" : { \"ru\" : \"ru\", \"en\" : \"en\", \"et\" : \"et\" }, \"scope\" : [ \"scope\", \"scope\" ], \"token_request_allowed_ip_addresses\" : [ \"token_request_allowed_ip_addresses\", \"token_request_allowed_ip_addresses\" ], \"client_contacts\" : [ { \"phone\" : \"+3726630200\", \"name\" : \"test\", \"department\" : \"test\", \"email\" : \"test@example.com\" }, { \"phone\" : \"+3726630200\", \"name\" : \"test\", \"department\" : \"test\", \"email\" : \"test@example.com\" } ], \"id\" : \"id\", \"backchannel_logout_uri\" : \"https://example.com/\", \"client_name\" : { \"ru\" : \"ru\", \"en\" : \"en\", \"et\" : \"et\" }, \"post_logout_redirect_uris\" : [ \"https://logout-redirect-uri.test.ee/callback\", \"https://logout-redirect-uri.test.ee/callback\" ], \"institution_metainfo\" : { \"name\" : \"Example Institution\", \"type\" : { \"type\" : \"public\" }, \"registry_code\" : \"12345678\" }, \"smartid_settings\" : { \"should_use_additional_verification_code_check\" : true, \"relying_party_name\" : \"relying_party_name\", \"relying_party_UUID\" : \"relying_party_UUID\" }, \"sla_notification_emails\" : [ \"katkestused@test.ee\", \"katkestused@test.ee\" ], \"redirect_uris\" : [ \"https://redirect-uri.test.ee/callback\", \"https://redirect-uri.test.ee/callback\" ], \"is_user_consent_required\" : false, \"eidas_requester_id\" : \"urn:uuid:33ca0ae1-a5fb-4885-80d7-6af6bf6e0e5f\", \"mid_settings\" : { \"relying_party_name\" : \"relying_party_name\", \"relying_party_UUID\" : \"relying_party_UUID\" }, \"client_logo\" : \"client_logo\", \"info_notification_emails\" : [ \"teavitused@test.ee\", \"teavitused@test.ee\" ] }, { \"client_url\" : \"https://client.example.com/\", \"skip_user_consent_client_ids\" : [ \"openIdDemo\", \"openIdDemo\" ], \"description\" : \"description\", \"created_at\" : \"2019-08-24T14:15:22Z\", \"secret\" : \"secret\", \"client_id\" : \"openIdDemo\", \"token_endpoint_auth_method\" : \"client_secret_basic\", \"client_secret_export_settings\" : { \"recipient_id_code\" : \"60001019906\", \"recipient_name_in_ldap\" : \"Mari-Liis Männik\", \"recipient_email\" : \"60001019906@eesti.ee\" }, \"updated_at\" : \"2019-08-24T14:15:22Z\", \"client_short_name\" : { \"ru\" : \"ru\", \"en\" : \"en\", \"et\" : \"et\" }, \"scope\" : [ \"scope\", \"scope\" ], \"token_request_allowed_ip_addresses\" : [ \"token_request_allowed_ip_addresses\", \"token_request_allowed_ip_addresses\" ], \"client_contacts\" : [ { \"phone\" : \"+3726630200\", \"name\" : \"test\", \"department\" : \"test\", \"email\" : \"test@example.com\" }, { \"phone\" : \"+3726630200\", \"name\" : \"test\", \"department\" : \"test\", \"email\" : \"test@example.com\" } ], \"id\" : \"id\", \"backchannel_logout_uri\" : \"https://example.com/\", \"client_name\" : { \"ru\" : \"ru\", \"en\" : \"en\", \"et\" : \"et\" }, \"post_logout_redirect_uris\" : [ \"https://logout-redirect-uri.test.ee/callback\", \"https://logout-redirect-uri.test.ee/callback\" ], \"institution_metainfo\" : { \"name\" : \"Example Institution\", \"type\" : { \"type\" : \"public\" }, \"registry_code\" : \"12345678\" }, \"smartid_settings\" : { \"should_use_additional_verification_code_check\" : true, \"relying_party_name\" : \"relying_party_name\", \"relying_party_UUID\" : \"relying_party_UUID\" }, \"sla_notification_emails\" : [ \"katkestused@test.ee\", \"katkestused@test.ee\" ], \"redirect_uris\" : [ \"https://redirect-uri.test.ee/callback\", \"https://redirect-uri.test.ee/callback\" ], \"is_user_consent_required\" : false, \"eidas_requester_id\" : \"urn:uuid:33ca0ae1-a5fb-4885-80d7-6af6bf6e0e5f\", \"mid_settings\" : { \"relying_party_name\" : \"relying_party_name\", \"relying_party_UUID\" : \"relying_party_UUID\" }, \"client_logo\" : \"client_logo\", \"info_notification_emails\" : [ \"teavitused@test.ee\", \"teavitused@test.ee\" ] } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /institutions : Get list of all institutions
     *
     * @param filterBy Filters search results by name or registry code (optional)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     *         or Internal server error (status code 500)
     */
    @Operation(
        operationId = "getAllInstitutions",
        summary = "Get list of all institutions",
        tags = { "institutions" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Institution.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/institutions",
        produces = { "application/json" }
    )
    
    default ResponseEntity<List<Institution>> getAllInstitutions(
        @Parameter(name = "filter_by", description = "Filters search results by name or registry code", in = ParameterIn.QUERY) @Valid @RequestParam(value = "filter_by", required = false) String filterBy
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"address\" : \"Test st 123\", \"billing_settings\" : { \"email\" : \"email\" }, \"updated_at\" : \"2019-08-24T14:15:22Z\", \"phone\" : \"+3726630200\", \"name\" : \"Example Institution\", \"created_at\" : \"2019-08-24T14:15:22Z\", \"id\" : \"id\", \"type\" : { \"type\" : \"public\" }, \"registry_code\" : \"12345678\", \"client_ids\" : [ \"openIdDemo\", \"openIdDemo\" ], \"email\" : \"info@example.com\" }, { \"address\" : \"Test st 123\", \"billing_settings\" : { \"email\" : \"email\" }, \"updated_at\" : \"2019-08-24T14:15:22Z\", \"phone\" : \"+3726630200\", \"name\" : \"Example Institution\", \"created_at\" : \"2019-08-24T14:15:22Z\", \"id\" : \"id\", \"type\" : { \"type\" : \"public\" }, \"registry_code\" : \"12345678\", \"client_ids\" : [ \"openIdDemo\", \"openIdDemo\" ], \"email\" : \"info@example.com\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /institutions/metainfo : Get list of existing institutions and their types.
     *
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "getInstitutionMetainfo",
        summary = "Get list of existing institutions and their types.",
        tags = { "institutions" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InstitutionMetainfo.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/institutions/metainfo",
        produces = { "application/json" }
    )
    
    default ResponseEntity<List<InstitutionMetainfo>> getInstitutionMetainfo(
        
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"name\" : \"Example Institution\", \"type\" : { \"type\" : \"public\" }, \"registry_code\" : \"12345678\" }, { \"name\" : \"Example Institution\", \"type\" : { \"type\" : \"public\" }, \"registry_code\" : \"12345678\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /institutions/{registry_code}/clients/{client_id} : Update existing client for existing institution
     *
     * @param registryCode Registry code of the institution (required)
     * @param clientId OpenID Connect client_id (required)
     * @param client Client object that needs to be added (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "updateClient",
        summary = "Update existing client for existing institution",
        tags = { "clients" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/institutions/{registry_code}/clients/{client_id}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    default ResponseEntity<String> updateClient(
        @Parameter(name = "registry_code", description = "Registry code of the institution", required = true, in = ParameterIn.PATH) @PathVariable("registry_code") String registryCode,
        @Parameter(name = "client_id", description = "OpenID Connect client_id", required = true, in = ParameterIn.PATH) @PathVariable("client_id") String clientId,
        @Parameter(name = "Client", description = "Client object that needs to be added", required = true) @Valid @RequestBody Client client
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /institutions/{registry_code} : Update an existing institution
     *
     * @param registryCode Registry code of the institution (required)
     * @param institution Institution object that needs to be updated (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "updateInstitution",
        summary = "Update an existing institution",
        tags = { "institutions" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/institutions/{registry_code}",
        consumes = { "application/json" }
    )
    
    default ResponseEntity<Void> updateInstitution(
        @Parameter(name = "registry_code", description = "Registry code of the institution", required = true, in = ParameterIn.PATH) @PathVariable("registry_code") String registryCode,
        @Parameter(name = "Institution", description = "Institution object that needs to be updated", required = true) @Valid @RequestBody Institution institution
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
