package pl.allegro.tech.leader.only.api;

import org.springframework.lang.NonNull;

public interface LeadershipFactory {
    Leadership of(@NonNull String path);
}
