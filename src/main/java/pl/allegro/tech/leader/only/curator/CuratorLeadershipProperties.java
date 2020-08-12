package pl.allegro.tech.leader.only.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryOneTime;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@ConfigurationProperties(prefix = "curator-leadership")
@ConstructorBinding
class CuratorLeadershipProperties {

    private final String connectionString;
    private final RetryPolicyProperties retry;
    private final AuthProperties auth;
    private final Integer sessionTimeoutMs;
    private final Integer connectionTimeoutMs;
    private final String pathPrefix;

    public CuratorLeadershipProperties(
            String connectionString,
            String pathPrefix,
            RetryPolicyProperties retry,
            AuthProperties auth,
            Integer sessionTimeoutMs,
            Integer connectionTimeout
    ) {
        this.connectionString = connectionString;
        this.pathPrefix = Optional.ofNullable(pathPrefix).orElse("/leader-only");
        this.retry = retry;
        this.auth = auth;
        this.sessionTimeoutMs = sessionTimeoutMs;
        this.connectionTimeoutMs = connectionTimeout;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getPathPrefix() {
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
        return Optional.ofNullable(sessionTimeoutMs);
    }

    public Optional<Integer> getConnectionTimeoutMs() {
        return Optional.ofNullable(connectionTimeoutMs);
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
