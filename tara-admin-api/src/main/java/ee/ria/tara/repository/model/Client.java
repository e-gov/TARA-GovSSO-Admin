package ee.ria.tara.repository.model;

import ee.ria.tara.repository.helper.StringListConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clientId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_registry_code", referencedColumnName = "registry_code")
    private Institution institution;
    // Only stored here to check and ensure uniqueness
    private String eidasRequesterId;
    // Host and port parts from HydraClient.backchannelLogoutUri duplicated here for read-only consuming by outproxy.
    private String backchannelLogoutHostAndPort;
    private String description;
    @Convert(converter = StringListConverter.class)
    private List<String> infoNotificationEmails;
    @Convert(converter = StringListConverter.class)
    private List<String> slaNotificationEmails;
    @Convert(converter = StringListConverter.class)
    private List<String> tokenRequestAllowedIpAddresses;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "client")
    private List<ClientContact> clientContacts;
    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
