package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IssueTest {
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    @Test
    public void testSerializationOfSingleIssue() throws Exception {
        var issue = generateGitLabIssue();
        var json = MAPPER.writeValueAsString(issue);
        var i = MAPPER.readValue(json, GitLabIssue.class);

        assertThat(i).isNotNull().usingRecursiveComparison().isEqualTo(issue);
    }

    @Test
    public void testSerializationOfManyIssues() {
        List<GitLabIssue> issues = loadGitLabIssues();
        assertThat(issues.size()).isNotZero();
        GitLabIssue issue = issues.get(0);
        assertThat(issue.title).isNotNull().isNotEmpty();
        assertThat(issue.createdAt).isNotNull();
    }

    public static GitLabIssue generateGitLabIssue() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/gitlab/issue.json");
            GitLabIssue issue = MAPPER.readValue(file, GitLabIssue.class);
            issue.id = issue.id + seed;
            issue.createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
            issue.updatedAt = Instant.now();
            issue.author.id = issue.author.id + seed;
            return issue;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<GitLabIssue> loadGitLabIssues() {
        try {
            var file = new File("src/test/resources/gitlab/issues.json");
            return MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
