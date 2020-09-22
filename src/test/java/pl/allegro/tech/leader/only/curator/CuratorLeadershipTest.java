package pl.allegro.tech.leader.only.curator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.allegro.tech.leader.only.fixtures.SampleApplication;
import pl.allegro.tech.leader.only.fixtures.SampleLeaderOnlyExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(classes = SampleApplication.class)
class CuratorLeadershipTest {

    private static final int PORT = 2181;

    @Container
    public static final GenericContainer<?> zookeeper = new GenericContainer<>("zookeeper:3.4.10")
            .withExposedPorts(PORT);

    @DynamicPropertySource
    static void zookeeperProperties(DynamicPropertyRegistry registry) {
        registry.add("curator-leadership.connection-string", () ->
                zookeeper.getContainerIpAddress() + ":" + zookeeper.getMappedPort(PORT));
        registry.add("curator-leadership.path-prefix", () -> "test/path");
    }

    @Autowired
    SampleLeaderOnlyExecutor underTest;

    @Test
    void shouldRespondOnlyOnLeader() {
        assertTrue(zookeeper.isRunning());

        Integer actual = underTest.calculateWhatIsTwoPlusTwo();

        assertEquals(4, actual);
    }
}
