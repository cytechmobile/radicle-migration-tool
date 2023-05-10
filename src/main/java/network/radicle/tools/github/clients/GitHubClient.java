package network.radicle.tools.github.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.HttpHeaders;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.core.github.Comment;
import network.radicle.tools.github.core.github.Issue;
import network.radicle.tools.github.handlers.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class GitHubClient implements IGitHubClient {
    private static final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    @Inject Client client;
    @Inject ObjectMapper mapper;
    @Inject Config config;

    public List<Issue> getIssues(int page) throws Exception {
        var url = String.join("/", List.of(
                Strings.nullToEmpty(config.getGithub().url()), "repos",
                Strings.nullToEmpty(config.getGithub().owner()),
                Strings.nullToEmpty(config.getGithub().repo()), "issues"));

        logger.debug("Fetching GitHub issues: {}", url);
        try (var resp = client.target(url)
                .queryParam("state", "all")
                .queryParam("per_page", config.getGithub().pageSize())
                .queryParam("page", page)
                .request()
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGithub().token()))
                .header("X-GitHub-Api-Version", config.getGithub().version())
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() { });
        }
    }

    @Override
    public List<Comment> getComments(long issueId, int page) throws Exception {
        var url = String.join("/", List.of(
                Strings.nullToEmpty(config.getGithub().url()), "repos",
                Strings.nullToEmpty(config.getGithub().owner()),
                Strings.nullToEmpty(config.getGithub().repo()), "issues",
                Long.toString(issueId), "comments"));

        logger.debug("Fetching GitHub comments for issue {}: {}", issueId, url);
        try (var resp = client.target(url)
                .queryParam("per_page", config.getGithub().pageSize())
                .queryParam("page", page)
                .request()
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getGithub().token()))
                .header("X-GitHub-Api-Version", config.getGithub().version())
                .get()) {

            var json = ResponseHandler.handleResponse(resp);
            return mapper.readValue(json, new TypeReference<>() { });
        }
    }
}
