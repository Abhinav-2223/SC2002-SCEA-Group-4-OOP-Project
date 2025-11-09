import enums.InternshipLevel;
import enums.OpportunityStatus;
import enums.RepRegistrationStatus;

import java.util.Scanner;

public class CompanyRep extends User {

    // instance variables
    private final String companyName;
    private final String department;
    private final String position;

    private final RepRegistrationStatus regStatus;



    // constructor
    // NOTE: repId === email
    // internships will be created separately into csv, with repId tied to each internship.
    public CompanyRep(String repId, String name, String password, String companyName,
                      String department, String position, RepRegistrationStatus regStatus) {
        super(repId, name, password, "companyrep");
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.regStatus = regStatus;
    }

    // getters and setters
    public String getRepName() { return this.name; }
    public String getCompanyName() { return this.companyName; }
    public RepRegistrationStatus getRegStatus() { return this.regStatus; }


    // class methods
    public static void registerCompany(String companyName) {
        // TODO: create an entry in company_reps_list.csv, let staff approve it (change enum val in csv)
    }

    // instance methods
    @Override
    public void runUserUi(Scanner scanner){
        while() {
            System.out.println("# Company Rep Dashboard - Welcome: " + this.getRepName() + " #");
            System.out.println("1. View created internships");
            System.out.println("2. Create internships");
            System.out.println("3. Approve/Reject internships");
            System.out.println("4. Toggle internship visibility");
            System.out.println("0. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine(); // flush buffer
            switch (choice) {
                case 1: {
                    break;
                }
                case 2: {
                    System.out.println("2");
                    break;
                }
                case 3: {
                    System.out.println("3");
                    break;
                }
                default: {

                }
            }
        }
    }

    // TODO: double check logic for logging out, implement if any
    @Override
    public void logout(){

    }

    // max 5 internships/company, max 10 slots each
    public void createInternships(String title, String description, InternshipLevel internshipLevel,
                                  String preferredMajor, int preferredYear, int openingDate, int closingDate,
                                  OpportunityStatus oppStatus, int slots, boolean visibility){
        switch (regStatus){
            case RepRegistrationStatus.APPROVED -> {
                Internships internships = new Internships();

                // TODO: do up the logic for inserting into csv etc etc
            }
            case RepRegistrationStatus.PENDING -> System.out.println("Unable to create internship. Registration PENDING from Career Staff!");
            case RepRegistrationStatus.REJECTED -> System.out.println("Unable to create internship. Registration REJECTED from Career Staff!");
        }
    }


}
