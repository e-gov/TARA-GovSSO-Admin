/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.0.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ee.ria.tara.api;

import ee.ria.tara.model.WhoAmIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "whoami", description = "the whoami API")
public interface WhoamiApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /whoami : Check if user is already logged in
     * The method is used to check if user is logged in
     *
     * @return Success (status code 200)
     */
    @Operation(
        operationId = "whoAmI",
        summary = "Check if user is already logged in",
        tags = { "user" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = WhoAmIResponse.class))
            })
        },
        security = {
            @SecurityRequirement(name = "cookieAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/whoami",
        produces = { "application/json" }
    )
    default ResponseEntity<WhoAmIResponse> whoAmI(
        
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"username\" : \"username\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
