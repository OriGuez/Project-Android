package com.example.project_android;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RandomIDGenerator {

    public static String generateRandomID() {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();

        // Format date and time as string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String formattedDateTime = now.format(formatter);

        // Generate a random UUID (Universally Unique Identifier)
        String uuid = UUID.randomUUID().toString();

        // Concatenate date/time string and UUID

        // Return the random ID
        return formattedDateTime + uuid;
    }

    public static void main(String[] args) {
        // Generate and print a random ID
        String randomID = generateRandomID();
        System.out.println("Random ID: " + randomID);
    }
}