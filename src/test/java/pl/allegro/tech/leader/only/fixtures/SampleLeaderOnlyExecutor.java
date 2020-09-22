package pl.allegro.tech.leader.only.fixtures;

import org.springframework.stereotype.Component;
import pl.allegro.tech.leader.only.api.Leader;
import pl.allegro.tech.leader.only.api.LeaderOnly;

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
