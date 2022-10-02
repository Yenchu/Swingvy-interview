package idv.attendance.configuration;

import idv.attendance.configuration.security.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket restApi() {
        return new Docket(DocumentationType.OAS_30)
                .ignoredParameterTypes(CustomUserDetails.class)
                .select()
                .build();
    }
}
