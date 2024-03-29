package network.radicle.tools.migrate.clients;

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
import network.radicle.tools.migrate.core.github.Comment;
import network.radicle.tools.migrate.core.github.Commit;
import network.radicle.tools.migrate.core.github.Event;
import network.radicle.tools.migrate.core.github.Issue;
import network.radicle.tools.migrate.handlers.ResponseHandler;
import network.radicle.tools.migrate.services.CliService;
import network.radicle.tools.migrate.services.FilesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class GitHubClient implements IGitHubClient {
    private static final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    @Inject Client client;
    @Inject ObjectMapper mapper;
    @Inject Config config;
    @Inject FilesService filesService;

    @Inject CliService cli;

    public List<Issue> getIssues(int page, Config.Filters filters) throws Exception {
        var url = config.getGithub().url() + "/repos/" + config.getGithub().owner() + "/" + config.getGithub().repo() +
                "/issues";

        var milestone = filters.milestone() != null ? filters.milestone().toString() : null;
        var state = filters.state() != null ? filters.state().name() : State.all.name();
        try (var resp = client.target(url)
                .queryParam("per_page", config.getGithub().pageSize())
                .queryParam("page", page)
                .queryParam("milestone", milestone)
                .queryParam("state", state)
                .queryParam("assignee", filters.assignee())
                .queryParam("creator", filters.creator())
                .queryParam("labels", filters.labels())
                .queryParam("since", filters.since().toString())
                .request()
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGithub().token()))
                .header("X-GitHub-Api-Version", config.getGithub().version())
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() {
            });
        }
    }

    @Override
    public List<Comment> getComments(long issueNumber, int page) throws Exception {
        var url = config.getGithub().url() + "/repos/" + config.getGithub().owner() + "/" + config.getGithub().repo() +
                "/issues/" + issueNumber + "/comments";

        try (var resp = client.target(url)
                .queryParam("per_page", config.getGithub().pageSize())
                .queryParam("page", page)
                .request()
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGithub().token()))
                .header("X-GitHub-Api-Version", config.getGithub().version())
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() {
            });
        }
    }

    @Override
    public List<Event> getEvents(long issueNumber, int page, boolean timeline) throws Exception {
        var url = config.getGithub().url() + "/repos/" + config.getGithub().owner() + "/" + config.getGithub().repo() +
                "/issues/" + issueNumber;

        url += timeline ? "/timeline" : "/events";

        try (var resp = client.target(url)
                .queryParam("per_page", config.getGithub().pageSize())
                .queryParam("page", page)
                .request()
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGithub().token()))
                .header("X-GitHub-Api-Version", config.getGithub().version())
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() {
            });
        }
    }

    @Override
    public Commit getCommit(String commitId) throws Exception {
        var url = config.getGithub().url() + "/repos/" + config.getGithub().owner() + "/" + config.getGithub().repo() +
                "/commits/" + commitId;

        try (var resp = client.target(url)
                .queryParam("per_page", config.getGithub().pageSize())
                .queryParam("page", 1)
                .request()
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGithub().token()))
                .header("X-GitHub-Api-Version", config.getGithub().version())
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
            if (!url.startsWith(urlPrefix + "/assets") && !url.startsWith(urlPrefix + "/files")) {
                return null;
            }

            var cookieHeader = "user_session=" + config.getGithub().session() + "; Domain=github.com; Secure; HttpOnly";
            try (var response = client.target(url)
                    .request()
                    .header("Cookie", cookieHeader)
                    .get()) {

                int statusCode = response.getStatus();
                var family = Response.Status.Family.familyOf(statusCode);
                if (family != Response.Status.Family.SUCCESSFUL) {
                    logger.warn("Invalid user_session cookie. Received status code {}: {}.", statusCode, url);
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

    @Override
    public String execSubtreeCmd(String owner, String repo, String token, String path) {
        var wikiRepo = getRepoUrl(token) + ".wiki.git";
        var command = "";

        if (!Files.exists(Path.of(path, ".wiki"))) {
            command = "git -C " + path + " subtree add -m 'Migrating wiki' --prefix=.wiki/ " + wikiRepo + " master";
        } else {
            command = "git -C " + path + " subtree pull -m 'Migrating wiki' --prefix=.wiki/ " + wikiRepo + " master";
        }

        var payload = cli.execCommand(command, null);
        if (Strings.isNullOrEmpty(payload)) {
            logger.debug("git subtree add command failed.");
            return null;
        }

        return payload;
    }

    public String getRepoUrl(String token) {
        var tokenPrefix = Strings.isNullOrEmpty(token) ? "" : token + "@";
        return "https:// " + tokenPrefix + "github.com/" + config.getGithub().owner() + "/" + config.getGithub().repo();
    }
}
