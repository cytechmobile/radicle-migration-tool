package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {
    @Test
    public void testSerializationOfSingleComment() throws Exception {
        var comment = generateGitHubComment();
        var json = IssueTest.MAPPER.writeValueAsString(comment);
        var c = IssueTest.MAPPER.readValue(json, Comment.class);

        assertThat(c).isNotNull().usingRecursiveComparison().isEqualTo(comment);
    }

    @Test
    public void testSerializationOfManyComments() {
        List<Comment> comments = loadGitHubComments();
        assertThat(comments.size()).isNotZero();
        Comment comment = comments.get(0);
        assertThat(comment.body).isNotNull().isNotEmpty();
        assertThat(comment.createdAt).isNotNull();
    }

    public static Comment generateGitHubComment() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/github/comment.json");
            var comment = IssueTest.MAPPER.readValue(file, Comment.class);
            comment.id = comment.id + seed;
            comment.createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
            comment.updatedAt = Instant.now();
            comment.user.id = comment.user.id + seed;
            return comment;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Comment> loadGitHubComments() {
        try {
            var file = new File("src/test/resources/github/comments.json");
            return IssueTest.MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
