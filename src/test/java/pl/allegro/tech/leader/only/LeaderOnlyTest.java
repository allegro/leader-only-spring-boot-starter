package pl.allegro.tech.leader.only;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import pl.allegro.tech.leader.only.fixtures.SampleApplication;
import pl.allegro.tech.leader.only.fixtures.SampleLeaderOnlyExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = SampleApplication.class)
@Import(LeaderOnlyTest.TestLeaderConfiguration.class)
class LeaderOnlyTest {

    private static final TestLeadership TEST_LEADERSHIP = new TestLeadership();

    @Autowired
    SampleLeaderOnlyExecutor underTest;

    @Test
    void shouldRunOnlyOnLeaderIfMethodHasLeaderOnlyAnnotation() {
        TEST_LEADERSHIP.setLeadership(true);

        Integer actual = underTest.calculateWhatIsTwoPlusTwo();

        assertEquals(4, actual);
    }

    @Test
    void shouldNotRunOnNotLeaderIfMethodHasLeaderOnlyAnnotation() {
        TEST_LEADERSHIP.setLeadership(false);

        Integer actual = underTest.calculateWhatIsTwoPlusTwo();

        assertNull(actual);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldRunRegardlessOfLeadershipIfMethodHasNoLeaderOnlyAnnotation(boolean leadership) {
        TEST_LEADERSHIP.setLeadership(leadership);

        Integer actual = underTest.calculateWhatIsOnePlusTwo();

        assertEquals(3, actual);
    }

    @TestConfiguration
    static class TestLeaderConfiguration {
        @Bean
        LeadershipFactory testLeadershipFactory() {
            return path -> TEST_LEADERSHIP;
        }
    }

    static class TestLeadership implements Leadership {
        private boolean leadership;

        @Override
        public boolean hasLeadership() {
            return leadership;
        }

        public void setLeadership(boolean leadership) {
            this.leadership = leadership;
        }
    }
}
