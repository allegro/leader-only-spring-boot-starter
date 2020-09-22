# Leader Only Spring Boot Starter

## Why?

Sometimes it is crucial to perform some action only on one node. 
This library makes this mundane task easy. 

## How to use it?

There are two annotations, that allow you to select method
that is performed only on leader node.

First is `@Leader` used on whole class. It allows you to name
leader. Each leader has its name, so you can create multiple leaders
and each will have separate lock.

Then, there is `@LeaderOnly` annotation used on method. You can have
multiple methods inside leader with this annotation. Methods without
`@LeaderOnly` are performed as usual.

```java
import org.springframework.stereotype.Component;
import pl.allegro.tech.leader.only.Leader;
import pl.allegro.tech.leader.only.LeaderOnly;

@Component
@Leader("leader-identifier")
public class Sample {
    
    @LeaderOnly
    public Integer performActionOnlyOnLeader() {
        return veryExpensiveOperation();
    }

    public Integer performActionOnEveryNode() {
        return somethingCheapToPerform();
    }
}
``` 

See example project [here](./leader-example).

## Configuration

Zookeeper & Apache Curator are technologies that drives selecting leader.

```yaml
curator-leadership:
    connection-string: localhost:2181 # only required property
    session-timeout: 100
    connection-timeout: 100
    path-prefix: /leader-only
    retry:
      max-retries: 3
      max-sleep-time-ms: 1000
      base-sleep-time-ms: 200
    auth:
      scheme: digest
      username: username
      password: password
```

Dependencies that needs to be set in `build.grade`.

```groovy
implementation "pl.allegro.tech:leader-only-spring-boot-starter:1.0.0"
implementation "org.apache.zookeeper:zookeeper:3.4.10" 
```

## What if you don't want to use Zookeeper?

You can make your own `Leadership` implementation and add your `LeadershipFactory` bean to Spring context.
If you want to know more, check out this [example](src/test/java/pl/allegro/tech/leader/only/api/LeaderOnlyTest.java).