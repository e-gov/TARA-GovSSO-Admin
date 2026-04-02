package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ClientMetaData
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.15.0")
public class ClientMetaData {

  /**
   * Gets or Sets clientType
   */
  public enum ClientTypeEnum {
    DEFAULT("DEFAULT"),
    
    SECURED_APP("SECURED_APP");

    private final String value;

    ClientTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ClientTypeEnum fromValue(String value) {
      for (ClientTypeEnum b : ClientTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ClientTypeEnum clientType = ClientTypeEnum.DEFAULT;

  public ClientMetaData clientType(ClientTypeEnum clientType) {
    this.clientType = clientType;
    return this;
  }

  /**
   * Get clientType
   * @return clientType
   */
  
  @Schema(name = "client_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_type")
  public ClientTypeEnum getClientType() {
    return clientType;
  }

  public void setClientType(ClientTypeEnum clientType) {
    this.clientType = clientType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientMetaData clientMetaData = (ClientMetaData) o;
    return Objects.equals(this.clientType, clientMetaData.clientType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientMetaData {\n");
    sb.append("    clientType: ").append(toIndentedString(clientType)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

