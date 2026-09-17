package pl.allegro.tech.boot.leader.only;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import pl.allegro.tech.boot.leader.only.api.LeadershipFactory;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class LeaderOnlyConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LeaderOnlyConfiguration.class))
            .withBean(LeadershipFactory.class, () -> path -> null);

    @Test
    void shouldNotCreateBeansBeforeAllBeanPostProcessorsAreRegistered(CapturedOutput output) {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(LeaderOnlyBeanPostProcessor.class);
            assertThat(output).doesNotContain("not eligible for getting processed by all BeanPostProcessors");
        });
    }
}
