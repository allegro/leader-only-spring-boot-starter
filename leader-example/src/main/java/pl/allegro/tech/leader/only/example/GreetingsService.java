package pl.allegro.tech.leader.only.example;

import org.springframework.stereotype.Service;
import pl.allegro.tech.leader.only.Leader;
import pl.allegro.tech.leader.only.LeaderOnly;

@Leader("greetings")
@Service
class GreetingsService {
    @LeaderOnly
    String greet(String name) {
        return "Hello " + name;
    }
}
