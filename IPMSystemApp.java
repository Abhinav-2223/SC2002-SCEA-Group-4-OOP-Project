import database.FilePaths;
import enums.*;

import java.io.File;
import java.util.Scanner;

public class IPMSystemApp {
    public static void main(String[] args){


        // init
        Scanner scanner = new Scanner(System.in);
        User loggedInUser = null;
        boolean isLoggedIn = false;

        // auth + validation
        System.out.println("### Internship Management Placement System - IPMS ###");
        // Example IDs:
        // Student ID: U1234567X, CompanyRep ID: bob@companyname.com, CareerCen IO: lee123
        do {
            System.out.println("# Login");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter domain (Student/CompanyRep/Staff): "); // can be changed to int too, currently string for clarity
            String domain = scanner.nextLine().toLowerCase();

            // validation (check if id & pw exists in csvs)
            isLoggedIn = User.userLogin(username,password,domain,FilePaths.STUDENTS_CSV,FilePaths.REPS_CSV,FilePaths.STAFF_CSV);

            /*
            TODO:
             login method -> change to use to proper user instance method
             assign User to the appropriate subclass once logged in (based on domain)
             */
        } while (!isLoggedIn);

        if(isLoggedIn){
            System.out.println("[DEBUG] Log in success");
        }
    }
}
