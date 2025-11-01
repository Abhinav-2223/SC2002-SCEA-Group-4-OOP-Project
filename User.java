import enums.RepRegistrationStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class User {
    public User() { }

    // fields
    protected String userid;
    protected String password;
    protected String name;
    protected String domain; // Student, CompanyRep, Staff


    // getters & setters
    public String getUserid() { return this.userid; }

    public void setUserid(String userid) { this.userid = userid; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }

    // methods
    public void registerUsers(String userid, String password, String name) {
        // TODO: change reigster to pull directly from csv data (as per pdf reqs)
        System.out.println("Signup successful!");
    }

    public static boolean userLogin(String id, String password, String domain,
                                    String studentsCsv, String repsCsv, String staffCsv) {
        // fields[0] = id, fields[1] = pw for ALL csv, fields[7] = approval for repsCsv ONLY
        String file;
        switch (domain) {
            case "student" -> file = studentsCsv;
            case "companyrep" -> file = repsCsv;
            case "staff" -> file = staffCsv;
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

    public void logout() {
    }

    // TODO: test method
    public void changePassword(String userId, String domain) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter current password: ");
        String currentPw = scanner.nextLine();

        // password validation
        if (currentPw.equals(Helper.csvExtractFields("password",userId,domain))) {
            // password confirmation
            System.out.println("Enter new password: ");
            String newPw = scanner.nextLine();

            System.out.println("Confirm new password: ");
            if (scanner.nextLine().equals(newPw))
                this.password = newPw;
        }
        System.out.println("Password unchanged!");
    }

    public abstract void handleUserInput(String input);
}
