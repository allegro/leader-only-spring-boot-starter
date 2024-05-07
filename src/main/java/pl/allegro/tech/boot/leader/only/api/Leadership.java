package pl.allegro.tech.boot.leader.only.api;

public interface Leadership {
    boolean hasLeadership();

    void registerLeadershipAcquisitionCallback(Runnable runnable);

    void registerLeadershipLossCallback(Runnable runnable);
}
