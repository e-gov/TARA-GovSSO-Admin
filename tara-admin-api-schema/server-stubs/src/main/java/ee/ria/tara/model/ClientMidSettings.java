package ee.ria.tara.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ClientMidSettings
 */

public class ClientMidSettings   {
  @JsonProperty("relying_party_UUID")
  private String relyingPartyUUID;

  @JsonProperty("relying_party_name")
  private String relyingPartyName;

  public ClientMidSettings relyingPartyUUID(String relyingPartyUUID) {
    this.relyingPartyUUID = relyingPartyUUID;
    return this;
  }

  /**
   * Get relyingPartyUUID
   * @return relyingPartyUUID
  */
  @ApiModelProperty(value = "")

@Size(max=512) 
  public String getRelyingPartyUUID() {
    return relyingPartyUUID;
  }

  public void setRelyingPartyUUID(String relyingPartyUUID) {
    this.relyingPartyUUID = relyingPartyUUID;
  }

  public ClientMidSettings relyingPartyName(String relyingPartyName) {
    this.relyingPartyName = relyingPartyName;
    return this;
  }

  /**
   * Get relyingPartyName
   * @return relyingPartyName
  */
  @ApiModelProperty(value = "")

@Size(max=512) 
  public String getRelyingPartyName() {
    return relyingPartyName;
  }

  public void setRelyingPartyName(String relyingPartyName) {
    this.relyingPartyName = relyingPartyName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientMidSettings clientMidSettings = (ClientMidSettings) o;
    return Objects.equals(this.relyingPartyUUID, clientMidSettings.relyingPartyUUID) &&
        Objects.equals(this.relyingPartyName, clientMidSettings.relyingPartyName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relyingPartyUUID, relyingPartyName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientMidSettings {\n");
    
    sb.append("    relyingPartyUUID: ").append(toIndentedString(relyingPartyUUID)).append("\n");
    sb.append("    relyingPartyName: ").append(toIndentedString(relyingPartyName)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

