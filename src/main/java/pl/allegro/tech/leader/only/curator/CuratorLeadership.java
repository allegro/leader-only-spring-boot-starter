package pl.allegro.tech.leader.only.curator;

import org.apache.curator.framework.recipes.leader.LeaderLatch;
import pl.allegro.tech.leader.only.api.LeaderLatchCannotStartException;
import pl.allegro.tech.leader.only.api.LeaderLatchCannotStopException;
import pl.allegro.tech.leader.only.api.Leadership;

import java.io.Closeable;
import java.io.IOException;

final class CuratorLeadership implements Leadership, Closeable {

    private final LeaderLatch leaderLatch;

    public CuratorLeadership(LeaderLatch leaderLatch) {
        this.leaderLatch = leaderLatch;

        try {
            leaderLatch.start();
        } catch (Exception e) {
            throw new LeaderLatchCannotStartException(e);
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
            throw new LeaderLatchCannotStopException(e);
        }
    }
}
