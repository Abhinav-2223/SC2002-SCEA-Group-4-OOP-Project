import database.FilePaths;
import enums.RepRegistrationStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public abstract class User {
    protected final String userId;
    protected String password;
    protected final String name;
    protected final String domain; // "student", "companyrep", "staff" (consider an enum)

    protected User(String userId, String name, String password, String domain) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.domain = domain;
    }

    // getters & setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getDomain() { return domain; }

    public void setPassword(String password) { this.password = password; }

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

    public abstract boolean logout();

    // TODO: test method
    public void changePassword(String userId, String domain) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter current password: ");
        String currentPw = scanner.nextLine();

        // password validation
        if (currentPw.equals(Helper.csvExtractFields("password",userId,domain))) {
            // password confirmation
            System.out.println("Enter new password: ");
            String newPw = scanner.nextLine().trim();

            System.out.println("Confirm new password: ");
            if (scanner.nextLine().trim().equals(newPw))
                this.password = newPw;
        }
        System.out.println("Password unchanged!");
    }
}
