package network.radicle.tools.migrate.core.github;

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
        var event = generateGitHubEvent();
        var json = IssueTest.MAPPER.writeValueAsString(event);
        var c = IssueTest.MAPPER.readValue(json, GitHubEvent.class);

        assertThat(c).isNotNull().usingRecursiveComparison().isEqualTo(event);
    }

    @Test
    public void testSerializationOfManyEvents() {
        List<GitHubEvent> events = loadGitHubEvents();
        assertThat(events.size()).isNotZero();
        GitHubEvent event = events.get(0);
        assertThat(event.event).isNotNull().isNotEmpty();
        assertThat(event.createdAt).isNotNull();
    }

    @Test
    public void testCreatedAtFormatting() {
        var expected = "Dec 20, 2021 13:42:05 UTC";
        var events = loadGitHubEvents();
        var formatted = DTF.format(events.get(0).createdAt);
        assertThat(formatted).isEqualTo(expected);
    }

    public static GitHubEvent generateGitHubEvent() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/github/event.json");
            var event = IssueTest.MAPPER.readValue(file, GitHubEvent.class);
            event.id = event.id + seed;
            event.createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
            event.actor.id = event.actor.id + seed;
            return event;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<GitHubEvent> loadGitHubEvents() {
        try {
            var file = new File("src/test/resources/github/events.json");
            return IssueTest.MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
