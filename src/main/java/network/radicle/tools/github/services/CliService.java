package network.radicle.tools.github.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.core.radicle.Session;
import network.radicle.tools.github.utils.Multibase;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CliService {
    private static final Logger logger = LoggerFactory.getLogger(CliService.class);
    public static final String ALGORITHM_NAME = "ssh-ed25519";

    @Inject Config config;
    @Inject ObjectMapper mapper;

    @ConfigProperty(name = "rad.passphrase")
    Optional<String> passphrase;

    @ConfigProperty(name = "rad.home")
    Optional<String> radHome;

    public Session createSession() {
        try {
            var backend = config.getRadicle().url().toString().replace("/api", "");
            var command = "rad web -b " + backend + " --json";

            var authSessionPayload = execCommand(command, null);
            if (Strings.isNullOrEmpty(authSessionPayload)) {
                logger.debug("Session authentication failed.");
                return null;
            }

            Session session = null;
            try {
                session = mapper.readValue(authSessionPayload, Session.class);
            } catch (Exception ex) {
                logger.debug("Failed to create session. Error: {}", authSessionPayload);
            }
            return session;
        } catch (Exception ex) {
            logger.error("Failed to create session by using cli.", ex);
            return null;
        }
    }

    // [PROTOCOL.sshsig]: https://cvsweb.openbsd.org/src/usr.bin/ssh/PROTOCOL.sshsig?annotate=HEAD
    public String sign(Session session) {
        try {
            if (radHome.isEmpty()) {
                logger.debug("RAD_HOME is missing");
                return null;
            }

            var dataToSign = session.id + ":" + session.publicKey;
            var sourceTempFile = Files.createTempFile(null, null);
            Files.writeString(sourceTempFile, dataToSign);

            var dataToSignFromFile = Files.readAllBytes(sourceTempFile);
            logger.info("SSH-KEYGEN DATA TO SIGN: {}", dataToSignFromFile);

            var sigTempFile = Files.createTempFile(null, null);

            var command = "cat " + sourceTempFile + " | " +
            "ssh-keygen -Y sign -n file -f " + radHome.get() + "/keys/radicle > " + sigTempFile;

            logger.debug("{}", command);

            execCommand(command, passphrase.orElse(null));

            var lines = Files.readAllLines(sigTempFile);
            var signed = "";
            if (lines.size() > 2) {
                signed = lines.stream().skip(1).limit(lines.size() - 2).collect(Collectors.joining());
            }

            //the 8 is for the integers that keep the header's and signature's lengths
            var base64Decoded = Base64.getDecoder().decode(signed);
            var signature = Arrays.copyOfRange(base64Decoded, base64Decoded.length - 64, base64Decoded.length);

            var s = Multibase.encode(Multibase.Base.Base58BTC, signature);
            var log = Map.of("sessionId", session.id, "publicKey", session.publicKey, "signature", s);
            logger.debug("{}, {}", mapper.writeValueAsString(log), base64Decoded);

            return s;
        } catch (Exception ex) {
            logger.error("Failed to sign the session using the ssh-keygen -Y", ex);
            return null;
        }
    }

    public String execCommand(String command, String userInput) {
        var output = new StringBuilder();

        try {
            var builder = new ProcessBuilder("/bin/sh", "-c", command);
            passphrase.ifPresent(s -> builder.environment().put("RAD_PASSPHRASE", s));

            var process = builder.start();

            if (userInput != null) {
                try (var outputStream = process.getOutputStream()) {
                    outputStream.write(userInput.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }
            }

            int exitCode = process.waitFor();
            logger.debug("Command finished with exit code: {}", exitCode);

            try (var outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 var errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = outputReader.readLine()) != null) {
                    output.append(line).append("\n");
                    logger.debug("{}", line);
                }

                while ((line = errorReader.readLine()) != null) {
                    logger.debug("{}", line);
                }
            }

        } catch (IOException | InterruptedException e) {
            logger.debug("Could not execute the command: {}", command);
        }

        return output.toString();
    }
}
