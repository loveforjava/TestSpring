package integration.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ua.ukrpost", "integration.helper"})
@PropertySource(value = "classpath:application-test.properties")
public class ApplicationConfigTest {
}
