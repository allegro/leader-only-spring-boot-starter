package pl.allegro.tech.boot.leader.only;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.lang.NonNull;
import pl.allegro.tech.boot.leader.only.api.*;

import java.io.Closeable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

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
        Class<?> objClass = object.getClass();
        Arrays.stream(objClass.getDeclaredMethods())
                .filter(method -> findAnnotation(method, OnLeadershipAcquisition.class) != null)
                .filter(method -> method.getParameterCount() == 0)
                .filter(AccessibleObject::trySetAccessible)
                .forEach(method -> {
                    leadership.registerLeadershipAcquisitionCallback(() -> invoke(object, method));
                    logger.info("Method {}.{} registered as leadership acquisition callback",
                            objClass.getCanonicalName(), method.getName());
                });
        Arrays.stream(objClass.getDeclaredMethods())
                .filter(method -> findAnnotation(method, OnLeadershipLoss.class) != null)
                .filter(method -> method.getParameterCount() == 0)
                .filter(AccessibleObject::trySetAccessible)
                .forEach(method -> {
                    leadership.registerLeadershipLossCallback(() -> invoke(object, method));
                    logger.info("Method {}.{} registered as leadership loss callback",
                            objClass.getCanonicalName(), method.getName());
                });
    }

    private <T> void invoke(T object, Method method) {
        try {
            method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new LeaderChangeCallbackExecutionException(e);
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
