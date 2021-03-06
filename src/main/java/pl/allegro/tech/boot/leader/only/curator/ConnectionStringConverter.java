package pl.allegro.tech.boot.leader.only.curator;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import pl.allegro.tech.boot.leader.only.curator.CuratorLeadershipProperties.ConnectionString;

@ConfigurationPropertiesBinding
class ConnectionStringConverter implements Converter<String, ConnectionString> {
    @Nullable
    @Override
    public ConnectionString convert(@NonNull String source) {
        if (StringUtils.hasText(source)) {
            return new ConnectionString(source);
        }

        return null;
    }
}
