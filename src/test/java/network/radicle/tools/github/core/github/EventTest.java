package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {
    @Test
    public void testSerializationOfSingleEvent() throws Exception {
        var event = generateGitHubEvent();
        var json = IssueTest.MAPPER.writeValueAsString(event);
        var c = IssueTest.MAPPER.readValue(json, Event.class);

        assertThat(c).isNotNull().usingRecursiveComparison().isEqualTo(event);
    }

    @Test
    public void testSerializationOfManyEvents() {
        List<Event> events = loadGitHubEvents();
        assertThat(events.size()).isNotZero();
        Event event = events.get(0);
        assertThat(event.event).isNotNull().isNotEmpty();
        assertThat(event.createdAt).isNotNull();
    }

    @Test
    public void testCreatedAtFormatting() {
        var expected = "Apr 14, 2011 16:00:49 UTC";
        var events = loadGitHubEvents();
        var formatted = Timeline.DTF.format(events.get(0).createdAt);
        assertThat(formatted).isEqualTo(expected);
    }

    public static Event generateGitHubEvent() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/github/event.json");
            var event = IssueTest.MAPPER.readValue(file, Event.class);
            event.id = event.id + seed;
            event.createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
            event.actor.id = event.actor.id + seed;
            return event;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Event> loadGitHubEvents() {
        try {
            var file = new File("src/test/resources/github/events.json");
            return IssueTest.MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
