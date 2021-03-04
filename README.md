# Leader Only Spring Boot Starter

Sometimes it is crucial to perform some action only on one application node. 
This library makes this boring task easy.

- Integrates with [Spring Boot 2](https://github.com/spring-projects/spring-boot)
- Leverages [Apache Curator](https://curator.apache.org/)
- Handles multiple locks at once

## Installation

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>pl.allegro.tech</groupId>
        <artifactId>leader-only-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.zookeeper</groupId>
        <artifactId>zookeeper</artifactId>
        <version>3.6.2</version>
    </dependency>
</dependencies>
``` 

### Gradle

```groovy
dependecies {
    implementation "pl.allegro.tech:leader-only-spring-boot-starter:1.0.0"
    implementation "org.apache.zookeeper:zookeeper:3.6.2" 
}
```

## Usage

```java
import org.springframework.stereotype.Component;
import pl.allegro.tech.boot.leader.only.Leader;
import pl.allegro.tech.boot.leader.only.LeaderOnly;

@Leader("leader-identifier") // creates new leader latch with identifier
public class Sample {

    @LeaderOnly
    public Integer performActionOnlyOnLeader() {
        return veryExpensiveOperation(); // this will be performed only at leader node
    }

    public Integer performActionOnEveryNode() {
        return somethingCheapToPerform(); // this will be performed at all nodes
    }
}
``` 

`@Leader` annotation enhances `@Component` and will add a candidate 
for auto-detection  when using annotation-based configuration and classpath scanning.

## Configuration

```yaml
curator-leadership:
    connection-string: localhost:2181 # only required property
    namespace: /leader-only
    timeout:
      session: 100ms
      connection: 100ms
      wait-for-shutdown: 100ms
    retry:
      max-retries: 3
      max-sleep-time: 1s
      base-sleep-time: 200ms
    auth:
      scheme: digest
      username: username
      password: password
```

[Apache Zookeeper](https://zookeeper.apache.org/) & 
[Apache Curator](https://curator.apache.org/) 
are technologies that drives selecting leader.

## What if you don't want to use Zookeeper?

You can make your own `Leadership` implementation and add your `LeadershipFactory` bean to Spring context.
If you want to know more, check out this [example](src/test/java/pl/allegro/tech/leader/only/api/LeaderOnlyTest.java).