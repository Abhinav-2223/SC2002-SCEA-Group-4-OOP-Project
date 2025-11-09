import database.FilePaths;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Helper {
    // returns specific field based on id and domain params
    public static String csvExtractFields(String requestedField, String userId, String domain) {
        // selects file based on domain input
        String file = switch (domain.toLowerCase()) {
            case "student"    -> FilePaths.STUDENTS_CSV;
            case "companyrep" -> FilePaths.REPS_CSV;
            case "staff"      -> FilePaths.STAFF_CSV;
            default -> null;
        };

        // arg sanity checks
        if (file == null) { System.out.println("Invalid domain"); return null; }
        if (userId == null || userId.isBlank()) return null;

        // actual file reading
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // reads headers and creates HashMap (dictionary) to store maps of header to index
            String headerRow = reader.readLine();
            if (headerRow == null) return null;
            String[] headers = headerRow.split(",",-1); // creates array of String headers, split by comma, include empty fields
            Map<String,Integer> headerIndexHashMap = new HashMap<>(); // creates HashMap for mapping String -> Integer

            // maps each header to index (e.g. "ID" : 0) key id mapped to value 0 (which can be accessed later)
            for (int i = 0; i < headers.length; i++)
                headerIndexHashMap.put(headers[i].trim().toLowerCase(), i);

            // find the ID column (pick the first that exists)
            Integer idIndex = headerIndexHashMap.get("id"); // gets value associated with key "id"
            if (idIndex == null) { System.out.println("No ID column in " + file); return null; }

            // find the requestedField column
            Integer requestedFieldIndex = headerIndexHashMap.get(requestedField.trim().toLowerCase()); // gets value associated with key requestedField
            if (requestedFieldIndex == null) { System.out.println("Field not in header: " + requestedField); return null; }

            // returns requestedField located at (requestedFieldIndex, idIndex) -> x,y
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] cols = line.split(",",-1);
                if (cols.length <= Math.max(idIndex, requestedFieldIndex)) continue; // ensure line has enough columns (within bounds of indices)

                // checks if idValue in csv matches input, if so returns requestedField
                String idValue = cols[idIndex].trim();
                if (idValue.equalsIgnoreCase(userId)) {
                    return cols[requestedFieldIndex].trim();
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
            return null;
        }
    }

    // TODO: implement overloaded method to work for Title-based csvs
    // overloaded csvExtractFields (to work with Title-based csvs for Internships)
    public static String csvExtractFields(String requestedField, String internshipTitle) {
        String file = FilePaths.INTERNSHIPS_CSV;  // path to your internship CSV
        if (internshipTitle == null || internshipTitle.isBlank()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerRow = reader.readLine();
            if (headerRow == null) return null;

            headerRow = headerRow.replace("\uFEFF", ""); // remove BOM if any
            String[] headers = headerRow.split(",", -1);

            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndex.put(headers[i].trim().toLowerCase(), i);
            }

            Integer titleIndex = headerIndex.get("title");
            Integer requestedFieldIndex = headerIndex.get(requestedField.trim().toLowerCase());

            if (titleIndex == null || requestedFieldIndex == null) {
                System.out.println("Missing 'Title' or requested field in Internship CSV.");
                return null;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] cols = line.split(",", -1);
                if (cols.length <= Math.max(titleIndex, requestedFieldIndex)) continue;

                String titleValue = cols[titleIndex].trim();
                if (titleValue.equalsIgnoreCase(internshipTitle)) {
                    return cols[requestedFieldIndex].trim();
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("Error reading internship CSV: " + e.getMessage());
            return null;
        }
    }




    public static User loadUserFromCSV(String userId, String domain) {
        switch (domain) {
            case "student" -> {
                String name = csvExtractFields("Name", userId, domain);
                String major = csvExtractFields("Major", userId, domain);
                String password = csvExtractFields("Password", userId, domain);
                int year = Integer.parseInt(Objects.requireNonNull(csvExtractFields("Year", userId, domain)));
                return new Student(userId, name, password, major, year);
            }
            case "companyrep" -> { // TODO: implement when CompanyRep class done properly
//                String name = csvExtractFields("Name", userId, domain);
//                String company = csvExtractFields("CompanyName", userId, domain);
//                String department = csvExtractFields("Department", userId, domain);
//                String position = csvExtractFields("Position", userId, domain);
//                String approval = csvExtractFields("RegStatus", userId, domain);
//                return new CompanyRep();
            }
            case "staff" -> {
//                String name = csvExtractFields("Name", userId, domain);
//                String dept = csvExtractFields("Department", userId, domain);
//                return new Staff();
            }
            default -> throw new IllegalArgumentException("Invalid domain: " + domain);
        }
        return null;
    }
}