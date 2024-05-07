package pl.allegro.tech.boot.leader.only.fixtures;

import pl.allegro.tech.boot.leader.only.api.Leader;
import pl.allegro.tech.boot.leader.only.api.LeaderOnly;
import pl.allegro.tech.boot.leader.only.api.OnLeadershipAcquisition;
import pl.allegro.tech.boot.leader.only.api.OnLeadershipLoss;

@Leader("sample")
public class SampleLeaderOnlyExecutor {
    private int leadershipAcquisitionCounter = 0;
    private int leadershipLossCounter = 0;

    @LeaderOnly
    public Integer calculateWhatIsTwoPlusTwo() {
        return 4;
    }

    @OnLeadershipAcquisition
    void leadershipAcquisitionCallback() {
        leadershipAcquisitionCounter++;
    }

    @OnLeadershipLoss
    void leadershipLossCallback() {
        leadershipLossCounter++;
    }

    public Integer calculateWhatIsOnePlusTwo() {
        return 3;
    }

    public int getLeadershipAcquisitionCounter() {
        return leadershipAcquisitionCounter;
    }

    public int getLeadershipLossCounter() {
        return leadershipLossCounter;
    }

    public void resetCounters() {
        leadershipAcquisitionCounter = 0;
        leadershipLossCounter = 0;
    }
}
