package pl.allegro.tech.leader.only.curator;

import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.tech.leader.only.Leadership;

import java.io.Closeable;
import java.io.IOException;

public class CuratorLeadership implements Leadership, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(CuratorLeadership.class);

    private final LeaderLatch leaderLatch;

    public CuratorLeadership(LeaderLatch leaderLatch) {
        this.leaderLatch = leaderLatch;

        try {
            leaderLatch.start();
        } catch (Exception e) {
            logger.error("Cannot start LeaderLatch", e);
        }
    }

    @Override
    public boolean hasLeadership() {
        return leaderLatch.hasLeadership();
    }

    @Override
    public void close() {
        try {
            leaderLatch.close();
        } catch (IOException e) {
            logger.error("Cannot close LeaderLatch", e);
        }
    }
}
