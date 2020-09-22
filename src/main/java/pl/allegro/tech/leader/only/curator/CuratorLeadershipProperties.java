package pl.allegro.tech.leader.only.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryOneTime;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.convert.DurationUnit;
import pl.allegro.tech.leader.only.api.ConnectionStringCannotBeEmptyException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties(prefix = "curator-leadership")
@ConstructorBinding
class CuratorLeadershipProperties {
    private static final Path DEFAULT_PATH = Paths.get("/leader-only");
    private static final Path ABSOLUTE_START = Paths.get("/");

    private final ConnectionString connectionString;
    private final RetryPolicyProperties retry;
    private final AuthProperties auth;
    @DurationUnit(MILLIS)
    private final Duration sessionTimeout;
    @DurationUnit(MILLIS)
    private final Duration connectionTimeout;
    private final Path pathPrefix;

    public CuratorLeadershipProperties(
            ConnectionString connectionString,
            Path pathPrefix,
            RetryPolicyProperties retry,
            AuthProperties auth,
            Duration sessionTimeout,
            Duration connectionTimeout
    ) {
        this.connectionString = connectionString;
        this.pathPrefix = Optional.ofNullable(pathPrefix)
                .map(ABSOLUTE_START::resolve)
                .orElse(DEFAULT_PATH);
        this.retry = retry;
        this.auth = auth;
        this.sessionTimeout = sessionTimeout;
        this.connectionTimeout = connectionTimeout;
    }

    public String getConnectionString() {
        return Optional.ofNullable(connectionString)
                .map(ConnectionString::getValue)
                .orElseThrow(ConnectionStringCannotBeEmptyException::new);
    }

    public Path getPathPrefix() {
        return pathPrefix;
    }

    public RetryPolicy getRetryPolicy() {
        return Optional.ofNullable(retry)
                .map(RetryPolicyProperties::getExponentialBackoffRetry)
                .orElseGet(() -> new RetryOneTime(100));
    }

    public Optional<AuthProperties> getAuth() {
        return Optional.ofNullable(auth)
                .filter(AuthProperties::hasCredentials);
    }

    public Optional<Integer> getSessionTimeoutMs() {
        return Optional.ofNullable(sessionTimeout)
                .map(Duration::toMillis)
                .map(Long::intValue);
    }

    public Optional<Integer> getConnectionTimeoutMs() {
        return Optional.ofNullable(connectionTimeout)
                .map(Duration::toMillis)
                .map(Long::intValue);
    }

    static class ConnectionString {
        private final String value;

        ConnectionString(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static class RetryPolicyProperties {
        private final Integer maxRetries;
        private final Integer maxSleepTimeMs;
        private final Integer baseSleepTimeMs;

        RetryPolicyProperties(Integer maxRetries, Integer maxSleepTimeMs, Integer baseSleepTimeMs) {
            this.maxRetries = Optional.ofNullable(maxRetries).orElse(3);
            this.maxSleepTimeMs = Optional.ofNullable(maxSleepTimeMs).orElse(1000);
            this.baseSleepTimeMs = Optional.ofNullable(baseSleepTimeMs).orElse(200);
        }

        public RetryPolicy getExponentialBackoffRetry() {
            return new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries, maxSleepTimeMs);
        }
    }

    static class AuthProperties {
        private final String username;
        private final String password;
        private final String schema;

        AuthProperties(String username, String password, String schema) {
            this.username = username;
            this.password = password;
            this.schema = Optional.ofNullable(schema).orElse("digest");
        }

        public String getSchema() {
            return schema;
        }

        private boolean hasCredentials() {
            return hasText(username) && hasText(password);
        }

        public byte[] getCredentials() {
            return (username + ":" + password).getBytes();
        }
    }
}
