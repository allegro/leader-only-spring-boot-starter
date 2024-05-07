package pl.allegro.tech.boot.leader.only.api;

public class LeaderChangeCallbackExecutionException extends RuntimeException {
    public LeaderChangeCallbackExecutionException(Exception e) {
        super("Cannot execute leader change callback", e);
    }
}
