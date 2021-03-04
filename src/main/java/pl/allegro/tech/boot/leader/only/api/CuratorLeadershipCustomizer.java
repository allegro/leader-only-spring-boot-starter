package pl.allegro.tech.boot.leader.only.api;

import org.apache.curator.framework.CuratorFrameworkFactory;

public interface CuratorLeadershipCustomizer {
    void customize(CuratorFrameworkFactory.Builder builder);
}
