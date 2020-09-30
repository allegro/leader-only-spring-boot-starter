package pl.allegro.tech.leader.only.api;

import org.apache.curator.framework.CuratorFrameworkFactory;

public interface CuratorLeadershipCustomizer {
    void customize(CuratorFrameworkFactory.Builder builder);
}
