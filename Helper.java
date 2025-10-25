import enums.*;
import database.FilePaths;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Helper {
    public static String csvExtractFields(String requestedField, String userId, String domain) {
        // fields[0] = id, fields[1] = pw for ALL csv
        String file = null;
        switch (domain) {
            case "student" -> file = FilePaths.STUDENTS_CSV;
            case "companyrep" -> file = FilePaths.REPS_CSV;
            case "staff" -> file = FilePaths.STAFF_CSV;
            default -> {
                System.out.println("Invalid Domain!"); // likely won't fall to default
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue; // skips empty row

                String[] fields = line.split(",", -1); // splits by "," and keep empty columns
                if (fields.length < 2) continue; // skips invalid row

                String firstRow = fields[0].trim().toLowerCase();
                if (firstRow.equals("id")) continue; // skips header

                // TODO: fix logic to get correct password based on ID and DOMAIN
                switch (requestedField) {
                    case "id" -> { return fields[0].trim(); }
                    case "password" -> { return fields[1].trim(); }
                    default -> {
                        System.out.println("Invalid field type."); // likely won't fall to default
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return null;
    }
}