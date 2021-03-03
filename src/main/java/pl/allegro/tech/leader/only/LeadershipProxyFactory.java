package pl.allegro.tech.leader.only;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import pl.allegro.tech.leader.only.api.LeaderOnly;
import pl.allegro.tech.leader.only.api.Leadership;
import pl.allegro.tech.leader.only.api.LeadershipFactory;

import java.io.Closeable;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

class LeadershipProxyFactory {
    private final LeadershipFactory leadershipFactory;

    LeadershipProxyFactory(LeadershipFactory leadershipFactory) {
        this.leadershipFactory = leadershipFactory;
    }

    @SuppressWarnings("unchecked")
    <T> T getProxy(T object, String path) {
        Leadership leadership = leadershipFactory.of(path);
        return (T) createProxy(object, leadership).getProxy();
    }

    private <T> ProxyFactory createProxy(T object, Leadership leadership) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(object.getClass());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setTarget(object);
        proxyFactory.addAdvice(new LeaderOnlyMethodInterceptor(leadership));
        proxyFactory.addInterface(Closeable.class);
        return proxyFactory;
    }

    private static class LeaderOnlyMethodInterceptor implements MethodInterceptor {
        private final Leadership leadership;

        public LeaderOnlyMethodInterceptor(Leadership leadership) {
            this.leadership = leadership;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            LeaderOnly annotation = findAnnotation(invocation.getMethod(), LeaderOnly.class);

            if (annotation != null) {
                if (leadership.hasLeadership()) {
                    return invocation.proceed();
                }

                return null;
            } else {
                return invocation.proceed();
            }
        }
    }
}
