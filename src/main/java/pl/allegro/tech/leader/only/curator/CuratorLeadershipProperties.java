package pl.allegro.tech.leader.only.curator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties(prefix = "curator-leadership")
@ConstructorBinding
public class CuratorLeadershipProperties {

    private final String connectionString;
    private final Integer sleepTimeMs;
    private final Integer maxRetries;
    private final String pathPrefix;
    private final String username;
    private final String password;

    public CuratorLeadershipProperties(
            String connectionString,
            Integer sleepTimeMs,
            Integer maxRetries,
            String pathPrefix,
            String username,
            String password
    ) {
        this.connectionString = connectionString;
        this.sleepTimeMs = sleepTimeMs;
        this.maxRetries = maxRetries;
        this.pathPrefix = pathPrefix;
        this.username = username;
        this.password = password;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public int getSleepTimeMs() {
        return Optional.ofNullable(sleepTimeMs).orElse(1000);
    }

    public int getMaxRetries() {
        return Optional.ofNullable(maxRetries).orElse(3);
    }

    public String getPathPrefix() {
        return Optional.ofNullable(pathPrefix).orElse("/leader-only");
    }

    public boolean hasCredentials() {
        return hasText(username) && hasText(password);
    }

    public byte[] getCredentials() {
        return (username + ":" + password).getBytes();
    }
}
