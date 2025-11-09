import enums.WithdrawalDecision;

import java.util.List;
import java.util.Scanner;

public class CCSMenu {
    private CareerCenStaff staff;
    private Scanner sc;

    public CCSMenu(CareerCenStaff staff) {
        this.staff = staff;
        this.sc = new Scanner(System.in);
    }

    public void startDashboard() {
        int choice = -1;
        do {
            System.out.println("\n=== Staff Dashboard ===");
            System.out.println("Welcome, " + staff.getName() + " (" + staff.getUserId() + ")");
            System.out.println("1. Authorize Company Representative");
            System.out.println("2. Approve Internship Opportunity");
            System.out.println("3. Approve Withdrawal Request");
            System.out.println("4. Filter Internships");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("Enter the name of the Company Rep: ");
                    String companyRepName1 = sc.next();
                    CompanyRep companyRep1 = staff.findCompanyRep(companyRepList, companyRepName1); //NEED TO LOAD FROM COMPANY REP CSV TO FIND IT
                    System.out.println("Approve Company Rep? (Y/N)");
                    String choice1 = sc.next();
                    staff.authorizeCompanyRep(companyRep1, choice1.charAt(0));
                case 2:
                    System.out.println("Enter the name of the Company Rep: ");
                    String companyRepName2 = sc.next();
                    CompanyRep companyRep2 = staff.findCompanyRep(companyRepList, companyRepName2);//NEED TO LOAD FROM COMPANY REP CSV TO FIND IT
                    System.out.println("Enter the Internship to approve: ");
                    if (companyRep2 == null) {
                        System.out.println("Company representative not found!");
                        break;
                    }
                    List<Internships> internships = companyRep2.getInternships();
                    if (internships.isEmpty()) {
                        System.out.println("No internships found for " + companyRep2.getCompanyName());
                        break;
                    }
                    System.out.println("Internships offered by " + companyRep2.getCompanyName() + ":");
                    for (int i = 0; i < internships.size(); i++) {
                        System.out.println((i + 1) + ". " + internships.get(i).getTitle() +
                                " (" + internships.get(i).getOpportunityStatus() + ")");
                    }
                    System.out.println("Enter the internship title to approve: ");
                    String internshipName = sc.nextLine();
                    // find the matching internship
                    int index = -1;
                    for (int i = 0; i < internships.size(); i++) {
                        if (internships.get(i).getTitle().equalsIgnoreCase(internshipName)) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        System.out.println("Internship not found.");
                    } else {
                        staff.approveIntern(companyRep2, index);
                    }
                    break;

                case 3:
                    System.out.println("Enter the student name or application ID: ");
                    String studentKey = sc.next();
                    StudentApplication selectedApp = null;
                    for (StudentApplication app : applicationList) { //NEED TO LOAD FROM APPLICATION LIST CSV
                        if (app.getStudent().getName().equalsIgnoreCase(studentKey) || app.getApplicationID().equalsIgnoreCase(studentKey)) {
                            selectedApp = app; //selectedApp of the student
                            break;
                        }
                    }
                    selectedApp.setWithdrawDecision(WithdrawalDecision.APPROVED);
                    break;

                case 4:
                    System.out.println("\n=== Filter Internships ===");
                    System.out.println("1. Filter by Status");
                    System.out.println("2. Filter by Preferred Major");
                    System.out.println("3. Filter by Internship Level");
                    System.out.print("Enter choice: ");

                    int filterChoice = sc.nextInt();
                    sc.nextLine(); // clear newline buffer

                    // Call the updated CareerCenStaff method
                    List<Internships> filteredList = staff.filteringInternships(companyRepList, filterChoice);//NEED TO LOAD FROM COMPANY REP CSV

                    // Display results (CareerCenStaff already prints them, but this is optional confirmation)
                    if (filteredList.isEmpty()) {
                        System.out.println("No internships found matching the selected criteria.");
                    } else {
                        System.out.println("\nTotal results: " + filteredList.size());
                    }
                    break;


                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 0);
    }
}
