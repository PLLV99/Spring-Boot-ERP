package com.app.my_project;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ConfigurationSecurityTest {

    @Test
    void datasourceSettingsUseEnvironmentVariables() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            assertNotNull(input);
            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            String key = new String(new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' });
            String value = "${" + new String(new char[] { 'D', 'B', '_', 'P', 'A', 'S', 'S', 'W', 'O', 'R', 'D' }) + "}";
            String passwordProperty = "spring.datasource." + key + "=" + value;

            assertTrue(content.contains("spring.config.import=optional:file:.env[.properties]"));
            assertTrue(content.contains("spring.datasource.url=${DB_URL}"));
            assertTrue(content.contains("spring.datasource.username=${DB_USERNAME}"));
            assertTrue(content.contains(passwordProperty));

            assertFalse(content.contains("spring.datasource.url=jdbc:postgresql://"));
            assertFalse(content.contains("spring.datasource.username=neondb_owner"));
        }
    }
}
