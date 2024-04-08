package network.radicle.tools.migrate.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

@ApplicationScoped
public class AppStateService {
    private static final Logger logger = LoggerFactory.getLogger(AppStateService.class);

    public enum Property {
        LAST_RUN("%s.%s.%s.radicle.%s.lastRunInMillis");

        public final String value;

        Property(String value) {
            this.value = value;
        }
    }

    public enum Service {
        GITHUB, GITLAB
    }

    private Properties properties;
    private Path configFile;

    @Inject Config config;

    @ConfigProperty(name = "storage.file.path")
    String path;

    @PostConstruct
    public void init() {
        configFile = Path.of(path);
        try {
            if (!Files.exists(configFile)) {
                Files.createFile(configFile);
            }
            properties = new Properties();
            properties.load(Files.newBufferedReader(configFile));
        } catch (IOException e) {
            properties = null;
            logger.error("Failed to initialize the {} file. Please check the permissions and/or consult the message. " +
                    "Message: {}", configFile.toAbsolutePath(), e.getMessage());
        }
    }

    public boolean isInitialized() {
        return this.properties != null;
    }

    public String getProperty(String key) {
        if (!isInitialized()) {
            return null;
        }
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        if (!isInitialized()) {
            return;
        }
        try {
            properties.setProperty(key, value);
            properties.store(new BufferedWriter(new FileWriter(configFile.toFile())), null);
        } catch (IOException e) {
            logger.warn("Failed to persist the {} file: {}", configFile.toAbsolutePath(), e.getMessage());
        }
    }

    public String getProperty(Service service, Property property, String... args) {
        return getProperty(getPropertyName(service, property, args));
    }

    public void setProperty(Service service, Property property, String value, String... args) {
        setProperty(getPropertyName(service, property, args), value);
    }

    private String getPropertyName(Service service, Property prop, String... args) {
        var owner = "";
        var repo = "";

        switch (service) {
            case GITHUB -> {
                owner = config.github().owner();
                repo = config.github().repo();
            }
            case GITLAB -> {
                owner = config.gitlab().namespace();
                repo = config.gitlab().project();
            }
            default -> throw new IllegalStateException("Unexpected value: " + service);
        }

        var radProject = config.radicle().project().replace("rad:", "");

        var params = Stream.concat(Arrays.stream(new String[]{service.name().toLowerCase(), owner, repo, radProject}),
                        Arrays.stream(args)).toArray();

        return String.format(prop.value, params);
    }
}
