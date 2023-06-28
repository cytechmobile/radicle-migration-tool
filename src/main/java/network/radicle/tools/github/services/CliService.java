package network.radicle.tools.github.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.core.radicle.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@ApplicationScoped
public class CliService {
    private static final Logger logger = LoggerFactory.getLogger(CliService.class);

    @Inject Config config;
    @Inject ObjectMapper mapper;

    public Session createSession() {

        try {
            var rt = Runtime.getRuntime();
            var backend = config.getRadicle().url().replace("/api", "");
            var pr = rt.exec("rad web -b " + backend + " --json");
            var authReq = "";
            try (var input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                 var error = new BufferedReader(new InputStreamReader(pr.getErrorStream()))) {

                var errorLine = error.readLine();
                if (!Strings.isNullOrEmpty(errorLine)) {
                    logger.error("Session authentication failed: {}", errorLine);
                    return null;
                }

                authReq = input.readLine();
                if (Strings.isNullOrEmpty(authReq)) {
                    logger.error("Session authentication failed.");
                    return null;
                }

                Session session = null;
                try {
                    session = mapper.readValue(authReq, Session.class);
                } catch (Exception ex) {
                    logger.error("Failed to create session. Error: {}", authReq);
                }
                return session;
            }
        } catch (Exception ex) {
            logger.error("Failed to create session by using cli.", ex);
            return null;
        }
    }
}











