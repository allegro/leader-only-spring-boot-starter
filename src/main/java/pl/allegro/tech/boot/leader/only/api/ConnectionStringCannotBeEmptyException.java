package pl.allegro.tech.boot.leader.only.api;

/**
 * Thrown when connection string is empty.
 */
public final class ConnectionStringCannotBeEmptyException extends IllegalArgumentException {
    /**
     * Creates new instance of {@link ConnectionStringCannotBeEmptyException}.
     */
    public ConnectionStringCannotBeEmptyException() {
        super("Provided connection string cannot be empty");
    }
}
