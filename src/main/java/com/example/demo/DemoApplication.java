package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx, PasswordEncoder passwordEncoder) {
        return args -> {
            if (args.length > 0 && args[0].equals("--passwordGenerator")) {
                String plainPassword = "admin";
                String encodedPassword = passwordEncoder.encode(plainPassword);
                System.out.println("\nPassword Generator:");
                System.out.println("Plain password: " + plainPassword);
                System.out.println("Encoded password: " + encodedPassword);
                
                // Verify the encoding worked correctly
                boolean matches = passwordEncoder.matches(plainPassword, encodedPassword);
                System.out.println("Password verification test: " + matches + "\n");
                
                // Now test against the stored password from data.sql
                String storedHash = "$2a$10$vpb7gxXK9uTecYnUuKoibOT36rrO/FWPihsvRLYziQahD0qgL3v8.";
                boolean matchesStored = passwordEncoder.matches(plainPassword, storedHash);
                System.out.println("Testing against stored password:");
                System.out.println("Stored hash from data.sql: " + storedHash);
                System.out.println("Matches stored password: " + matchesStored + "\n");
            }
        };
    }
}
