package pl.allegro.tech.boot.leader.only.api;

import org.jspecify.annotations.NonNull;

public interface LeadershipFactory {
    Leadership of(@NonNull String path);
}
