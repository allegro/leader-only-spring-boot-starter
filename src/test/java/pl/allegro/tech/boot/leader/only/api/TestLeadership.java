package pl.allegro.tech.boot.leader.only.api;

import java.util.ArrayList;
import java.util.List;

class TestLeadership implements Leadership {
    private boolean leadership;
    private final List<Runnable> leadershipAcquisitionCallbacks = new ArrayList<>();
    private final List<Runnable> leadershipLossCallbacks = new ArrayList<>();

    @Override
    public boolean hasLeadership() {
        return leadership;
    }

    @Override
    public void registerLeadershipAcquisitionCallback(Runnable callback) {
        leadershipAcquisitionCallbacks.add(callback);
    }

    @Override
    public void registerLeadershipLossCallback(Runnable callback) {
        leadershipLossCallbacks.add(callback);
    }

    public void setLeadership(boolean leadership) {
        if (leadership) {
            leadershipAcquisitionCallbacks.forEach(Runnable::run);
        } else {
            leadershipLossCallbacks.forEach(Runnable::run);

        }
        this.leadership = leadership;
    }

    private boolean isLeadershipAcquired(boolean leadership) {
        return !this.leadership && leadership;
    }

    private boolean isLeadershipLost(boolean leadership) {
        return this.leadership && !leadership;
    }

}
