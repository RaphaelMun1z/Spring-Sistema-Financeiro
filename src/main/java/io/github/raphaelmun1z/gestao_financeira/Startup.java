package io.github.raphaelmun1z.gestao_financeira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Startup {

    static void main(String[] args) {
        SpringApplication.run(Startup.class, args);
    }

}
