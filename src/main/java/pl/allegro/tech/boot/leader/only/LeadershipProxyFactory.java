package pl.allegro.tech.boot.leader.only;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.lang.NonNull;
import pl.allegro.tech.boot.leader.only.api.LeaderOnly;
import pl.allegro.tech.boot.leader.only.api.Leadership;
import pl.allegro.tech.boot.leader.only.api.LeadershipAcquisitionCallback;
import pl.allegro.tech.boot.leader.only.api.LeadershipFactory;
import pl.allegro.tech.boot.leader.only.api.LeadershipLossCallback;

import java.io.Closeable;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

final class LeadershipProxyFactory {
    private static final Logger logger = getLogger(LeadershipProxyFactory.class);

    private final LeadershipFactory leadershipFactory;

    LeadershipProxyFactory(LeadershipFactory leadershipFactory) {
        this.leadershipFactory = leadershipFactory;
    }

    @SuppressWarnings("unchecked")
    <T> T getProxy(@NonNull T object, @NonNull String path) {
        Leadership leadership = leadershipFactory.of(path);
        initializeCallbacks(object, leadership);
        return (T) createProxy(object, leadership).getProxy();
    }

    private <T> void initializeCallbacks(@NonNull T object, @NonNull Leadership leadership) {
        String canonicalName = object.getClass().getCanonicalName();
        if (object instanceof LeadershipAcquisitionCallback) {
            leadership.registerLeadershipAcquisitionCallback(() ->
                    ((LeadershipAcquisitionCallback)object).onLeadershipAcquisition());
            logger.info("{} registered as leadership acquisition callback", canonicalName);
        }
        if (object instanceof LeadershipLossCallback) {
            leadership.registerLeadershipLossCallback(() ->
                    ((LeadershipLossCallback)object).onLeadershipLoss());
            logger.info("{} registered as leadership loss callback", canonicalName);
        }
    }

    private <T> ProxyFactory createProxy(@NonNull T object, @NonNull Leadership leadership) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(object.getClass());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setTarget(object);
        proxyFactory.addAdvice(new LeaderOnlyMethodInterceptor(leadership));
        proxyFactory.addInterface(Closeable.class);
        return proxyFactory;
    }

    private static final class LeaderOnlyMethodInterceptor implements MethodInterceptor {
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
