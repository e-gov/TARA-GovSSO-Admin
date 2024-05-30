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

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Column(updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
