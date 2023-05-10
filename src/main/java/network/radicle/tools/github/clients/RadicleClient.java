package network.radicle.tools.github.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.handlers.ResponseHandler;
import network.radicle.tools.github.core.radicle.Issue;
import network.radicle.tools.github.core.radicle.Session;
import network.radicle.tools.github.core.radicle.actions.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class RadicleClient implements IRadicleClient {
    private static final Logger logger = LoggerFactory.getLogger(RadicleClient.class);

    @Inject Client client;
    @Inject ObjectMapper mapper;
    @Inject Config config;

    @Override
    public Session createSession() throws Exception {
        var url = String.join("/", List.of(
                Strings.nullToEmpty(config.getRadicle().url()),
                Strings.nullToEmpty(config.getRadicle().version()), "sessions"));

        logger.debug("Creating radicle session: {}", url);
        try (var resp = client.target(url).request().post(null)) {
            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, Session.class);
        }
    }

    @Override
    public String createIssue(Session session, Issue issue) throws Exception {
        var url = String.join("/", List.of(
                Strings.nullToEmpty(config.getRadicle().url()),
                Strings.nullToEmpty(config.getRadicle().version()), "projects",
                Strings.nullToEmpty(config.getRadicle().project()), "issues"));

        logger.debug("Creating radicle issue: {}", url);
        try (var resp = client.target(url).request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(session.id))
                .post(Entity.entity(issue, MediaType.APPLICATION_JSON))) {

            var json = ResponseHandler.handleResponse(resp);
            var jsonNode = mapper.readTree(json);
            var success = jsonNode.get("success").asBoolean();
            if (success) {
                return jsonNode.get("id").asText();
            }
            return null;
        }
    }

    @Override
    public boolean updateIssue(Session session, String id, Action action) throws Exception {
        var url = String.join("/", List.of(
                Strings.nullToEmpty(config.getRadicle().url()),
                Strings.nullToEmpty(config.getRadicle().version()), "projects",
                Strings.nullToEmpty(config.getRadicle().project()), "issues", id));

        logger.debug("Updating radicle issue: {} with url: {}", id, url);
        try (var resp = client.target(url)
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(session.id))
                .method("PATCH", Entity.entity(action, MediaType.APPLICATION_JSON))) {

            var json = ResponseHandler.handleResponse(resp);
            var jsonNode = mapper.readTree(json);
            return jsonNode.get("success").asBoolean();

        }
    }
}
