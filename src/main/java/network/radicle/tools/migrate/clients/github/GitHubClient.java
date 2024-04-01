package network.radicle.tools.migrate.clients.github;

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
import network.radicle.tools.migrate.core.github.GitHubComment;
import network.radicle.tools.migrate.core.github.GitHubCommit;
import network.radicle.tools.migrate.core.github.GitHubEvent;
import network.radicle.tools.migrate.core.github.GitHubIssue;
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

    public List<GitHubIssue> getIssues(int page, Config.Filters filters) throws Exception {
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
    public List<GitHubComment> getComments(long issueNumber, int page) throws Exception {
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
    public List<GitHubEvent> getEvents(long issueNumber, int page, boolean timeline) throws Exception {
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
    public GitHubCommit getCommit(String commitId) throws Exception {
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

            var cookieHeader = "user_session=" + config.getGithub().session() + "; Domain=" + config.getGithub().domain() + "; Secure; HttpOnly";
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
        logger.info("Migration started for {}", getRepoUrl("") + ".wiki.git");

        var command = getCommand(path, token);
        var response = cli.execCommand(command, null);
        if (Strings.isNullOrEmpty(response)) {
            var cmd = getCommand(path, "<token>");
            logger.error(cmd);
            return null;
        }
        logger.debug(response);
        logger.info("Wiki migrated to {}", Path.of(path, ".wiki"));

        return response;
    }

    public String getRepoUrl(String token) {
        var tokenPrefix = Strings.isNullOrEmpty(token) ? "" : token + "@";
        return "https://" + tokenPrefix + config.getGithub().domain() + "/" + config.getGithub().owner() + "/" +
                config.getGithub().repo();
    }

    public String getCommand(String path, String token) {
        var wikiRepo = getRepoUrl(token) + ".wiki.git";
        var command = "";

        var targetFolder = Path.of(path, ".wiki");
        if (!Files.exists(targetFolder)) {
            command = "git -C " + path + " subtree add -m 'Merge wiki' --prefix=.wiki/ " + wikiRepo + " master";
        } else {
            command = "git -C " + path + " subtree pull -m 'Merge wiki' --prefix=.wiki/ " + wikiRepo + " master";
        }

        return command;
    }
}
