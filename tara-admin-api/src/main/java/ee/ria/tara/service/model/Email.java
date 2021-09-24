package ee.ria.tara.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.core.io.ByteArrayResource;

import static java.util.Objects.requireNonNull;

@Getter
@Builder
public class Email {

    @NonNull
    private From from;
    @NonNull
    private String to;
    @NonNull
    private String subject;
    @NonNull
    private String html;
    @NonNull
    private Attachment attachment;

    @Getter
    public static class Attachment {
        private String attachmentFileName;
        private ByteArrayResource attachmentFile;

        private Attachment(String attachmentFileName, byte[] attachmentFile) {
            this.attachmentFileName = requireNonNull(attachmentFileName);
            this.attachmentFile = new ByteArrayResource(requireNonNull(attachmentFile));
        }
    }

    @Getter
    public static class From {
        private String email;
        private String name;

        private From(String email, String name) {
            this.email = requireNonNull(email);
            this.name = requireNonNull(name);
        }
    }

    //used to override default setter of attachment for lombok builder
    public static class EmailBuilder {
        private Attachment attachment;
        private From from;

        public EmailBuilder attachment(String attachmentFileName, byte[] attachmentFile) {
            this.attachment = new Attachment(attachmentFileName, attachmentFile);
            return this;
        }

        public EmailBuilder from(String email, String name) {
            this.from = new From(email, name);
            return this;
        }
    }
}
