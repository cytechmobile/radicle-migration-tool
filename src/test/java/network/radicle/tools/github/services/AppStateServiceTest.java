package network.radicle.tools.github.services;

import network.radicle.tools.github.core.radicle.actions.EmbedTest;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AppStateServiceTest {

    @Test
    public void testGetAndSetProperty() {
        var name = UUID.randomUUID().toString();
        var value = UUID.randomUUID().toString();

        var service = new AppStateService();
        service.path = "store.properties";
        service.init();
        service.setProperty(name, value);
        service.init();

        var property = service.getProperty(name);
        assertThat(property).isEqualTo(value);
    }

    @Test
    void testMimeTypeDetection() {
        var embed = EmbedTest.generateEmbed();
        assertThat(embed).isNotNull();

        var parts = embed.content.split(",");
        var expectedHeader = parts[0] + ",";

        var fileService = new FilesService();
        var actualHeader = fileService.getBase64Prefix(Base64.getDecoder().decode(parts[1]));
        assertThat(actualHeader).isEqualTo(expectedHeader);
    }
}
