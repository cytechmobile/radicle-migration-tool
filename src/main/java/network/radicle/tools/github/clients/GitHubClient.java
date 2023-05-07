package network.radicle.tools.github.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.core.GitHubIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class GitHubClient implements IGitHubClient {
    private static final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    @Inject Client client;
    @Inject ObjectMapper mapper;
    @Inject Config config;

    public List<GitHubIssue> getIssues(int page) throws Exception {
        var url = String.join("/", List.of(Strings.nullToEmpty(config.getUrl()), "repos",
                Strings.nullToEmpty(config.getOwner()), Strings.nullToEmpty(config.getRepo()), "issues"));

        logger.info("Fetching issues from {}", url);

        try (var resp = client.target(url)
                .queryParam("state", "all")
                .queryParam("per_page", config.getPageSize())
                .queryParam("page", page)
                .request()
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.getToken()))
                .header("X-GitHub-Api-Version", config.getVersion())
                .get()) {

            var status = resp.getStatus();
            var json = Strings.nullToEmpty(resp.readEntity(String.class));

            if (Response.Status.Family.familyOf(status) != Response.Status.Family.SUCCESSFUL) {
                logger.error("Got status: {}, payload: {}", status, json);
                throw new BadRequestException(json);
            }

            return mapper.readValue(json, new TypeReference<>() { });

        }
    }
}
