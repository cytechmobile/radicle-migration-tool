package network.radicle.tools.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import network.radicle.tools.github.core.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class GitHubClient {
    private static final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    @Inject Client client;
    @Inject ObjectMapper mapper;

    //todo: check how to pass configuration by using cdi
    public boolean migrateIssues(GitHubConfig config) {
        String json;
        int status;
        var url = String.join("/", List.of(Strings.nullToEmpty(config.url()), "repos",
                Strings.nullToEmpty(config.owner()), Strings.nullToEmpty(config.repo()), "issues"));

        logger.info("Migrating issues from {}", url);

        var page = 1;
        var perPage = 100;
        var hasMorePages = true;
        var total = 0;
        while (hasMorePages) {
            logger.info("Fetching page {}", page);
            try (var resp = client.target(url)
                    .queryParam("state", "all")
                    .queryParam("per_page", perPage)
                    .queryParam("page", page)
                    .request()
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + Strings.nullToEmpty(config.token()))
                    .header("X-GitHub-Api-Version", config.version())
                    .get()) {

                status = resp.getStatus();
                json = Strings.nullToEmpty(resp.readEntity(String.class));

                if (Response.Status.Family.familyOf(status) != Response.Status.Family.SUCCESSFUL) {
                    logger.error("Got status: {}, payload: {}", status, json);
                    return false;
                }

                List<Issue> issues = mapper.readValue(json, new TypeReference<>() { });
                var batchSize = issues.size();
                total += batchSize;
                logger.info("Fetched page {} with {} issues. Total fetched {}", page, batchSize, total);

                //todo: import issues to radicle

                hasMorePages = perPage == issues.size();
                page++;
            } catch (Exception e) {
                logger.error("Failed to fetch issues from {}.", url, e);
                return false;
            }
        }

        logger.info("Migrated {} issues in total", total);
        return true;
    }

}
