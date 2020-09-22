package pl.allegro.tech.leader.only.curator;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import pl.allegro.tech.leader.only.curator.CuratorLeadershipProperties.ConnectionString;

@ConfigurationPropertiesBinding
public class ConnectionStringConverter implements Converter<String, ConnectionString> {
    @Override
    public ConnectionString convert(@NonNull String source) {
        if (StringUtils.hasText(source)) {
            return new ConnectionString(source);
        }

        return null;
    }
}
