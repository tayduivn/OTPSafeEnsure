package com.itrus.ikey.safecenter.safecenter;

import com.itrus.ikey.safecenter.TOPMFA.utils.Validator;
import com.itrus.raapi.implement.Des3Util;
import com.leo.gesturelibray.crypto.Base64;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String password = "panguoxing123";
        String random = "1";
        String iv = "12345678";

        if (iv.length() > 8) {
            iv = iv.substring(0, 8);
        } else {
            for (int i = iv.length(); i < 8; i++) {
                iv += "0";
            }
        }

        System.out.println(iv);

        String key = Validator.hexSh1Encoding(random.getBytes());

        byte[] enc_pwd_byte = Des3Util.des3EncodeCBC(key.getBytes(), iv.getBytes(), password.getBytes());
        String b64_enc_pwd = Base64.encode(enc_pwd_byte);
        System.out.printf("%s\n", b64_enc_pwd);

        byte[] dec_pwd_byte = Des3Util.des3DecodeCBC(key.getBytes(), iv.getBytes(), enc_pwd_byte);
        String dec_pwd = new String(dec_pwd_byte);
        System.out.println(dec_pwd);


    }

    /**
     * Ikey public key verify Signature
     *
     * @throws Exception
     */
    @Test
    public void testIkey() {
        String certStr = "MIICYTCCAgagAwIBAgIUEfKdBkAhrZ3u+br1US+E2vX9xzIwDAYIKoEcz1UBg3UFADCBiTEvMC0GA1UEAwwm5aSp5aiB6K+a5L+hUk9PQS1TTTLor4HkuabvvIjmtYvor5XvvIkxJjAkBgNVBAsMHei/kOe7tOmDqFJPT0EtU00y77yI5rWL6K+V77yJMSEwHwYDVQQKDBjlpKnlqIHor5rkv6HvvIjmtYvor5XvvIkxCzAJBgNVBAYTAkNOMB4XDTE2MTEwMTA5MTA1MFoXDTE2MTIwMTA5MTA1MFowMTENMAsGA1UEAwwEdXNlcjEPMA0GA1UECwwGWWFuc2hpMQ8wDQYDVQQKDAZZYW5zaGkwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBALYg7SJsE4nyqyLewkwI66ycHXgY1PrmlCJcg+kHHW8xpFitKWmRII63PuCdv3WcW+Bzup3jaWfjuC3oKXtyorVpAtu2uwtxMTZ/NafS3c/f187qCwbQcwborW0Fe/OOcgJdmrOQX3NEfwL9FbW4kboaw+X+XGzQ3/J/FWGtufhzAgMBAAGjWjBYMAkGA1UdEwQCMAAwCwYDVR0PBAQDAgbAMB8GA1UdIwQYMBaAFKFGVzjz9O7+5qTGAEd5SEYn3JsaMB0GA1UdDgQWBBTL97lhY86yPlFdb/NmTnyn3YX4MTAMBggqgRzPVQGDdQUAA0cAMEQCID6W+9dx7ihm/ZCVGTD7XnTwybpnXc8pebJtOcpROeuqAiDf8gq+vSt7TdkBX06EOYH/BEnciwidCqdptJqeadOapA==";
        String originData = "test_test_test_test_test_test_test_test_test_test_test_test_test_test_test_test_";
        String signData = "jNVd/XHJxDb/kHpo+M/N5EHHIKFU05rc6+QYEFW42hNYmyXZ+NwuNefJu7Q328W72QnV7lcjAURJRQuhsbx/yTtDsuILC4MNx0R8+/PSTGf3rw/TCxEeO9WsdYAHzrftY/OTB7v9kl1R879CDDKfyk8z2HJ22U/D8o7+kdhmpG4=";
        try {
            CertificateFactory cft = CertificateFactory.getInstance("X.509");
            Certificate cert = cft.generateCertificate(new ByteArrayInputStream(Base64.decode(certStr)));

            Signature sign = Signature.getInstance("SHA1WithRSA");
            sign.initVerify(cert);
            sign.update(originData.getBytes());
            Boolean falg = sign.verify(Base64.decode(signData));
            System.out.println(falg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Ukey public key verify Signature
     *
     * @throws Exception
     */
    @Test
    public void testUkey() throws Exception {
        String certStr = "MIIDnzCCAoegAwIBAgIUZDTu7DDMSmgrOKFvce2rJdbCxGYwDQYJKoZIhvcNAQEFBQAwazEYMBYGA1UEAwwPUGVuZyBDaGFuZ3hpb25nMRgwFgYDVQQLDA9IYW5nemhvdSBPZmZpY2UxEzARBgNVBAoMCmlUcnVzQ2hpbmExEzARBgNVBAgMClNvbWUtU3RhdGUxCzAJBgNVBAYTAkNOMB4XDTE2MTAzMTEzMDEzNFoXDTE2MTEwMTEzMDEzNFowgYUxFTATBgNVBA0MDOWuieWFqOivgeS5pjEZMBcGA1UEAAwQT3JnYW5pemF0aW9uQ29kZTELMAkGA1UEBhMCQ04xEjAQBgNVBAUTCTQ4MTY3MTM4MTEwMC4GA1UEAwwn5YyX5Lqs6Zu3562W5rab56eR5oqA6IKh5Lu95pyJ6ZmQ5YWs5Y+4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx/eXIsNNqdE70SrShxPdpHk3rb1w5GGWqiFx70xbp17Ak4ut7TD1z8A0zER0rx5UzLrTFgbKbvA1A7+QUVxkOT0VIjn+CiiJvLdBArflyHep2hrWf1GhRr1LWL7JZy2FiyAVbNQyd2dOgj533rcQ9ByF0IaeLLqGXFqtT+8+A8FHJtS55OxRwPJ/iEW97nbj++fzYZ8JDAQaiplB0JZP2OG2p0DXISOA0j/TjnzW1uMwW4fd/qjGIUEoZl/WxvYp7DG2K+y8fpwykH+y8tfVDlDoJrG+oy+z+dQ+3ZyDIfrfn9O9bhod+K1D8qY4XbpBymb0Cwq+yWFHNzzAZbTRnQIDAQABoyAwHjAOBgNVHQ8BAf8EBAMCBeAwDAYDVR0TAQH/BAIwADANBgkqhkiG9w0BAQUFAAOCAQEAnHk3H+Zw6EN2HK0Z8V56AArfRsWsrcIRWpI9bGz/BNLXmKnPdFIFQhn8ROkRonVG72+2FDWhAhPHdKdEKHhZwcm97qaSZzTLt54FerBiwxiABzpXuQ50uvD10gRBCb3/spQGwa6WvP34r7ol9GCE0eduXC/2U3/NPcSMe8i7LiUAsaLMIe9Ogn4Goo693NG394KoGNUkvUTjORj160XhK+YAI1RyRxsqrQLIi6oOzjT2Cb/RhRBaltjAny8USdG6rcmhhMUXKX22BoLU/pyIdaVSoC0lWbW3Rx/7xqmjCrnoENdC93qy9N8KezFwiCLZ7ykitHAn0UQK9RdYxLiaaw==";
        String originData = "MV0wGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMTYxMDMxMTMwOTA1WjAjBgkqhkiG9w0BCQQxFgQUdo12r/BNjorFPbrE9TG9FTUpHOQ=";
        String signData = "D8Mo/+lojH10puWGStM6uaNWwFr17rknthu9NpJ4M161iZJ1WdlnK1lQTBSZa/DLY69Bojzq1O4sUJS+m+/DdoBWTpqjXZumA8gtjSA5hoTdM+sWJRzsX3emMiR0Srt/XzUbRCD2CatSYztxj1l+rYGJxItDXweaqnW0uTpShfrgSWz/t/VYC9qBz9NGycH0DUs+ueLXfTDAiG5YLuYWTR5agfbYAaeRZF1qJ6G41UVOjGZRbHvzKgnvNvVCcAZc4J/ZDHPSbIKWzS1K17ta8NVcjeBmTXictL/5PF/Brhie3lhG607O1SwFoA1h11oq6i7REtNuq1IUeKQANaWcMA==";

        CertificateFactory cft = CertificateFactory.getInstance("X.509");
        Certificate cert = cft.generateCertificate(new ByteArrayInputStream(Base64.decode(certStr)));


        Signature sign = Signature.getInstance("SHA1WithRSA");
        sign.initVerify(cert);
        sign.update(Base64.decode(originData));
        Boolean falg = sign.verify(Base64.decode(signData));
        System.out.println(falg);

    }

    @Test
    public void test1() throws Exception {
        String certStr = "MIIDcDCCAligAwIBAgIUDk9WVYatH23OtnVsrXSw4VKjnjEwDQYJKoZIhvcNAQEL\n" +
                "BQAwNDELMAkGA1UEAwwCcmExCzAJBgNVBAsMAnJhMQswCQYDVQQKDAJyYTELMAkG\n" +
                "A1UEBhMCQ04wHhcNMTcwNjI5MDI1OTU2WhcNMTgwNjI5MDI1OTU2WjBUMRUwEwYD\n" +
                "VQQDDAxza3lmYWl0aElrZXkxFjAUBgNVBAsMDXJhLWFkbWluLTA2MjkxFjAUBgNV\n" +
                "BAoMDXJhLWFkbWluLTA2MjkxCzAJBgNVBAYMAkNOMIIBIjANBgkqhkiG9w0BAQEF\n" +
                "AAOCAQ8AMIIBCgKCAQEAsXKrbHrWsV30Ks6quUfUayeIwEZqHpB/FfHp8MqPe/K+\n" +
                "zdmzgaOF1YFHTKrO/YZ1BTBfI3+9K/+IQWZFCNcgjo3chIbBuYnfhBCPaY/DJjTr\n" +
                "j908ZtqP0A6c2zUKD/eOfMbJpJIoB5dk05/e6W4fRplWccO0uX9XFPCpriTsv7jC\n" +
                "84XIKyQD5HHtjiem3q5pPx4K0IDh6OL/bcHxRjxDJlQlBqIGQOc3D5Lc5axlVU4b\n" +
                "1f/X3JYw3mepHVNsZDCzI3Z2ymUxPANnybf1eMkGUUIiUniaTgLGQ617CuUkzwcU\n" +
                "WK8DBKRoX/NZx1vT5ryf9zI06Qv2W9ECg7ClGuCdgQIDAQABo1owWDAJBgNVHRME\n" +
                "AjAAMAsGA1UdDwQEAwIGwDAfBgNVHSMEGDAWgBQv/rbuxnQBWYgvD3rT3NNFtEXE\n" +
                "szAdBgNVHQ4EFgQU/E0fpip+7z4IWkcWwHeDoaWpwWUwDQYJKoZIhvcNAQELBQAD\n" +
                "ggEBAGi1/MRdBup74wI8VcKj1twYmbQxjhZmt74Txt17bOve2vhZ1v3JybLzp0Zn\n" +
                "U8eIMC19+2tebYOSt4Hrq75kpv3sUVx60MnL1ilEZ0DR8nD1OvVSdx4VJth2f9Gv\n" +
                "3ZoBqKOHkX4O5ihFsUyS6NNyXRBGVU7BIJ40UjOgXxGHMnJxIY9CGq//zSCZTpB3\n" +
                "bjx5KHQdVGBj1BS6KXmywSDmQNNjsSCk4zhMIqLsV1soerONIf38d8L+3wnaKOyY\n" +
                "HYyAoD3Y8tiyspSAyCznFDs97alj+7NW9shb26KClNjJleJ/aXp2g0q6rPg4orvi\n" +
                "95XkRyyNy/LPBLCDR1mCRJjQP1A=";

        String originData = "MV0wGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMTcwNjI4MTAwMDQzWjAjBgkqhkiG9w0BCQQxFgQUZRSTMaoQbdTLxiSL3E8gWR+aqiw=";

        String signData = "AMQEW3q8pJAKxpcQGFbQhhiFozLtHVlqJKzndO4c2HoGhfV6NqdQNQnC7uIGmnpNQhmgsnqq1bzBy1/r2oYxmiq8dd3yHQszK9hdhAIbzQXYtMcWwFJaleIFc1OBYTa/nJGPPdnbfd3wT0QIjMvU6E8b4pJexQC6ouwmDR5w+uP/dpPti2qePLH66+9n0EN+m2UqBttUH1qoYssBNmkzB6o9B9NdsdSs4RQ6u1JJoiN+9JKDw5tLrD3v+eWxHIf6DYB6NhOusGRzrzvnNFq0b2rIlHZBpcuEBPNZLUStwqyE5D9xwIu9ptkuUlWcwpHXwpjkVDiXCxuw+S2WuwRghw==";
        try {
            CertificateFactory cft = CertificateFactory.getInstance("X.509");
            Certificate cert = cft.generateCertificate(new ByteArrayInputStream(Base64.decode(certStr)));

            Signature sign = Signature.getInstance("SHA1WithRSA");
            sign.initVerify(cert);
            sign.update(originData.getBytes());
            Boolean falg = sign.verify(Base64.decode(signData));
            System.out.println(falg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSubject(){
        String b64cert = "MIH6MIHnoAMCAQICCFkSI4MzlSYzMAsGByqGSM49AgEFADAeMQ0wCwYDVQQLDARyb290MQ0wCwYDVQQKDARyb290MB4XDTE3MDUwNDE2MDAwMFoXDTE4MDUwNDE2MDAwMFowLjEOMAwGA1UEAwwFMTExMTExDTALBgNVBAsMBHJvb3QxDTALBgNVBAoMBHJvb3QwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQ5UgKn7gXloBqONjjYpjB7IQ+S9kYAiXLOe7K/l51Z9cjAXxfap2TnZEjo1AhVlPM7apuciLM0BMkcdGQXAxBnMAsGByqGSM49AgEFAAMBAA==";

        byte[] binCert = Base64.decode(b64cert);
        ByteArrayInputStream bain = new ByteArrayInputStream(binCert);
        X509Certificate cert = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(bain);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        String subjects[] = cert.getSubjectDN().toString().split(",");
        for (int i = 0; i < subjects.length; i++) {
            if (subjects[i].contains("CN=")) {
                int index = subjects[i].indexOf("CN=");
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String endTime = sdf.format(cert.getNotBefore());
        String startTime = sdf.format(cert.getNotAfter());
    }
}