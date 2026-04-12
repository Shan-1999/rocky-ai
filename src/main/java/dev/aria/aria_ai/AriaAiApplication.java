package dev.aria.aria_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "dev.aria")
@EntityScan(basePackages = "dev.aria.memory")
@EnableJpaRepositories(basePackages = "dev.aria.memory")  
public class AriaAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AriaAiApplication.class, args);
        System.out.println("""
        ╔══════════════════════════════════════╗
        ║   Rocky is online. Ready, Shanmuga.  ║
        ╚══════════════════════════════════════╝
        """);
    }
}