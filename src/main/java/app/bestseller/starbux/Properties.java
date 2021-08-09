package app.bestseller.starbux;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "app.bestseller")
@Data
@Configuration
public class Properties {
    private Integer maxPoolSize;
    private Integer corePoolSize;
    private Integer queueCapacity;
    private String threadNamePrefix;
}