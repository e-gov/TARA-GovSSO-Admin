package ee.ria.tara.service.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import ee.ria.tara.model.Client;
import lombok.NonNull;

import java.util.StringJoiner;

public record ClientImportItem(
        @NonNull @JsonUnwrapped Client client,
        String secret
) {

    @Override
    public String toString() {
        String maskedSecret = secret != null ? "[non-null value]" : "null";
        return new StringJoiner(", ", ClientImportItem.class.getSimpleName() + "[", "]")
                .add("client=" + client)
                .add("secret='" + maskedSecret + "'")
                .toString();
    }
}
