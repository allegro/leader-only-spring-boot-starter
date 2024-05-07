package pl.allegro.tech.boot.leader.only.curator;

import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import pl.allegro.tech.boot.leader.only.api.LeaderLatchCannotStartException;
import pl.allegro.tech.boot.leader.only.api.LeaderLatchCannotStopException;
import pl.allegro.tech.boot.leader.only.api.Leadership;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.slf4j.LoggerFactory.getLogger;

final class CuratorLeadership implements Leadership, Closeable {

    private static final Logger logger = getLogger(CuratorLeadership.class);

    private final LeaderLatch leaderLatch;

    public CuratorLeadership(LeaderLatch leaderLatch) {
        this.leaderLatch = leaderLatch;

        try {
            leaderLatch.start();
        } catch (Exception e) {
            throw new LeaderLatchCannotStartException(e);
        }

        leaderLatch.addListener(new LeaderLatchListener() {
            final String hostname = resolveHostname();

            @Override
            public void isLeader() {
                logger.info("{} is selected for the leader", hostname);
            }

            @Override
            public void notLeader() {
                logger.info("{} is no longer the leader", hostname);
            }

            private String resolveHostname() {
                try {
                    return InetAddress.getLocalHost().getHostName();
                } catch (UnknownHostException e) {
                    return "???";
                }
            }
        });
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
            throw new LeaderLatchCannotStopException(e);
        }
    }
}
