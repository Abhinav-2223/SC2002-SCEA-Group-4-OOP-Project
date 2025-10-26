import enums.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class IPMSystemApp {
    public static void main(String[] args){
        // init
        Scanner scanner = new Scanner(System.in);

        // *NOTE*: update this if csv name changed
        final String STUDENTS_CSV = "./database/student_list.csv";
        final String REPS_CSV     = "./database/company_reps_list.csv";
        final String STAFF_CSV    = "./database/staff_list.csv";

        User loggedInUser = null;
        boolean isLoggedIn = false;
        String username = "";
        String password = "";
        String domain = "";

        // auth + validation
        System.out.println("### Internship Management Placement System - IPMS ###");
        // Example IDs:
        // Student ID: U1234567X, CompanyRep ID: bob@companyname.com, CareerCen IO: lee123
        do {
            System.out.println("# Login");
            System.out.print("Enter username: ");
            username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            password = scanner.nextLine().trim();
            System.out.print("Enter domain (Student/CompanyRep/Staff): "); // can be changed to int too, currently string for clarity
            domain = scanner.nextLine().toLowerCase().trim();

            // validation (check if id & pw exists in csvs)
            isLoggedIn = Helper.csvAuth(username, password, domain, STUDENTS_CSV, REPS_CSV, STAFF_CSV);

            /*
            TODO:
             login method -> change to use to proper user instance method
             assign User to the appropriate subclass once logged in (based on id)
             */
        } while (!isLoggedIn);

        if (isLoggedIn) {
            System.out.println("Logged in [DEBUG]");


            if (domain.equals("student")) {
                Student loggedStudent = loadStudentFromCSV(username, STUDENTS_CSV);
                if (loggedStudent != null) {
                    System.out.println("Welcome, " + loggedStudent.getName() + "!");
                    StudentMenu studentMenu = new StudentMenu(loggedStudent);
                    studentMenu.startDashboard();
                } else {
                    System.out.println("Student not found in CSV file.");
                }
            }

        }
    }

    // helper function to load student info from CSV
    private static Student loadStudentFromCSV(String studentId, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] fields = line.split(",", -1);
                if (fields[0].equalsIgnoreCase("StudentID")) continue; // skip header

                if (fields[0].trim().equalsIgnoreCase(studentId)) {
                    String id = fields[0].trim();
                    String name = fields[1].trim();
                    String major = fields[2].trim();
                    int year = Integer.parseInt(fields[3].trim());
                    return new Student(id, name, major, year);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading student CSV: " + e.getMessage());
        }
        return null;
    }
}
