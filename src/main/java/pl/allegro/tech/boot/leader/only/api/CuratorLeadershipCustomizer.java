package pl.allegro.tech.boot.leader.only.api;

import org.apache.curator.framework.CuratorFrameworkFactory;

/**
 * Customizer for CuratorFrameworkFactory.Builder
 */
public interface CuratorLeadershipCustomizer {
    /**
     * Customize CuratorFrameworkFactory.Builder
     *
     * @param builder CuratorFrameworkFactory.Builder
     */
    void customize(CuratorFrameworkFactory.Builder builder);
}
