import database.FilePaths;
import enums.RepRegistrationStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Helper {
    // # CSV EXTRACTION METHODS #
    // returns specific field based on id and domain params (to work with ID-based csvs: i.e. user csvs)
    public static String csvExtractFields(String requestedField, String userId, String domain) {
        // selects file based on domain input
        String file = switch (domain.toLowerCase()) {
            case "student" -> FilePaths.STUDENTS_CSV;
            case "companyrep" -> FilePaths.REPS_CSV;
            case "staff" -> FilePaths.STAFF_CSV;
            default -> null;
        };

        // arg sanity checks
        if (file == null) {
            System.out.println("Invalid domain");
            return null;
        }
        if (userId == null || userId.isBlank()) return null;

        // actual file reading
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // reads headers and creates HashMap (dictionary) to store maps of header to index
            String headerRow = reader.readLine();
            if (headerRow == null) return null;
            String[] headers = headerRow.split(",", -1); // creates array of String headers, split by comma, include empty fields
            Map<String, Integer> headerIndexHashMap = new HashMap<>(); // creates HashMap for mapping String -> Integer

            // maps each header to index (e.g. "ID" : 0) key id mapped to value 0 (which can be accessed later)
            for (int i = 0; i < headers.length; i++)
                headerIndexHashMap.put(headers[i].trim().toLowerCase(), i);

            // find the ID column (pick the first that exists)
            Integer idIndex = headerIndexHashMap.get("id"); // gets value associated with key "id"
            if (idIndex == null) {
                System.out.println("No ID column in " + file);
                return null;
            }

            // find the requestedField column
            Integer requestedFieldIndex = headerIndexHashMap.get(requestedField.trim().toLowerCase()); // gets value associated with key requestedField
            if (requestedFieldIndex == null) {
                System.out.println("Field not in header: " + requestedField);
                return null;
            }

            // returns requestedField located at (requestedFieldIndex, idIndex) -> x,y
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] cols = line.split(",", -1);
                if (cols.length <= Math.max(idIndex, requestedFieldIndex))
                    continue; // ensure line has enough columns (within bounds of indices)

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

    // overload csvExtractFields to work with Title-based csvs. i.e.: internships_list.csv
    public static String csvExtractFields(String requestedField, String internshipTitle) {
        // selects internships_list.csv
        String file = FilePaths.INTERNSHIPS_LIST_CSV;

        // arg sanity checks
        if (internshipTitle == null || internshipTitle.isBlank()) return null;

        // actual file reading
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // reads headers and creates HashMap (dictionary) to store maps of header to index
            String headerRow = reader.readLine();
            if (headerRow == null) return null;
            String[] headers = headerRow.split(",", -1); // creates array of String headers, split by comma, include empty fields
            Map<String, Integer> headerIndexHashMap = new HashMap<>(); // creates HashMap for mapping String -> Integer

            // maps each header to index (e.g. "Title" : 0)
            for (int i = 0; i < headers.length; i++)
                headerIndexHashMap.put(headers[i].trim().toLowerCase(), i);

            // find the Title column
            Integer titleIndex = headerIndexHashMap.get("title"); // gets value associated with key "title"
            if (titleIndex == null) {
                System.out.println("No Title column in " + file);
                return null;
            }

            // find the requestedField column
            Integer requestedFieldIndex = headerIndexHashMap.get(requestedField.trim().toLowerCase()); // gets value associated with key requestedField
            if (requestedFieldIndex == null) {
                System.out.println("Field not in header: " + requestedField);
                return null;
            }

            // returns requestedField located at (requestedFieldIndex, titleIndex)
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] cols = line.split(",", -1);
                if (cols.length <= Math.max(titleIndex, requestedFieldIndex))
                    continue; // ensure line has enough columns (within bounds of indices)

                // checks if "title" in csv matches input, if true returns requestedField
                String titleValue = cols[titleIndex].trim();
                if (titleValue.equalsIgnoreCase(internshipTitle)) {
                    return cols[requestedFieldIndex].trim();
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
            return null;
        }
    }

    // queries internships_reps_map to get company rep names for a certain internship
    public static ArrayList<String> getInternshipRepsFor(String internshipTitle) {
        String file = FilePaths.INTERNSHIPS_REPS_MAP_CSV;
        ArrayList<String> reps = new ArrayList<>();

        if (internshipTitle == null || internshipTitle.isBlank())
            return reps;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String headerRow = reader.readLine();
            if (headerRow == null) return reps;

            headerRow = headerRow.replace("\uFEFF", ""); // remove BOM if any
            String[] headers = headerRow.split(",", -1);

            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndex.put(headers[i].trim().toLowerCase(), i);
            }

            Integer titleIndex = headerIndex.get("title");
            Integer repIndex = headerIndex.get("companyrep");

            if (titleIndex == null || repIndex == null) {
                System.out.println("Missing 'Title' or 'CompanyRep' column in CSV.");
                return reps;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] cols = line.split(",", -1);
                if (cols.length <= Math.max(titleIndex, repIndex)) continue;

                String titleValue = cols[titleIndex].trim();
                if (titleValue.equalsIgnoreCase(internshipTitle)) {
                    String repValue = cols[repIndex].trim();
                    if (!repValue.isEmpty()) {
                        reps.add(repValue);
                    }
                }
            }
            return reps;
        } catch (IOException e) {
            System.out.println("Error reading rep mapping CSV: " + e.getMessage());
            return reps;
        }
    }

    public static User loadUserFromCSV(String userId, String domain) {
        switch (domain) {
            case "student" -> {
                String name = csvExtractFields("Name", userId, domain);
                String password = csvExtractFields("Password", userId, domain);
                String major = csvExtractFields("Major", userId, domain);
                int year = Integer.parseInt(Objects.requireNonNull(csvExtractFields("Year", userId, domain)));
                return new Student(userId, name, password, major, year);
            }
            case "companyrep" -> {
                String name = csvExtractFields("Name", userId, domain);
                String password = csvExtractFields("Password", userId, domain);
                String companyName = csvExtractFields("CompanyName", userId, domain);
                String department = csvExtractFields("Department", userId, domain);
                String position = csvExtractFields("Position", userId, domain);
                int internshipsCreated = Integer.parseInt(csvExtractFields("InternshipsCreated", userId, domain));
                RepRegistrationStatus accountApproval = RepRegistrationStatus.valueOf(csvExtractFields("RegStatus", userId, domain));
                return new CompanyRep(userId, name, password, companyName, department, position, internshipsCreated, accountApproval);
            }
            case "staff" -> {
                String name = csvExtractFields("Name", userId, domain);
                String password = csvExtractFields("Password", userId, domain);
                String role = csvExtractFields("Role", userId, domain);
                String dept = csvExtractFields("Department", userId, domain);
                String email = csvExtractFields("Email", userId, domain);
                return new CareerCenStaff(userId, name, password, role, dept, email);
            }
            default -> throw new IllegalArgumentException("Invalid domain: " + domain);
        }
    }
}