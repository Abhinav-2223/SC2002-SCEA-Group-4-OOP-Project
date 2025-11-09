import enums.InternshipLevel;
import enums.OpportunityStatus;
import enums.WithdrawalDecision;

import java.util.*;

public class CareerCenStaff extends User{

    //fields all inherited from User

    //constructor
    public CareerCenStaff(String userID, String name, String password, String domain) {
        super(userID, name, password, domain);
    }

    //methods
    // this adds the companyRep INTO csv (company_reps_list.csv)
    public void authorizeCompanyRep(CompanyRep companyRep) {
        companyRep.setIsApproved(true);
        System.out.println(companyRep.getCompanyName() + " has been approved.");
    }

    public void approveIntern(CompanyRep rep, int index ) {
        Internships internships = rep.getInternships().get(index);
        internships.setOpportunityStatus(OpportunityStatus.APPROVED);
        System.out.println(rep.getCompanyName() + "'s " + rep.getInternships().get(index).getTitle() + " opportunity status has been approved");

    }

    public void approveWithdrawalRequest(StudentApplication app, WithdrawalDecision decision) {
        app.setWithdrawDecision(decision);  //same thing as before
        System.out.println(app.getStudent().getName() + "'s withdrawal decision has been " + decision);
    }


    public List<Internships> filteringInternships(List<Internships> internships, int choice) {
        Scanner sc = new Scanner(System.in); //more useful to create a global scanner and feed it, but i write this here for the time being
        switch (choice) {
            case 1: //filter by Status
                System.out.println("Preferred Status: ");
                String preferredStatus = sc.next();
                List<Internships> availableInternships;
                availableInternships = internships.stream().filter(internships1 -> internships1.getOpportunityStatus()
                                                            == OpportunityStatus.valueOf(preferredStatus.toUpperCase())).toList();
                return availableInternships;

            case 2: //filter by preferred majors
                System.out.println("(Case sensitive!) Preferred Major: ");
                String preferredMajor = sc.next();
                List<Internships> sortByMajor;
                sortByMajor = internships.stream().filter(internships1 -> Objects.equals(internships1.getPreferredMajor(), preferredMajor)).toList();
                return sortByMajor;

            case 3: //filter by Internship level
                System.out.println("Internship Level: ");
                String internshipLevel = sc.next();
                List<Internships> internshipsLevelList;
                internshipsLevelList = internships.stream().filter(internships1 -> internships1.getInternshipLevel()
                        == InternshipLevel.valueOf(internshipLevel.toUpperCase())).toList();
                return internshipsLevelList;


            default:
                System.out.println("Invalid Choice!");
                return internships;
        }
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
