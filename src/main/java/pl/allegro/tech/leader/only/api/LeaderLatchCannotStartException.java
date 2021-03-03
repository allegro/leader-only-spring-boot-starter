package pl.allegro.tech.leader.only.api;

public class LeaderLatchCannotStartException extends IllegalStateException {
    public LeaderLatchCannotStartException(Exception e) {
        super("Cannot start LeaderLatch", e);
    }

    public LeaderLatchCannotStartException() {
        super("Cannot start LeaderLatch due timeout");
    }
}
