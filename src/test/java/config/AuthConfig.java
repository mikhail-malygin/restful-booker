package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:api.properties"
})

public interface AuthConfig extends Config {
    @Key("auth.username")
    String username();

    @Key("auth.password")
    String password();
}
