package ee.ria.tara.util;

import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.HOURS;

@UtilityClass
public class EidCertificateTestUtil {

    private static final KeyPairGenerator KEY_PAIR_GENERATOR;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KEY_PAIR_GENERATOR = KeyPairGenerator.getInstance("EC", "BC");
            KEY_PAIR_GENERATOR.initialize(new ECGenParameterSpec("secp384r1"), SECURE_RANDOM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static X509Certificate createCertificate(CertificateParams params) {
        KeyPair keyPair = KEY_PAIR_GENERATOR.generateKeyPair();
        Instant now = Instant.now();
        X500Name subject = new X500NameBuilder()
                .addRDN(BCStyle.C, "EE")
                .addRDN(BCStyle.SURNAME, params.surname())
                .addRDN(BCStyle.GIVENNAME, params.givenName())
                .addRDN(BCStyle.SERIALNUMBER, "PNOEE-" + params.idCode())
                .addRDN(BCStyle.CN,
                        String.format("\"%s,%s,%s\"", params.surname(), params.givenName(), params.idCode()))
                .build();
        BigInteger serial = BigInteger.valueOf(SECURE_RANDOM.nextLong());
        X500Name issuer = new X500NameBuilder()
                .addRDN(BCStyle.C, "EE")
                .addRDN(BCStyle.O, "OÜ Certificates Inc.")
                .addRDN(BCStyle.CN, "ESTEID9999")
                .build();
        ContentSigner signer = new JcaContentSignerBuilder("SHA384withECDSA")
                .setProvider("BC")
                .build(keyPair.getPrivate());
        PolicyInformation policyInformation = new PolicyInformation(new ASN1ObjectIdentifier(params.policyIdentifier()));
        X509CertificateHolder certificateHolder = new JcaX509v3CertificateBuilder(
                issuer, serial, Date.from(now), Date.from(now.plus(1, HOURS)), subject, keyPair.getPublic()
        )
                .addExtension(Extension.certificatePolicies, false, new CertificatePolicies(policyInformation))
                .addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyEncipherment))
                .build(signer);
        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certificateHolder);

    }

    public static String simplifiedDescription(X509Certificate certificate) {
        byte[] serialNumberBytes = certificate.getSerialNumber().toByteArray();
        return String.format("%s (Serial number: %s)",
                certificate.getSubjectX500Principal().toString(),
                new String(Hex.encode(serialNumberBytes)));
    }

    @Builder
    @Value
    @Accessors(fluent = true)
    public static class CertificateParams {
        @NonNull String givenName = "MARY ÄNN";
        @NonNull String surname = "O’CONNEŽ-ŠUSLIK";
        @NonNull String idCode;
        @NonNull String policyIdentifier;
    }

}
