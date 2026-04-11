package dev.aria.aria_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "dev.aria")
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