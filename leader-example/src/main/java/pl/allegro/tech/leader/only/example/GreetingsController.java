package pl.allegro.tech.leader.only.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GreetingsController {

    private final GreetingsService service;

    GreetingsController(GreetingsService service) {
        this.service = service;
    }

    @GetMapping("/greetings/{name}")
    public String greetings(@PathVariable("name") String name) {
        return service.greet(name);
    }
}
