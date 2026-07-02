package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:api.properties"
})

public interface AuthConfig extends Config {
    @Key("auth.username")
    @DefaultValue("admin")
    String username();

    @Key("auth.password")
    @DefaultValue("password123")
    String password();
}
