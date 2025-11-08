import enums.InternshipLevel;
import enums.OpportunityStatus;
import enums.WithdrawalDecision;

import java.util.*;
import java.util.stream.Collectors;

public class CareerCenStaff extends User{

    //fields all inherited from User

    //constructor
    public CareerCenStaff(String userID, String name, String password, String domain) {
        super(userID, name, password, domain);
    }

    //methods
    public CompanyRep findCompanyRep(List<CompanyRep> companyRepList, String companyName) {
        for (CompanyRep rep : companyRepList) {
            if (rep.getCompanyName().equalsIgnoreCase(companyName)) {
                return rep;
            }
        }
        return null; // not found
    }



    public void authorizeCompanyRep(CompanyRep companyRep) {
        companyRep.setIsApproved(true);
        System.out.println(companyRep.getCompanyName() + " has been approved.");
    }

    public void approveIntern(CompanyRep rep, int index ) {
        Internships internship = rep.getInternships().get(index);
        internship.setOpportunityStatus(OpportunityStatus.APPROVED);
        System.out.println(rep.getCompanyName() + "'s " + rep.getInternships().get(index).getTitle() + " opportunity status has been approved");

    }

    public void approveWithdrawalRequest(StudentApplication app, WithdrawalDecision decision) {
        app.setWithdrawDecision(decision);  //same thing as before
        System.out.println(app.getStudent().getName() + "'s withdrawal decision has been " + decision);
    }


    public List<Internships> filteringInternships(List<Internships> internships, int choice) {
        Scanner sc = new Scanner(System.in); //more useful to create a global scanner and feed it, but i write this here for the time being
        // Step 1: Flatten all internships from all companies into a single list
        List<Internships> allInternships = new ArrayList<>();
        for (CompanyRep companyRep : companyRepList) { // again, we need a global list containing companyrep, either from csv or here
            allInternships.addAll(companyRep.getInternships());
        }
        List<Internships> filteredList = new ArrayList<>();
        switch (choice) {
            case 1: // Filter by Status
                System.out.print("Preferred Status (e.g. APPROVED, PENDING): ");
                String preferredStatus = sc.next();
                filteredList = allInternships.stream()
                        .filter(internship -> internship.getOpportunityStatus() ==
                                OpportunityStatus.valueOf(preferredStatus.toUpperCase()))
                        .toList();
                break;

            case 2: // Filter by Preferred Major
                System.out.print("Preferred Major (case-sensitive): ");
                String preferredMajor = sc.next();
                filteredList = allInternships.stream()
                        .filter(internship -> Objects.equals(internship.getPreferredMajor(), preferredMajor))
                        .toList();
                break;

            case 3: // Filter by Internship Level
                System.out.print("Internship Level: ");
                String internshipLevel = sc.next();
                filteredList = allInternships.stream()
                        .filter(internship -> internship.getInternshipLevel() ==
                                InternshipLevel.valueOf(internshipLevel.toUpperCase()))
                        .toList();
                break;

            default:
                System.out.println("Invalid choice!");
                return allInternships;
        }

        // Step 3: Display results, grouped by company
        if (filteredList.isEmpty()) {
            System.out.println("No internships match your criteria.");
        } else {
            System.out.println("\n--- Filtered Internships ---");
            for (Internships internship : filteredList) {
                System.out.println("Company: " + internship.getCompanyName());
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Level: " + internship.getInternshipLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Status: " + internship.getOpportunityStatus());
                System.out.println("-----------------------------------");
            }
        }

        return filteredList;
    }

//    public void generateReport(StudentApplication app) { //no ability to sort yet!!
//        System.out.println("Student application ID: " + app.getApplicationID());
//        System.out.println("Student name: " + app.getStudent().getName());
//        System.out.println("Number of internships applied: " + app.getInternship().size()); //internship is not an array yet! cannot retrieve number of internships
//        if (!app.getInternship().isEmpty()) { //need to initialize Internship object as a array!
//            for (int i = 0; i < app.getInternship().size(); i++) {
//                Internships internship = app.getInternship().get(i);
//                System.out.println((i + 1) + ". " + internship.getTitle() + " - Status: " + internship.getOpportunityStatus());
//            }
//        } else {
//            System.out.println("No internships applied yet.");
//        }
//    }

    @Override
    public boolean logout() {
        return false; //idk how to do this yet
    }
}
