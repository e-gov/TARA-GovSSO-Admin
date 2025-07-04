/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.13.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ee.ria.tara.api;

import ee.ria.tara.model.Alert;
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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.13.0")
@Validated
@Tag(name = "alerts", description = "alerts regarding service SLA")
public interface AlertsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /alerts : Add new alert
     *
     * @param alert Client object that needs to be added (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "addAlert",
        summary = "Add new alert",
        tags = { "alerts" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Alert.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/alerts",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    default ResponseEntity<Alert> addAlert(
        @Parameter(name = "Alert", description = "Client object that needs to be added", required = true) @Valid @RequestBody Alert alert
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"start_time\" : \"2019-08-24T14:15:22Z\", \"updated_at\" : \"2019-08-24T14:15:22Z\", \"end_time\" : \"2019-08-24T14:15:22Z\", \"email_alert\" : { \"send_at\" : \"2019-08-24T14:15:22Z\", \"message_templates\" : [ { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" }, { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" } ], \"enabled\" : true }, \"created_at\" : \"2019-08-24T14:15:22Z\", \"id\" : \"1234567\", \"title\" : \"Plaaniline katkestus SK teenustes\", \"login_alert\" : { \"auth_methods\" : [ \"idcard\", \"idcard\" ], \"message_templates\" : [ { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" }, { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" } ], \"enabled\" : true } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /alerts/{alert_id} : Delete existing alert
     *
     * @param alertId Alert ID (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "deleteAlert",
        summary = "Delete existing alert",
        tags = { "alerts" },
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
        value = "/alerts/{alert_id}"
    )
    
    default ResponseEntity<Void> deleteAlert(
        @Parameter(name = "alert_id", description = "Alert ID", required = true, in = ParameterIn.PATH) @PathVariable("alert_id") String alertId
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /alerts : Get all registered alerts
     *
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "getAllAlerts",
        summary = "Get all registered alerts",
        tags = { "alerts" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Alert.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/alerts",
        produces = { "application/json" }
    )
    
    default ResponseEntity<List<Alert>> getAllAlerts(
        
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"start_time\" : \"2019-08-24T14:15:22Z\", \"updated_at\" : \"2019-08-24T14:15:22Z\", \"end_time\" : \"2019-08-24T14:15:22Z\", \"email_alert\" : { \"send_at\" : \"2019-08-24T14:15:22Z\", \"message_templates\" : [ { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" }, { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" } ], \"enabled\" : true }, \"created_at\" : \"2019-08-24T14:15:22Z\", \"id\" : \"1234567\", \"title\" : \"Plaaniline katkestus SK teenustes\", \"login_alert\" : { \"auth_methods\" : [ \"idcard\", \"idcard\" ], \"message_templates\" : [ { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" }, { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" } ], \"enabled\" : true } }, { \"start_time\" : \"2019-08-24T14:15:22Z\", \"updated_at\" : \"2019-08-24T14:15:22Z\", \"end_time\" : \"2019-08-24T14:15:22Z\", \"email_alert\" : { \"send_at\" : \"2019-08-24T14:15:22Z\", \"message_templates\" : [ { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" }, { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" } ], \"enabled\" : true }, \"created_at\" : \"2019-08-24T14:15:22Z\", \"id\" : \"1234567\", \"title\" : \"Plaaniline katkestus SK teenustes\", \"login_alert\" : { \"auth_methods\" : [ \"idcard\", \"idcard\" ], \"message_templates\" : [ { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" }, { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" } ], \"enabled\" : true } } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /alerts/{alert_id} : Update existing alert
     *
     * @param alertId Alert ID (required)
     * @param alert Client object that needs to be added (required)
     * @return Successful operation (status code 200)
     *         or Invalid input (status code 400)
     */
    @Operation(
        operationId = "updateAlert",
        summary = "Update existing alert",
        tags = { "alerts" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Alert.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/alerts/{alert_id}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    default ResponseEntity<Alert> updateAlert(
        @Parameter(name = "alert_id", description = "Alert ID", required = true, in = ParameterIn.PATH) @PathVariable("alert_id") String alertId,
        @Parameter(name = "Alert", description = "Client object that needs to be added", required = true) @Valid @RequestBody Alert alert
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"start_time\" : \"2019-08-24T14:15:22Z\", \"updated_at\" : \"2019-08-24T14:15:22Z\", \"end_time\" : \"2019-08-24T14:15:22Z\", \"email_alert\" : { \"send_at\" : \"2019-08-24T14:15:22Z\", \"message_templates\" : [ { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" }, { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" } ], \"enabled\" : true }, \"created_at\" : \"2019-08-24T14:15:22Z\", \"id\" : \"1234567\", \"title\" : \"Plaaniline katkestus SK teenustes\", \"login_alert\" : { \"auth_methods\" : [ \"idcard\", \"idcard\" ], \"message_templates\" : [ { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" }, { \"message\" : \"Seoses SK plaaniliste hooldustöödega on Mobiil-ID teenuste kasutamine häiritud vahemikus 12.01.2020 00:00 kuni 13.01.2020 01:00\", \"locale\" : \"et\" } ], \"enabled\" : true } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
