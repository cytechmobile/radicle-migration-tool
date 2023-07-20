package network.radicle.tools.github.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.HttpHeaders;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.commands.Command.State;
import network.radicle.tools.github.core.github.Comment;
import network.radicle.tools.github.core.github.Commit;
import network.radicle.tools.github.core.github.Event;
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
}
