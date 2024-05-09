package pl.allegro.tech.boot.leader.only.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import pl.allegro.tech.boot.leader.only.fixtures.SampleApplication;
import pl.allegro.tech.boot.leader.only.fixtures.SampleLeaderOnlyExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = SampleApplication.class)
@Import(LeaderChangeCallbacksTest.TestLeaderConfiguration.class)
class LeaderChangeCallbacksTest {

    private static final TestLeadership TEST_LEADERSHIP = new TestLeadership();

    @Autowired
    SampleLeaderOnlyExecutor underTest;

    @AfterEach
    public void cleanUp() {
        underTest.resetCounters();
    }

    @Test
    void shouldExecuteLeadershipAcquisitionCallbackWhenSetAsLeader() {
        TEST_LEADERSHIP.setLeadership(true);

        int actual = underTest.getLeadershipAcquisitionCounter();

        assertEquals(1, actual);
    }

    @Test
    void shouldNotExecuteLeadershipLossCallbackWhenSetAsLeader() {
        TEST_LEADERSHIP.setLeadership(true);

        int actual = underTest.getLeadershipLossCounter();

        assertEquals(0, actual);
    }

    @Test
    void shouldExecuteLeadershipLossCallbackWhenSetAsNotLeader() {
        TEST_LEADERSHIP.setLeadership(false);

        int actual = underTest.getLeadershipLossCounter();

        assertEquals(1, actual);
    }

    @Test
    void shouldNotExecuteLeadershipAcquisitionCallbackWhenSetAsNotLeader() {
        TEST_LEADERSHIP.setLeadership(false);

        int actual = underTest.getLeadershipAcquisitionCounter();

        assertEquals(0, actual);
    }

    @TestConfiguration
    static class TestLeaderConfiguration {
        @Bean
        LeadershipFactory testLeadershipFactory() {
            return path -> TEST_LEADERSHIP;
        }
    }
}
