package pl.allegro.tech.leader.only.curator.fixtures;

import org.springframework.stereotype.Component;
import pl.allegro.tech.leader.only.Leader;
import pl.allegro.tech.leader.only.LeaderOnly;

@Leader("sample")
@Component
public class SampleLeaderOnlyExecutor {

    @LeaderOnly
    public Integer calculateWhatIsTwoPlusTwo() {
        return 4;
    }

    public Integer calculateWhatIsOnePlusTwo() {
        return 3;
    }
}
