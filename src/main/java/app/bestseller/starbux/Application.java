package app.bestseller.starbux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by Ebrahim Kh.
 */

@EnableCaching
@SpringBootApplication
@EntityScan("app.bestseller.starbux.domain")
@EnableJpaRepositories("app.bestseller.starbux.repository")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
