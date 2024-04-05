package network.radicle.tools.migrate.clients.gitlab;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.commands.Command.State;
import network.radicle.tools.migrate.core.gitlab.GitLabComment;
import network.radicle.tools.migrate.core.gitlab.GitLabEvent;
import network.radicle.tools.migrate.core.gitlab.GitLabIssue;
import network.radicle.tools.migrate.handlers.ResponseHandler;
import network.radicle.tools.migrate.services.FilesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static network.radicle.tools.migrate.core.gitlab.GitLabEvent.Type.LABEL;
import static network.radicle.tools.migrate.core.gitlab.GitLabEvent.Type.MILESTONE;
import static network.radicle.tools.migrate.core.gitlab.GitLabEvent.Type.STATE;

@ApplicationScoped
public class GitLabClient implements IGitLabClient {
    private static final Logger logger = LoggerFactory.getLogger(GitLabClient.class);

    @Inject Client client;
    @Inject ObjectMapper mapper;
    @Inject Config config;
    @Inject FilesService filesService;

    public List<GitLabIssue> getIssues(int page, Config.Filters filters) throws Exception {
        var id = URLEncoder.encode(config.getGitlab().namespace() + "/" + config.getGitlab().project(),
                StandardCharsets.UTF_8);

        var url = config.getGitlab().url() + "/" + config.getGitlab().version() + "/projects/" + id + "/issues";

        var milestone = filters.milestone() != null ? filters.milestone() : null;
        var state = filters.state() != null ? filters.state().name() : State.all.name();
        state = state.equals(State.open.name()) ? "opened" : state;
        try (var resp = client.target(url)
                .queryParam("per_page", config.getGitlab().pageSize())
                .queryParam("page", page)
                .queryParam("milestone", milestone)
                .queryParam("state", state)
                .queryParam("assignee_username", filters.assignee())
                .queryParam("author_username", filters.creator())
                .queryParam("labels", filters.labels())
                .queryParam("created_after", filters.since().toString())
                .request()
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGitlab().token()))
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() {
            });
        }
    }

    @Override
    public List<GitLabComment> getComments(long issueNumber, int page) throws Exception {
        var id = URLEncoder.encode(config.getGitlab().namespace() + "/" + config.getGitlab().project(),
                StandardCharsets.UTF_8);

        var url = config.getGitlab().url() + "/" + config.getGitlab().version() + "/projects/" + id +
                "/issues/" + issueNumber + "/notes";

        try (var resp = client.target(url)
                .queryParam("per_page", config.getGitlab().pageSize())
                .queryParam("page", page)
                .queryParam("sort", "asc")
                .request()
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGitlab().token()))
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() {
            });
        }
    }

    @Override
    public List<GitLabEvent> getEvents(long issueNumber, int page, GitLabEvent.Type type) throws Exception {
        var id = URLEncoder.encode(config.getGitlab().namespace() + "/" + config.getGitlab().project(),
                StandardCharsets.UTF_8);

        var endpoint = type == STATE ? "resource_state_events" :
                type == LABEL ? "resource_label_events" :
                        type == MILESTONE ? "resource_milestone_events" : "";

        var url = config.getGitlab().url() + "/" + config.getGitlab().version() + "/projects/" + id +
                "/issues/" + issueNumber + "/" + endpoint;

        try (var resp = client.target(url)
                .queryParam("per_page", config.getGitlab().pageSize())
                .queryParam("page", page)
                .request()
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGitlab().token()))
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() {
            });
        }
    }

    @Override
    public String getAssetOrFile(String url) {
        try {
            var urlPrefix = getRepoUrl("");
            if (!url.startsWith("/uploads")) {
                return null;
            }

            var cookieHeader = "_gitlab_session=" + config.getGitlab().session() + "; Domain= " +
                    config.getGitlab().domain() + "; Secure; HttpOnly";
            try (var response = client.target(urlPrefix + url)
                    .request()
                    .header("Cookie", cookieHeader)
                    .get()) {

                int statusCode = response.getStatus();
                var family = Response.Status.Family.familyOf(statusCode);
                if (family != Response.Status.Family.SUCCESSFUL) {
                    logger.warn("Invalid _gitlab_session cookie. Received status code {}: {}.", statusCode, url);
                    return null;
                }

                var fileContent = response.readEntity(InputStream.class).readAllBytes();
                if (fileContent == null) {
                    logger.debug("Received empty content with status code {}: {}.", statusCode, url);
                    return null;
                }

                var base64Prefix = filesService.getBase64Prefix(fileContent);
                var base64Content = Base64.getEncoder().encodeToString(fileContent);
                return base64Prefix + base64Content;
            }
        } catch (Exception ex) {
            logger.warn("Failed to fetch: {}", url, ex);
            return null;
        }
    }

    public String getRepoUrl(String token) {
        var tokenPrefix = Strings.isNullOrEmpty(token) ? "" : token + "@";
        return "https://" + tokenPrefix + config.getGitlab().domain() + "/" + config.getGitlab().namespace() + "/" +
                config.getGitlab().project();
    }
}
