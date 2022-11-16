package ee.ria.tara.repository.model;

import com.fasterxml.jackson.databind.JsonNode;
import ee.ria.tara.repository.helper.JsonConverter;
import ee.ria.tara.repository.helper.StringListConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Boolean notifyClientsOnTaraLoginPage;
    @Convert(converter = JsonConverter.class)
    private JsonNode notificationTemplates;
    @Convert(converter = StringListConverter.class)
    private List<String> displayOnlyForAuthmethods;
    private Boolean notifyClientsByEmail;
    private OffsetDateTime sendAt;
    private String emailTemplate;
    @CreationTimestamp
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
