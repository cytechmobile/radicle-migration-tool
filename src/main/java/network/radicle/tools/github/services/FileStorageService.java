package network.radicle.tools.github.services;

import io.quarkus.runtime.Quarkus;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@ApplicationScoped
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private Properties properties;
    private Path configFile;

    @PostConstruct
    public void init() {
        try {
            configFile = Path.of("config.properties");
            if (!Files.exists(configFile)) {
                Files.createFile(configFile);
            }
            properties = new Properties();
            properties.load(Files.newBufferedReader(configFile));
        } catch (IOException e) {
            logger.error("Failed to create / read the config.properties file.", e);
            Quarkus.asyncExit(1);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        try {
            properties.setProperty(key, value);
            properties.store(new BufferedWriter(new FileWriter(configFile.toFile())), null);
        } catch (IOException e) {
            logger.warn("Failed to persist the config.properties file.", e);
        }
    }
}
