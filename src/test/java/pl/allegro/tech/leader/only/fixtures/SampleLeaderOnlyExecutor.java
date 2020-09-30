package pl.allegro.tech.leader.only.fixtures;

import pl.allegro.tech.leader.only.api.Leader;
import pl.allegro.tech.leader.only.api.LeaderOnly;

@Leader("sample")
public class SampleLeaderOnlyExecutor {

    @LeaderOnly
    public Integer calculateWhatIsTwoPlusTwo() {
        return 4;
    }

    public Integer calculateWhatIsOnePlusTwo() {
        return 3;
    }
}
