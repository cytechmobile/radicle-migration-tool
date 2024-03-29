package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommitTest {

    private static final Logger logger = LoggerFactory.getLogger(CommitTest.class);

    @Test
    public void testSerializationOfSingleCommit() throws Exception {
        var commit = generateGitHubCommit();
        var json = IssueTest.MAPPER.writeValueAsString(commit);
        var c = IssueTest.MAPPER.readValue(json, Commit.class);

        assertThat(c).isNotNull().usingRecursiveComparison().isEqualTo(commit);
    }

    @Test
    public void testSerializationOfManyCommits() {
        var commits = loadGitHubCommits();
        assertThat(commits.size()).isNotZero();
        var commit = commits.get(0);
        assertThat(commit.metadata).isNotNull();
        assertThat(commit.metadata.message).isNotNull().isNotEmpty();
    }

    public static Commit generateGitHubCommit() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/github/commit.json");
            var commit = IssueTest.MAPPER.readValue(file, Commit.class);
            commit.sha = commit.sha + seed;
            commit.metadata.message = commit.metadata.message + seed;
            commit.author.id = commit.author.id + seed;
            return commit;
        } catch (Exception ex) {
            logger.error("Error while generating GitHub commit object", ex);
            return null;
        }
    }

    public static List<Commit> loadGitHubCommits() {
        try {
            var file = new File("src/test/resources/github/commits.json");
            return IssueTest.MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
