package ee.ria.tara.repository.model;

import ee.ria.tara.model.InstitutionType;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;


@Entity
@Data
public class Institution implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true, name="registry_code")
    private String registryCode;
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "institution")
    @ToString.Exclude
    private List<Client> clients;
    private String contactAddress;
    private String contactPhone;
    private String contactEmail;
    @Enumerated(EnumType.STRING)
    private InstitutionType.TypeEnum type;
    private String billingEmail;
    @CreationTimestamp
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
