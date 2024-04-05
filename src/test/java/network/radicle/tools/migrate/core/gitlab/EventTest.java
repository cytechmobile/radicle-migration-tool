package network.radicle.tools.migrate.core.gitlab;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static network.radicle.tools.migrate.services.MarkdownService.DTF;
import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {
    @Test
    public void testSerializationOfSingleEvent() throws Exception {
        var event = generateGitLabEvent();
        var json = IssueTest.MAPPER.writeValueAsString(event);
        var c = IssueTest.MAPPER.readValue(json, GitLabEvent.class);

        assertThat(c).isNotNull().usingRecursiveComparison().isEqualTo(event);
    }

    @Test
    public void testSerializationOfManyEvents() {
        List<GitLabEvent> events = loadGitLabEvents();
        assertThat(events.size()).isNotZero();
        GitLabEvent event = events.get(0);
        assertThat(event.getType()).isNotNull().isNotEmpty();
        assertThat(event.createdAt).isNotNull();
    }

    @Test
    public void testCreatedAtFormatting() {
        var expected = "Aug 20, 2018 13:38:20 UTC";
        var events = loadGitLabEvents();
        var formatted = DTF.format(events.get(0).createdAt);
        assertThat(formatted).isEqualTo(expected);
    }

    public static GitLabEvent generateGitLabEvent() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/gitlab/event.json");
            var event = IssueTest.MAPPER.readValue(file, GitLabEvent.class);
            event.id = event.id + seed;
            event.createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
            event.user.id = event.user.id + seed;
            return event;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<GitLabEvent> loadGitLabEvents() {
        try {
            var file = new File("src/test/resources/gitlab/events.json");
            return IssueTest.MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
