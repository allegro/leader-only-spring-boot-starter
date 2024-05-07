package pl.allegro.tech.boot.leader.only.fixtures;

import pl.allegro.tech.boot.leader.only.api.Leader;
import pl.allegro.tech.boot.leader.only.api.LeaderOnly;
import pl.allegro.tech.boot.leader.only.api.LeadershipChangeCallbacks;

@Leader("sample")
public class SampleLeaderOnlyExecutor implements LeadershipChangeCallbacks {
    private int leadershipAcquisitionCounter = 0;
    private int leadershipLossCounter = 0;

    @LeaderOnly
    public Integer calculateWhatIsTwoPlusTwo() {
        return 4;
    }

    public void onLeadershipAcquisition() {
        leadershipAcquisitionCounter++;
    }

    public void onLeadershipLoss() {
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
