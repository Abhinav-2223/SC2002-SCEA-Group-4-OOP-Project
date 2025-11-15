import database.FilePaths;
import enums.RepRegistrationStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public abstract class User {
    protected final String userId;
    protected String password;
    protected final String name;
    protected final String domain; // valid domains: "student", "companyrep", "staff"
    protected Map<String, String> filterPreferences; // in-memory filter preferences for this session

    protected User(String userId, String name, String password, String domain) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.domain = domain;
        this.filterPreferences = new HashMap<>();
    }

    // getters & setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getDomain() { return domain; }

    public void setPassword(String password) { this.password = password; }
    
    public Map<String, String> getFilterPreferences() { return filterPreferences; }
    public void setFilterPreference(String filterType, String filterValue) {
        filterPreferences.put(filterType, filterValue);
    }

    // class methods
    public static boolean userLogin(String id, String password, String domain) {
        // fields[0] = id, fields[1] = pw for ALL csv, fields[7] = approval for repsCsv ONLY
        String file;
        switch (domain) {
            case "student" -> file = FilePaths.STUDENTS_CSV;
            case "companyrep" -> file = FilePaths.REPS_CSV;
            case "staff" -> file = FilePaths.STAFF_CSV;
            default -> {
                System.out.println("Invalid Domain!");
                return false;
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue; // check if csv row empty

                String[] fields = line.split(",", -1); // splits by "," and keep empty columns
                if (fields.length < 2) continue; // invalid row

                String firstRow = fields[0].trim().toLowerCase();
                if (firstRow.equals("id")) continue; // skips header

                if (!domain.equals("companyrep")){ // basic validation
                    if (fields[0].trim().equals(id) && fields[1].trim().equals(password))
                        return true;
                } else { // advanced validation for account approval check
                    if (fields[0].trim().equals(id)
                            && fields[1].trim().equals(password)
                            && RepRegistrationStatus.APPROVED.name().equals(fields[7].trim()))
                        return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return false;
    }

    public abstract void runUserUi(Scanner scanner);
    public abstract List<Internships> filteringInternships(String filterType, String filterValue);

    public void changePassword(String userId, String domain, Scanner scanner) {
        System.out.println("Enter current password: ");
        String currentPw = scanner.nextLine();

        // password validation
        if (currentPw.equals(Helper.csvExtractFields("password",userId,domain))) {
            System.out.println("Enter new password: ");
            String newPw = scanner.nextLine().trim();
            
            if (newPw.isEmpty()) {
                System.out.println("Password cannot be empty. Password unchanged!");
                return;
            }
            
            // Update password in memory and CSV
            this.password = newPw;
            
            // Write to CSV based on domain
            String csvFile = switch (domain) {
                case "student" -> database.FilePaths.STUDENTS_CSV;
                case "companyrep" -> database.FilePaths.REPS_CSV;
                case "staff" -> database.FilePaths.STAFF_CSV;
                default -> null;
            };
            
            if (csvFile != null) {
                CompanyRepHelper.updateUserField(userId, "password", newPw, csvFile);
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Error: Invalid domain. Password unchanged!");
            }
        } else {
            System.out.println("Current password incorrect. Password unchanged!");
        }
    }
}
