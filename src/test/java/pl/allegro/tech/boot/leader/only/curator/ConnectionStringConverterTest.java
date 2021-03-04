package pl.allegro.tech.boot.leader.only.curator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.allegro.tech.boot.leader.only.curator.CuratorLeadershipProperties.ConnectionString;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConnectionStringConverterTest {

    private final ConnectionStringConverter underTest = new ConnectionStringConverter();

    @ParameterizedTest
    @ValueSource(strings = {"localhost:2181", "127.0.0.1:3000,127.0.0.1:3001"})
    void shouldConvertStringToConnectionString(String source) {
        // when
        final ConnectionString connectionString = underTest.convert(source);

        // then
        assertNotNull(connectionString);
        assertEquals(connectionString.getValue(), source);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void shouldNotConvertEmptyStringToConnectionString(String source) {
        // when
        final ConnectionString connectionString = underTest.convert(source);

        // then
        assertNull(connectionString);
    }
}