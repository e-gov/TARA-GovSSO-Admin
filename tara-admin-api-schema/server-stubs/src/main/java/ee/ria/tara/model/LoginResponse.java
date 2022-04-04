package ee.ria.tara.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LoginResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class LoginResponse   {

  @JsonProperty("error")
  private String error;

  @JsonProperty("username")
  private String username;

  public LoginResponse error(String error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
  */
  
  @Schema(name = "error", required = false)
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public LoginResponse username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  
  @Schema(name = "username", required = false)
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginResponse loginResponse = (LoginResponse) o;
    return Objects.equals(this.error, loginResponse.error) &&
        Objects.equals(this.username, loginResponse.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(error, username);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginResponse {\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
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

