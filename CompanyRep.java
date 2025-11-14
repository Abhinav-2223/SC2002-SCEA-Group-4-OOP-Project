import database.FilePaths;
import enums.InternshipLevel;
import enums.OpportunityStatus;
import enums.RepRegistrationStatus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Scanner;

public class CompanyRep extends User {

    // instance variables
    private final String companyName;
    private final String department;
    private final String position;
    private int internshipsCreated;

    private final RepRegistrationStatus regStatus;

    // constructor
    // NOTE: repId === email
    // internships will be created separately into csv, with repId tied to each internship.
    public CompanyRep(String repId, String name, String password, String companyName,
                      String department, String position, int internshipsCreated, RepRegistrationStatus regStatus) {
        super(repId, name, password, "companyrep");
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.internshipsCreated = internshipsCreated;
        this.regStatus = regStatus;
    }

    // getters and setters
    public String getRepId() { return this.userId; }
    public String getRepName() { return this.name; }
    public String getCompanyName() { return this.companyName; }
    public RepRegistrationStatus getRegStatus() { return this.regStatus; }


    // class methods
    // User inputs fields to create an account with a default password (can change upon login once approved)
    public static void registerCompany(Scanner scanner) {
        // TODO: create an entry in company_reps_list.csv, let staff approve it (change enum val in csv)
        System.out.println("# - Company Representative Registration");
        System.out.print("Enter ID (Company email): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Company's name: ");
        String companyName = scanner.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = scanner.nextLine().trim();
        System.out.print("Enter Job Position: ");
        String position = scanner.nextLine().trim();

        String regStatus = RepRegistrationStatus.PENDING.name();

        // append the new entry to reps csv
        try (FileWriter fw = new FileWriter(FilePaths.REPS_CSV, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Follow the exact CSV header order:
            // appends based on sequence (as in csv file): ID,Password,Name,CompanyName,Department,Position,regStatus
            out.println(String.join(",",
                    id,
                    "password", // default password
                    name,
                    companyName,
                    dept,
                    position,
                    regStatus
            ));

            System.out.println("Registration submitted!");
            System.out.println("Your account is now pending approval by Career Center Staff.");
        } catch (IOException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    // instance methods
    @Override
    public void runUserUi(Scanner scanner){
        boolean session = true;

        while(session) {
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
                    System.out.println("1");
                    break;
                }
                case 2: {
                    if (Integer.parseInt(Objects.requireNonNull(Helper.csvExtractFields("InternshipsCreated", getRepId(), "companyrep"))) < 5)
                        createInternships(scanner);
                    else {
                        System.out.println("Internship limit reached! Cannot create any more internships");
                        break;
                    }
                }
                case 3: {
                    System.out.println("3");
                    break;
                }
                case 4: {

                    break;
                }
                case 0: {
                    session = logout();
                    break;
                }
                default: {
                    System.out.println("Invalid option!");
                    break;
                }
            }
        }
    }

    // TODO: double check logic for logging out, implement if any
    @Override
    public boolean logout(){
        return false;
    }

    // max 5 internships/company, max 10 slots each
    public void createInternships(Scanner scanner){
//        String title, String description, InternshipLevel internshipLevel,
//        String preferredMajor, int preferredYear, int openingDate, int closingDate,
//        OpportunityStatus oppStatus, int slots, boolean visibility,
        boolean inOperation = true;
        switch (regStatus){
            case RepRegistrationStatus.APPROVED -> {
                System.out.println("# - Create Internship Opportunity");
                System.out.print("Enter internship title: ");
                String title = scanner.nextLine().trim();
                System.out.print("Enter description: ");
                String desc = scanner.nextLine().trim();
                System.out.print("Enter internship level (Basic/Intermediate/Advanced): ");
                String internshipLevelInput = scanner.nextLine().trim();
                InternshipLevel internshipLevel = InternshipLevel.valueOf(internshipLevelInput);

                System.out.print("Enter major preference: ");
                String preferredMajor = scanner.nextLine().trim();
                System.out.print("Enter Year of Study preference: ");
                String preferredYear = scanner.nextLine().trim();
                System.out.print("Enter internship opening date (YYYY-MM-DD): ");
                String openingDate = scanner.nextLine().trim();
                System.out.print("Enter internship closing date (YYYY-MM-DD): ");
                String closingDate = scanner.nextLine().trim();

                String regStatus = OpportunityStatus.PENDING.name();

                System.out.println("Enter internship slots (up to 10 maximum)");
                int slots = scanner.nextInt();
                scanner.nextLine(); // clear buffer after nextInt()

                boolean visibility = true;

                // append the new entry to reps csv
                try (FileWriter fw = new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV, true);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {

                    // Follow the exact CSV header order:
                    // appends based on sequence (as in csv file): ID,Password,Name,CompanyName,Department,Position,regStatus
                    out.println(String.join(",",
                            title,
                            desc,
                            internshipLevel.name(),
                            preferredMajor,
                            preferredYear,
                            openingDate,
                            closingDate,
                            regStatus, // initially PENDING
                            getCompanyName(),
                            String.valueOf(slots),
                            String.valueOf(visibility)
                    ));

                    internshipsCreated++;
                    CompanyRepHelper.updateRepField(getRepId(), "InternshipsCreated", Integer.toString(internshipsCreated));
                    System.out.println("Internship created! Pending approval by Career Center Staff.");
                } catch (IOException e) {
                    System.out.println("Creation error: " + e.getMessage());
                }
            }
            case RepRegistrationStatus.PENDING -> System.out.println("Unable to create internship. Registration PENDING from Career Staff!");
            case RepRegistrationStatus.REJECTED -> System.out.println("Unable to create internship. Registration REJECTED from Career Staff!");
        }
    }

    // flips value of Visibility in csv
    public void toggleVisibility() {

    }
}
