package pl.allegro.tech.boot.leader.only.api;

public final class ConnectionStringCannotBeEmptyException extends IllegalArgumentException {
    public ConnectionStringCannotBeEmptyException() {
        super("Provided connection string cannot be empty");
    }
}
