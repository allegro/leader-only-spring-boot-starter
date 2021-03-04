package pl.allegro.tech.boot.leader.only.curator;

import org.apache.curator.drivers.TracerDriver;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.tech.boot.leader.only.LeaderOnlyConfiguration;
import pl.allegro.tech.boot.leader.only.api.CuratorLeadershipCustomizer;
import pl.allegro.tech.boot.leader.only.api.LeadershipFactory;

import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.curator.framework.CuratorFrameworkFactory.builder;

@Configuration
@AutoConfigureBefore(LeaderOnlyConfiguration.class)
@EnableConfigurationProperties(CuratorLeadershipProperties.class)
public class CuratorLeadershipConfiguration {
    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnProperty(prefix = "curator-leadership", name = "connection-string")
    CuratorFramework leaderOnlyCuratorClient(
            CuratorLeadershipProperties properties,
            Optional<Stream<CuratorLeadershipCustomizer>> optionalCuratorFrameworkCustomizerProvider,
            Optional<EnsembleProvider> optionalEnsembleProvider,
            Optional<TracerDriver> optionalTracerDriverProvider) {
        final CuratorFrameworkFactory.Builder builder = builder()
                .connectString(properties.getConnectionString())
                .retryPolicy(properties.getRetryPolicy());

        properties.getAuth()
                .ifPresent(auth -> builder.authorization(auth.getSchema(), auth.getCredentials()));

        properties.getSessionTimeoutMs()
                .ifPresent(builder::sessionTimeoutMs);

        properties.getConnectionTimeoutMs()
                .ifPresent(builder::connectionTimeoutMs);

        properties.getWaitForShutdownTimeoutMs()
                .ifPresent(builder::waitForShutdownTimeoutMs);

        builder.namespace(properties.getNamespace());

        optionalEnsembleProvider.ifPresent(builder::ensembleProvider);

        optionalCuratorFrameworkCustomizerProvider
                .ifPresent(customizers -> customizers.forEach(it -> it.customize(builder)));

        final CuratorFramework client = builder.build();

        optionalTracerDriverProvider
                .ifPresent(tracerDriver -> Optional.ofNullable(client.getZookeeperClient())
                        .ifPresent(zookeeperClient -> zookeeperClient.setTracerDriver(tracerDriver)));

        return client;
    }

    @Bean
    @ConditionalOnBean(name = "leaderOnlyCuratorClient")
    LeadershipFactory curatorLeaderLatchFactory(CuratorFramework leaderOnlyCuratorClient) {
        return new CuratorLeadershipFactoryImpl(leaderOnlyCuratorClient);
    }

    @Bean
    ConnectionStringConverter connectionStringConverter() {
        return new ConnectionStringConverter();
    }
}
