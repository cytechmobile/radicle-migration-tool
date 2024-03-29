package network.radicle.tools.migrate.core.radicle.actions;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import network.radicle.tools.migrate.core.radicle.Embed;
import network.radicle.tools.migrate.services.FilesService;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbedTest {
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    @Test
    public void testSerializationOfSingleEmbed() throws Exception {
        var embed = generateEmbed();
        var json = MAPPER.writeValueAsString(embed);
        var i = MAPPER.readValue(json, Embed.class);

        assertThat(i).isNotNull().usingRecursiveComparison().isEqualTo(embed);
    }

    @Test
    public void testGenerateGithubObjectId() throws Exception {
        var embed = generateEmbed();
        assertThat(embed).isNotNull();

        var fileService = new FilesService();
        var oid = fileService.calculateGitObjectId(embed.content);
        assertThat(oid).isNotNull().isEqualTo(embed.oid);
    }

   public static Embed generateEmbed() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/radicle/embed.json");
            var embed =  MAPPER.readValue(file, Embed.class);
            embed.name = embed.name + seed;
            return embed;
        } catch (Exception ex) {
            return null;
        }
    }
}
