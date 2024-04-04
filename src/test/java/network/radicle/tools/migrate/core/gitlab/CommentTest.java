package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.core.type.TypeReference;
import network.radicle.tools.migrate.services.MarkdownService.MarkdownLink;
import network.radicle.tools.migrate.services.gitlab.GitLabMarkdownService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {
    private static final Logger logger = LoggerFactory.getLogger(CommentTest.class);

    @Test
    public void testSerializationOfSingleComment() throws Exception {
        var comment = generateGitLabComment();
        var json = IssueTest.MAPPER.writeValueAsString(comment);
        var c = IssueTest.MAPPER.readValue(json, GitLabComment.class);

        assertThat(c).isNotNull().usingRecursiveComparison().isEqualTo(comment);
    }

    @Test
    public void testSerializationOfManyComments() {
        List<GitLabComment> comments = loadGitLabComments();
        assertThat(comments.size()).isNotZero();
        GitLabComment comment = comments.get(0);
        assertThat(comment.body).isNotNull().isNotEmpty();
        assertThat(comment.createdAt).isNotNull();
    }

    @Test
    public void testMarkdownLinkParsingFromComments() throws Exception {
        var comments = loadGitLabComments();
        var markdownWithAsset = comments.get(0).body;
        var markdownService = new GitLabMarkdownService();
        var actual = markdownService.extractUrls(markdownWithAsset);

        var expected = List.of(
                new MarkdownLink("Dwarf", "/uploads/2494c32d7f4b94a372776fcaa6a801bd/dwarf.png")
        );

        assertThat(actual.size()).isNotZero().isEqualTo(expected.size());
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static GitLabComment generateGitLabComment() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/gitlab/comment.json");
            var comment = IssueTest.MAPPER.readValue(file, GitLabComment.class);
            comment.id = comment.id + seed;
            comment.createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
            comment.updatedAt = Instant.now();
            comment.author.id = comment.author.id + seed;
            return comment;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<GitLabComment> loadGitLabComments() {
        try {
            var file = new File("src/test/resources/gitlab/comments.json");
            return IssueTest.MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
