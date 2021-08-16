package app.bestseller.starbux;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Ebrahim Kh.
 */


@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableConfigurationProperties(value = Properties.class)
public class PropertiesTest {
    private @Autowired
    Properties properties;

    @Test
    @Transactional
    public void givenProperties_whenBindingPropertiesFileTest() {

    }
}
