package com.trainingcenter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = "$2y$10$j8pUiPjsF18AZreFjuKotOfV0MvD43NOxa4KNOP4kE4EkzYQDLLU2";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean match = encoder.matches(password, hash);
        System.out.println("Checking string: " + password);
        System.out.println("Against hash: " + hash);
        System.out.println("MATCH RESULT: " + match);

        if (!match) {
            System.out.println("The hash provided DOES NOT match 'admin123'.");
        }
    }
}
