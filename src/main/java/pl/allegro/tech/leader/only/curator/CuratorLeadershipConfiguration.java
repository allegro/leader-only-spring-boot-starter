package pl.allegro.tech.leader.only.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.tech.leader.only.LeaderOnlyConfiguration;
import pl.allegro.tech.leader.only.LeadershipFactory;

import static org.apache.curator.framework.CuratorFrameworkFactory.builder;

@Configuration
@AutoConfigureBefore(LeaderOnlyConfiguration.class)
@EnableConfigurationProperties(CuratorLeadershipProperties.class)
public class CuratorLeadershipConfiguration {
    private static final String AUTH_SCHEME = "digest";

    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnProperty(prefix = "curator-leadership", name = "connection-string")
    CuratorFramework leaderOnlyCuratorClient(CuratorLeadershipProperties properties) {
        final ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(properties.getSleepTimeMs(), properties.getMaxRetries());

        final CuratorFrameworkFactory.Builder builder = builder()
                .connectString(properties.getConnectionString())
                .retryPolicy(retryPolicy);

        if (properties.hasCredentials()) {
            builder.authorization(AUTH_SCHEME, properties.getCredentials());
        }

        return builder.build();
    }

    @Bean
    @ConditionalOnBean(name = "leaderOnlyCuratorClient")
    LeadershipFactory curatorLeaderLatchFactory(CuratorFramework leaderOnlyCuratorClient, CuratorLeadershipProperties properties) {
        return new CuratorLeadershipFactoryImpl(leaderOnlyCuratorClient, properties.getPathPrefix());
    }
}
