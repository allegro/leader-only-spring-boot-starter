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
    private static final Path DEFAULT_NAMESPACE = Paths.get("leader-only");

    private final ConnectionString connectionString;
    private final RetryPolicyProperties retry;
    private final AuthProperties auth;
    private final TimeoutProperties timeout;
    private final Path namespace;

    public CuratorLeadershipProperties(
            ConnectionString connectionString,
            Path namespace,
            RetryPolicyProperties retry,
            AuthProperties auth,
            TimeoutProperties timeout) {
        this.connectionString = connectionString;
        this.namespace = Optional.ofNullable(namespace)
                .orElse(DEFAULT_NAMESPACE);
        this.retry = retry;
        this.auth = auth;
        this.timeout = timeout;
    }

    public String getConnectionString() {
        return Optional.ofNullable(connectionString)
                .map(ConnectionString::getValue)
                .orElseThrow(ConnectionStringCannotBeEmptyException::new);
    }

    public String getNamespace() {
        return namespace.toString();
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
        return Optional.ofNullable(timeout)
                .map(TimeoutProperties::getSession)
                .map(Duration::toMillis)
                .map(Long::intValue);
    }

    public Optional<Integer> getConnectionTimeoutMs() {
        return Optional.ofNullable(timeout)
                .map(TimeoutProperties::getConnection)
                .map(Duration::toMillis)
                .map(Long::intValue);
    }

    public Optional<Integer> getWaitForShutdownTimeoutMs() {
        return Optional.ofNullable(timeout)
                .map(TimeoutProperties::getWaitForShutdown)
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
        private static final int DEFAULT_BASE_SLEEP_TIME_MS = 200;
        private static final int DEFAULT_MAX_SLEEP_TIME_MS = 1000;

        private final Integer maxRetries;
        private final Duration maxSleepTime;
        private final Duration baseSleepTime;

        RetryPolicyProperties(Integer maxRetries, Duration maxSleepTime, Duration baseSleepTime) {
            this.maxRetries = Optional.ofNullable(maxRetries).orElse(3);
            this.maxSleepTime = maxSleepTime;
            this.baseSleepTime = baseSleepTime;
        }

        public RetryPolicy getExponentialBackoffRetry() {
            return new ExponentialBackoffRetry(getBaseSleepTime(), maxRetries, getMaxSleepTime());
        }

        private int getBaseSleepTime() {
            return Optional.ofNullable(baseSleepTime)
                    .map(Duration::toMillis)
                    .map(Long::intValue)
                    .orElse(DEFAULT_BASE_SLEEP_TIME_MS);
        }

        private int getMaxSleepTime() {
            return Optional.ofNullable(maxSleepTime)
                    .map(Duration::toMillis)
                    .map(Long::intValue)
                    .orElse(DEFAULT_MAX_SLEEP_TIME_MS);
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

    static class TimeoutProperties {
        @DurationUnit(MILLIS)
        private final Duration session;
        @DurationUnit(MILLIS)
        private final Duration connection;
        @DurationUnit(MILLIS)
        private final Duration waitForShutdown;

        TimeoutProperties(Duration session, Duration connection, Duration waitForShutdown) {
            this.session = session;
            this.connection = connection;
            this.waitForShutdown = waitForShutdown;
        }

        public Duration getSession() {
            return session;
        }

        public Duration getConnection() {
            return connection;
        }

        public Duration getWaitForShutdown() {
            return waitForShutdown;
        }
    }
}
