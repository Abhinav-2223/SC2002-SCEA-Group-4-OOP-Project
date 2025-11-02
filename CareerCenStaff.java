import enums.OpportunityStatus;
import enums.WithdrawalDecision;

public class CareerCenStaff extends User{

    //fields all inherited from User

    //constructor
    public CareerCenStaff(String userID, String name, String password, String domain) {
        super(userID, name, password, domain);
    }

    //methods

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

    public void generateReport(StudentApplication app) { //no ability to sort yet!!
        System.out.println("Student application ID: " + app.getApplicationID());
        System.out.println("Student name: " + app.getStudent().getName());
        System.out.println("Number of internships applied: " + app.getInternship().size()); //internship is not an array yet! cannot retrieve number of internships
        if (!app.getInternship().isEmpty()) { //need to initialize Internship object as a array!
            for (int i = 0; i < app.getInternship().size(); i++) {
                Internships internship = app.getInternship().get(i);
                System.out.println((i + 1) + ". " + internship.getTitle() + " - Status: " + internship.getOpportunityStatus());
            }
        } else {
            System.out.println("No internships applied yet.");
        }
    }


}
