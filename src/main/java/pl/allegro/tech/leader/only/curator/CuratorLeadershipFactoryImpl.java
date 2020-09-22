package pl.allegro.tech.leader.only.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.tech.leader.only.api.Leadership;
import pl.allegro.tech.leader.only.api.LeadershipFactory;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

final class CuratorLeadershipFactoryImpl implements LeadershipFactory, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(CuratorLeadershipFactoryImpl.class);

    private final ConcurrentHashMap<String, CuratorLeadership> leaderships = new ConcurrentHashMap<>();

    private final CuratorFramework client;
    private final String prefix;

    public CuratorLeadershipFactoryImpl(
            CuratorFramework client,
            Path pathPrefix
    ) {
        this.client = client;
        this.prefix = pathPrefix.toString();

        logger.debug("Curator LeaderLatch will be served from {}", prefix);
    }

    @Override
    public Leadership of(String path) {
        if (!leaderships.containsKey(path)) {
            final String prefixedPath = Paths.get(prefix, path).toString();
            final LeaderLatch latch = new LeaderLatch(client, prefixedPath);
            leaderships.put(path, new CuratorLeadership(latch));
        }

        return leaderships.get(path);
    }

    @Override
    public void close() {
        leaderships.forEach((key, leadership) -> leadership.close());
    }
}
