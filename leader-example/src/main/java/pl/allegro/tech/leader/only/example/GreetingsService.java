package pl.allegro.tech.leader.only.example;

import pl.allegro.tech.leader.only.api.Leader;
import pl.allegro.tech.leader.only.api.LeaderOnly;

@Leader("greetings")
class GreetingsService {
    @LeaderOnly
    String greet(String name) {
        return "Hello " + name;
    }
}
