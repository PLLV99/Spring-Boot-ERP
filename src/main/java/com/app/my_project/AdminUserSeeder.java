package com.app.my_project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.my_project.entity.UserEntity;
import com.app.my_project.repository.UserRepository;

// Seeds the first admin account on an empty database so a fresh install can log in.
// Runs once at startup; does nothing if any user already exists.
@Component
public class AdminUserSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);

    private final UserRepository userRepository;

    public AdminUserSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        String password = System.getenv("ADMIN_PASSWORD");
        if (password == null || password.isBlank()) {
            password = "admin1234";
            log.warn("No ADMIN_PASSWORD env var set - seeding default admin with password 'admin1234'. "
                    + "Change it after first login!");
        }

        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPassword(new BCryptPasswordEncoder().encode(password));
        admin.setRole("admin");
        userRepository.save(admin);
        log.info("Seeded initial admin user 'admin'");
    }
}
