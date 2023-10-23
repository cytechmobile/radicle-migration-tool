package network.radicle.tools.github.services;

import com.sshtools.common.publickey.SshKeyUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.core.radicle.Session;
import network.radicle.tools.github.utils.Multibase;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@ApplicationScoped
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @ConfigProperty(name = "rad.home")
    Optional<String> radHome;

    @Inject Config config;

    public String sign(Session session) {
        try {
            return sign(session, new FileInputStream(radHome.orElse("~/.radicle") + "/keys/radicle"));
        } catch (Exception e) {
            logger.debug("Failed to sign the session. Error: {}", e.getMessage());
            return null;
        }
    }

    public String sign(Session session, InputStream key) {
        var originalError = System.err;
        try {
            System.setErr(new PrintStream(new ByteArrayOutputStream()));
            var kp = SshKeyUtils.getPrivateKey(key, config.getRadicle().passphrase());
            var dataToSign = session.id + ":" + session.publicKey;
            var signedData = kp.getPrivateKey().sign(dataToSign.getBytes(StandardCharsets.UTF_8));

            return Multibase.encode(Multibase.Base.Base58BTC, signedData);
        } catch (Exception e) {
            logger.debug("Failed to sign the session. Error: {}", e.getMessage());
            return null;
        } finally {
            System.setErr(originalError);
        }
    }
}
