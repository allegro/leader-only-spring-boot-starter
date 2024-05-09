# Leader Only Spring Boot Starter

[![Java CI with Gradle](https://github.com/allegro/leader-only-spring-boot-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/allegro/leader-only-spring-boot-starter/actions/workflows/ci.yml)
![Maven Central](https://img.shields.io/maven-central/v/pl.allegro.tech.boot/leader-only-spring-boot-starter)

Sometimes it is crucial to perform some action only on one application node. 
This library makes this boring task easy.

- Integrates with [Spring Boot 3](https://github.com/spring-projects/spring-boot)
- Leverages [Apache Curator](https://curator.apache.org/)
- Handles multiple locks at once

## Installation

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>pl.allegro.tech.boot</groupId>
        <artifactId>leader-only-spring-boot-starter</artifactId>
        <version>1.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.zookeeper</groupId>
        <artifactId>zookeeper</artifactId>
        <version>3.9.0</version>
    </dependency>
</dependencies>
``` 

### Gradle

```groovy
dependecies {
    implementation "pl.allegro.tech:leader-only-spring-boot-starter:1.1.0"
    implementation "org.apache.zookeeper:zookeeper:3.9.0" 
}
```

## Usage

```java
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

It is also possible to handle leadership status changes. To do so bean annotated with @Leader has to
implement LeadershipChangeCallbacks interface.

```java
import pl.allegro.tech.boot.leader.only.Leader;
import pl.allegro.tech.boot.leader.only.LeaderOnly;
import pl.allegro.tech.boot.leader.only.api.LeadershipChangeCallbacks;

@Leader("leader-identifier") // creates new leader latch with identifier
public class Sample implements LeadershipChangeCallbacks {

    @LeaderOnly
    public Integer performActionOnlyOnLeader() {
        return veryExpensiveOperation(); // this will be performed only at leader node
    }

    public void onLeadershipAcquisition() {
        leadershipSetUp(); // this will be performed when node becomes a leader
    }

    public void onLeadershipLoss() {
        leadershipCleanUp();  // this will be performed when node stops being a leader
    }

    public Integer performActionOnEveryNode() {
        return somethingCheapToPerform(); // this will be performed at all nodes
    }
}
```

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
If you want to know more, check out this [example](src/test/java/pl/allegro/tech/boot/leader/only/api/LeaderOnlyTest.java).
