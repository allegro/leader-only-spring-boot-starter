package pl.allegro.tech.leader.only;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

class LeaderOnlyBeanPostProcessor implements BeanPostProcessor {

    private final LeadershipProxyFactory leadershipProxyFactory;

    public LeaderOnlyBeanPostProcessor(LeadershipProxyFactory leadershipProxyFactory) {
        this.leadershipProxyFactory = leadershipProxyFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, @Nullable String beanName) {
        Leader annotation = findAnnotation(bean.getClass(), Leader.class);

        if (annotation == null) {
            return bean;
        }

        return leadershipProxyFactory.getProxy(bean, annotation.value());
    }
}