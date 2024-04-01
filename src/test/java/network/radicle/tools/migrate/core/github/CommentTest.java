package network.radicle.tools.migrate.core.github;

import com.fasterxml.jackson.core.type.TypeReference;
import network.radicle.tools.migrate.services.github.GitHubMarkdownService;
import network.radicle.tools.migrate.services.MarkdownService.MarkdownLink;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {
    private static final Logger logger = LoggerFactory.getLogger(CommitTest.class);

    @Test
    public void testSerializationOfSingleComment() throws Exception {
        var comment = generateGitHubComment();
        var json = IssueTest.MAPPER.writeValueAsString(comment);
        var c = IssueTest.MAPPER.readValue(json, GitHubComment.class);

        assertThat(c).isNotNull().usingRecursiveComparison().isEqualTo(comment);
    }

    @Test
    public void testSerializationOfManyComments() {
        List<GitHubComment> comments = loadGitHubComments();
        assertThat(comments.size()).isNotZero();
        GitHubComment comment = comments.get(0);
        assertThat(comment.body).isNotNull().isNotEmpty();
        assertThat(comment.createdAt).isNotNull();
    }

    @Test
    public void testMarkdownLinkParsingFromComments() throws Exception {
        var comments = loadGitHubComments();
        var markdownWithAsset = comments.get(0).body;
        var markdownService = new GitHubMarkdownService();
        var actual = markdownService.extractUrls(markdownWithAsset);

        var expected = List.of(
                new MarkdownLink("Sample Screenshot", "https://github.com/testowner/testrepo/assets/2813615/23bfc62e-791d-427b-bdab-aa5ea3abe81f"),
                new MarkdownLink("1f339325af4161591a1a1a206a2fc5e66.pdf", "https://github.com/testowner/testrepo/files/12895629/1f339325af4161591a1a1a206a2fc5e66.pdf")
        );

        assertThat(actual.size()).isNotZero().isEqualTo(expected.size());
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static GitHubComment generateGitHubComment() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/github/comment.json");
            var comment = IssueTest.MAPPER.readValue(file, GitHubComment.class);
            comment.id = comment.id + seed;
            comment.createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
            comment.updatedAt = Instant.now();
            comment.user.id = comment.user.id + seed;
            return comment;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<GitHubComment> loadGitHubComments() {
        try {
            var file = new File("src/test/resources/github/comments.json");
            return IssueTest.MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
