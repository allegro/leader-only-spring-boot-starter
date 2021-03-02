package pl.allegro.tech.leader.only.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import pl.allegro.tech.leader.only.api.Leadership;
import pl.allegro.tech.leader.only.api.LeadershipFactory;

import java.io.Closeable;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

final class CuratorLeadershipFactoryImpl implements LeadershipFactory, Closeable {
    private static final String ABSOLUTE_PATH = "/";

    private final ConcurrentHashMap<String, CuratorLeadership> leaderships = new ConcurrentHashMap<>();

    private final CuratorFramework client;

    public CuratorLeadershipFactoryImpl(
            CuratorFramework client
    ) {
        this.client = client;
    }

    @Override
    public Leadership of(String path) {
        final String absolutePath = Paths.get(ABSOLUTE_PATH, path).toString();
        if (!leaderships.containsKey(absolutePath)) {
            final LeaderLatch latch = new LeaderLatch(client, absolutePath);
            leaderships.put(absolutePath, new CuratorLeadership(latch));
        }

        return leaderships.get(absolutePath);
    }

    @Override
    public void close() {
        leaderships.values().forEach(CuratorLeadership::close);
    }
}
