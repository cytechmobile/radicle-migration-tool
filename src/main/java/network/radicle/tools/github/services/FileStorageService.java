package network.radicle.tools.github.services;

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
        configFile = Path.of("config.properties");
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
        if (!isInitialized()){
            return null;
        }
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        if (!isInitialized()){
            return;
        }
        try {
            properties.setProperty(key, value);
            properties.store(new BufferedWriter(new FileWriter(configFile.toFile())), null);
        } catch (IOException e) {
            logger.warn("Failed to persist the config.properties file: {}", e.getMessage());
        }
    }
}
