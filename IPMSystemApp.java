import enums.*;

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
            isLoggedIn = Helper.csvAuth(username,password,domain,STUDENTS_CSV,REPS_CSV,STAFF_CSV);

            /*
            TODO:
             login method -> change to use to proper user instance method
             assign User to the appropriate subclass once logged in (based on id)
             */
        } while (!isLoggedIn);

        if(isLoggedIn){
            System.out.println("Logged in [DEBUG]");
        }

    }
}
