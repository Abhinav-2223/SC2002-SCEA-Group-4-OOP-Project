import database.FilePaths;
import enums.*;

import java.util.Scanner;

public class IPMSystemApp {
    public static void main(String[] args) {

        // init
        Scanner scanner = new Scanner(System.in);
        boolean isLoggedIn = false;

        // Example IDs:
        // Student ID: U1234567X, CompanyRep ID: bob@companyname.com, CareerCen IO: lee123
        do {
            // auth + validation
            System.out.println("### Internship Management Placement System - IPMS ###");
            System.out.println("# Login");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter domain (Student/CompanyRep/Staff): "); // can be changed to int too, currently string for clarity
            String domain = scanner.nextLine().toLowerCase();

            User user = null;

            switch (domain) {
                case "student" -> Student currentUser = new Student();
                case "companyrep" -> // instantaite companyrep;
                case "staff" -> // instantiate careercenstaff;
                default -> {
                    System.out.println("Invalid Domain!"); // likely won't fall to default
                }
            }

            // user.enterGuiMode() or smth (some abstract method)

            // validation (check if id & pw exists in csvs)
            isLoggedIn = User.userLogin(username, password, domain, FilePaths.STUDENTS_CSV, FilePaths.REPS_CSV, FilePaths.STAFF_CSV);

            /*
            TODO:
             login method -> change to use to proper user instance method
             assign User to the appropriate subclass once logged in (based on domain)
             */
        } while (!isLoggedIn);

        while (isLoggedIn) {
            // main loop
        }

        if (isLoggedIn) {
            System.out.println("[DEBUG] Log in success");
        }
    }
}
