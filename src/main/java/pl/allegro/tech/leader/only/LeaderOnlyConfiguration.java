package pl.allegro.tech.leader.only;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LeaderOnlyConfiguration {
    @Bean
    LeadershipProxyFactory leaderOnlyProxyFactory(LeadershipFactory leadershipFactory) {
        return new LeadershipProxyFactory(leadershipFactory);
    }

    @Bean
    @ConditionalOnBean(LeadershipProxyFactory.class)
    LeaderOnlyBeanPostProcessor leaderOnlyBeanPostProcessor(LeadershipProxyFactory leadershipProxyFactory) {
        return new LeaderOnlyBeanPostProcessor(leadershipProxyFactory);
    }
}
