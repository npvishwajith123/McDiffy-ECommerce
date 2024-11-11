package com.np.mcdiffy;

import com.np.mcdiffy.utilities.CommonUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity(debug = false)
public class McDiffyEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(McDiffyEcommerceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(String[] args) {
        return runner -> {
			CommonUtils.createProgramBanner();
        };
    }

}
