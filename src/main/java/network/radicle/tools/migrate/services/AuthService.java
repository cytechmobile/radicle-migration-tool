package network.radicle.tools.migrate.services;

import com.sshtools.common.publickey.SshKeyUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.core.radicle.Session;
import network.radicle.tools.migrate.utils.Multibase;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        var originalError = System.err;
        try {
            System.setErr(new PrintStream(new ByteArrayOutputStream()));
            var kp = SshKeyUtils.getPrivateKey(new File(radHome.orElse("~/.radicle") + "/keys/radicle"),
                    config.radicle().passphrase());
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
