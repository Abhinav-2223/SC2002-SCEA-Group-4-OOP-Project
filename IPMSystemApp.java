import java.util.Scanner;

public class IPMSystemApp {
    public static void main(String[] args){
        // init
        Scanner scanner = new Scanner(System.in);
        User user;
        boolean isLoggedIn = false;

        // Example IDs:
        // Student ID: U1234567X, CompanyRep ID: bob@companyname.com, CareerCen IO: lee123
        // Main login menu of the app
        do {
            System.out.println("### Internship Management Placement System - IPMS ###");
            System.out.println("1. Login");
            System.out.println("2. Company Representative Registration");
            System.out.println("0. Exit Application");
            System.out.println("Enter option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // flush buffer
            switch (choice){
                case 1:{
                    break; // breaks out of switch to continue normal flow
                }
                case 2:{
                    CompanyRep.registerCompany(scanner);
                    continue;
                }
                case 0:{
                    System.out.println("Exiting application. Goodbye!");
                    scanner.close();
                    System.exit(0);
                }
                default:{
                    System.out.println("Invalid Option");
                    continue;
                }
            }
            System.out.println("# - Login");
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter domain (Student/CompanyRep/Staff): ");
            String domain = scanner.nextLine().toLowerCase().trim();

            // auth + validation
            if (!User.userLogin(username, password, domain)) {
                System.out.println("Login failed! Username or password is incorrect.\n");
                continue; // ignore rest of iteration and reprompts
            }

            user = Helper.loadUserFromCSV(username, domain);
            // ensure user !null so ide doesn't complain
            if (user == null) {
                System.out.println("Internal error: user record not found. Please try again.\n");
                continue;
            }

            isLoggedIn = true;

            // branches to individual subclass (routes program interface to implemented stuff within each subclass)
            switch (user.getDomain()) {
                case "student":{
                    user.runUserUi(scanner);
                    isLoggedIn = false;
                    break;
                }
                case "companyrep":{
                    user.runUserUi(scanner);
                    isLoggedIn = false;
                    break;
                }
                case "staff":{
                    user.runUserUi(scanner);
                    isLoggedIn = false;
                    break;
                }
                default:{
                    System.out.println("Unknown domain: " + user.getDomain());
                    isLoggedIn = false; // force retry
                    break;
                }
            }
        } while (!isLoggedIn);
    }
}
