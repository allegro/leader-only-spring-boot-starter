package pl.allegro.tech.boot.leader.only.api;

public class LeaderLatchCannotStopException extends IllegalStateException {
    public LeaderLatchCannotStopException(Exception e) {
        super("Cannot stop LeaderLatch", e);
    }
}
