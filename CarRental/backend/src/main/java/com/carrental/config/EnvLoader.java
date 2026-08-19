package com.carrental.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class EnvLoader {

    public static void loadEnv() {
        File[] searchLocations = new File[]{
                new File(".env"),
                new File("../.env"),
                new File("backend/.env"),
                new File(System.getProperty("user.dir"), ".env"),
                new File(System.getProperty("user.dir"), "../.env")
        };

        for (File envFile : searchLocations) {
            if (envFile.exists() && envFile.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String value = line.substring(eqIdx + 1).trim();
                            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                                value = value.substring(1, value.length() - 1);
                            } else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                                value = value.substring(1, value.length() - 1);
                            }
                            if (System.getProperty(key) == null && System.getenv(key) == null) {
                                System.setProperty(key, value);
                            }
                        }
                    }
                    System.out.println("Loaded environment variables from: " + envFile.getAbsolutePath());
                    return;
                } catch (Exception e) {
                    System.err.println("Failed to read .env file at " + envFile.getAbsolutePath() + ": " + e.getMessage());
                }
            }
        }
    }
}
