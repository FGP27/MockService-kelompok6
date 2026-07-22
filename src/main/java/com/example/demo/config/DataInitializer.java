package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("Admin@gmail.com")) {
            User admin = new User("Admin@gmail.com", passwordEncoder.encode("admin123"), "Admin");
            admin.setRole("ADMIN");
            userRepository.save(admin);
            log.info("User Admin berhasil dibuat: Admin@gmail.com");
        } else {
            log.info("User Admin sudah ada");
        }

        if (!userRepository.existsByEmail("user@gmail.com")) {
            User user = new User("user@gmail.com", passwordEncoder.encode("user123"), "User");
            userRepository.save(user);
            log.info("User berhasil dibuat: user@gmail.com");
        } else {
            log.info("User user@gmail.com sudah ada");
        }
    }
}
