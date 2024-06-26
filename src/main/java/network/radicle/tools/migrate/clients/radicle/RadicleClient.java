package network.radicle.tools.migrate.clients.radicle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.core.radicle.Issue;
import network.radicle.tools.migrate.core.radicle.Session;
import network.radicle.tools.migrate.core.radicle.actions.Action;
import network.radicle.tools.migrate.handlers.ResponseHandler;
import network.radicle.tools.migrate.services.AuthService;
import network.radicle.tools.migrate.services.CliService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RadicleClient implements IRadicleClient {
    private static final Logger logger = LoggerFactory.getLogger(RadicleClient.class);

    @Inject Client client;
    @Inject ObjectMapper mapper;
    @Inject Config config;
    @Inject AuthService authService;
    @Inject CliService cli;

    @Override
    public Session createSession() throws Exception {
        var url = config.radicle().url() + "/" + config.radicle().version() + "/sessions";

        Session session;
        try (var resp = client.target(url).request().post(null)) {
            var json = ResponseHandler.handleResponse(resp);
            session =  mapper.readValue(json, Session.class);
        }

        session.signature = authService.sign(session);

        // in case the signing session failed (e.g. due to ssh agent not being accessible)
        // fallback to creating session via the cli
        if (session.signature == null) {
            logger.debug("Fallback to session signing via the rad CLI.");
            session = cli.createSession();
        }

        if (session == null || session.signature == null) {
            return null;
        }

        var authSessionUrl = url + '/' + session.id;
        var authBody = Map.of("pk", session.publicKey, "sig", session.signature);
        try (var resp = client.target(authSessionUrl).request().put(Entity.json(authBody))) {
            var json = ResponseHandler.handleResponse(resp);
            var jsonNode = mapper.readTree(json);
            var success = jsonNode.get("success").asBoolean();
            if (!success) {
                logger.error("Session authentication failed.");
                return null;
            }
        }
        return session;
    }

    @Override
    public List<Issue> getIssues(Session session, String state) throws Exception {
        var url = config.radicle().url() + "/" + config.radicle().version() + "/projects/" +
                config.radicle().project() + "/issues";

        try (var resp = client.target(url)
                .queryParam("state", state)
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(session.id))
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() {
            });
        }
    }

    @Override
    public String createIssue(Session session, Issue issue) throws Exception {
        var url = config.radicle().url() + "/" + config.radicle().version() + "/projects/" +
        config.radicle().project() + "/issues";

        logger.trace("Creating radicle issue {}", issue.id);
        if (config.radicle().dryRun()) {
            return null;
        }

        try (var resp = client.target(url).request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(session.id))
                .post(Entity.json(issue))) {

            var json = ResponseHandler.handleResponse(resp);
            var jsonNode = mapper.readTree(json);
            var success = jsonNode.get("success").asBoolean();
            if (!success) {
                throw new BadRequestException(json);
            }
            return jsonNode.get("id").asText();
        }
    }

    @Override
    public boolean updateIssue(Session session, String id, Action action) throws Exception {
        var url = config.radicle().url() + "/" + config.radicle().version() + "/projects/" +
                config.radicle().project() + "/issues/" + id;

        logger.trace("Updating radicle issue {}, action {}", id, action);
        if (config.radicle().dryRun()) {
            return true;
        }

        try (var resp = client.target(url)
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(session.id))
                .method("PATCH", Entity.json(action))) {

            var json = ResponseHandler.handleResponse(resp);
            var jsonNode = mapper.readTree(json);
            return jsonNode.get("success").asBoolean();

        }
    }
}
