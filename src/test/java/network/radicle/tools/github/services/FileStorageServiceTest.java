package network.radicle.tools.github.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

public class FileStorageServiceTest {

    @Test
    public void testGetAndSetProperty() {
        var name = UUID.randomUUID().toString();
        var value = UUID.randomUUID().toString();

        var service = new FileStorageService();
        service.path = "store.properties";
        service.init();
        service.setProperty(name, value);
        service.init();

        var property = service.getProperty(name);
        assertThat(property).isEqualTo(value);
    }
}
