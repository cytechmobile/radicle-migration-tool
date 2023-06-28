package network.radicle.tools.github.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.core.radicle.Issue;
import network.radicle.tools.github.core.radicle.Session;
import network.radicle.tools.github.core.radicle.actions.Action;
import network.radicle.tools.github.handlers.ResponseHandler;
import network.radicle.tools.github.services.SshAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ApplicationScoped
public class RadicleClient implements IRadicleClient {
    private static final Logger logger = LoggerFactory.getLogger(RadicleClient.class);

    @Inject Client client;
    @Inject ObjectMapper mapper;
    @Inject Config config;
    @Inject SshAgentService agent;

    @Override
    public Session createSession() throws Exception {
        var url = config.getRadicle().url() + "/" + config.getRadicle().version() + "/sessions";

        logger.debug("Creating radicle session: {}", url);
        Session session;
        try (var resp = client.target(url).request().post(null)) {
            var json = ResponseHandler.handleResponse(resp);
            session =  mapper.readValue(json, Session.class);
        }

        session.signature = agent.sign(session);

        var authSessionUrl = url + '/' + session.id;
        logger.debug("Authenticating radicle session: {}", authSessionUrl);

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
    public String createIssue(Session session, Issue issue) throws Exception {
        var url = config.getRadicle().url() + "/" + config.getRadicle().version() + "/projects/" +
        config.getRadicle().project() + "/issues";

        logger.debug("Creating radicle issue: {}", url);
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
        var url = config.getRadicle().url() + "/" + config.getRadicle().version() + "/projects/" +
                config.getRadicle().project() + "/issues/" + id;

        logger.debug("Updating radicle issue: {} with url: {}", id, url);
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
