package ru.vas.dataservice.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;
import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySources;
import org.jasypt.digest.config.DigesterConfig;
import org.jasypt.digest.config.SimpleDigesterConfig;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EncryptablePropertySources(value = {
        @EncryptablePropertySource("bootstrap.properties"),
})
public class JasyptConfig {

    @Bean
    @ConfigurationProperties(prefix = "jasypt.password-encryptor")
    protected DigesterConfig digesterConfig() {
        return new SimpleDigesterConfig();
    }

    @Bean(name = "baseEncryptor")
    public PasswordEncryptor passwordEncryptor() {
        ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
        passwordEncryptor.setConfig(digesterConfig());
        return passwordEncryptor;
    }
}
