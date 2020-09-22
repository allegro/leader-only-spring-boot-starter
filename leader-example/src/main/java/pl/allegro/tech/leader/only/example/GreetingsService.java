package pl.allegro.tech.leader.only.example;

import org.springframework.stereotype.Service;
import pl.allegro.tech.leader.only.api.Leader;
import pl.allegro.tech.leader.only.api.LeaderOnly;

@Leader("greetings")
@Service
class GreetingsService {
    @LeaderOnly
    String greet(String name) {
        return "Hello " + name;
    }
}
