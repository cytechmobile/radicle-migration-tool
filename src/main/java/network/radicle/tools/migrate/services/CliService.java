package network.radicle.tools.migrate.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.core.radicle.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class CliService {
    private static final Logger logger = LoggerFactory.getLogger(CliService.class);
    public static final String ALGORITHM_NAME = "ssh-ed25519";

    @Inject Config config;
    @Inject ObjectMapper mapper;

    public Session createSession() {
        try {
            var backend = config.radicle().url().toString().replace("/api", "");
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

    public String execCommand(String command, String userInput) {
        var output = new StringBuilder();

        try {
            var builder = new ProcessBuilder("/bin/sh", "-c", command);
            var passphrase = config.radicle().passphrase();
            if (!Strings.isNullOrEmpty(passphrase)) {
                builder.environment().put("RAD_PASSPHRASE", passphrase);
            }

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
                    if (exitCode == 0) {
                        logger.debug("{}", line);
                    } else {
                        logger.error("{}", line);
                    }

                }
            }

        } catch (IOException | InterruptedException e) {
            logger.debug("Could not execute the command: {}", command);
        }

        return output.toString();
    }
}
