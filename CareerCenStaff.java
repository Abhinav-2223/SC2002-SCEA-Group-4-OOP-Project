import enums.OpportunityStatus;
import enums.WithdrawalDecision;

public class CareerCenStaff extends User{
    //fields
    private String staffID;

    //getters and setters

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String ID) {
        staffID = ID;
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

    public void approveWithdrawalRequest(Student student, WithdrawalDecision decision) {
        student.getStudentApplication().setWithdrawalDecision(decision);  //same thing as before
        System.out.println(student.getName() + "'s withdrawal decision has been " + decision);
    }

    public void generateReport(Student student) { //no ability to sort yet!!
        System.out.println("Student application ID: " + student.getStudentApplication().getApplicationID());
        System.out.println("Student name: " + student.getStudentId());
        System.out.println("Number of internships applied: " + student.getStudentApplication().getInternships().size());
        if (!student.getStudentApplication().getInternships().isEmpty()) {
            for (int i = 0; i < student.getStudentApplication().getInternships().size(); i++) {
                Internships internship = student.getStudentApplication().getInternships().get(i);
                System.out.println((i + 1) + ". " + internship.getTitle() + " - Status: " + internship.getOpportunityStatus());
            }
        } else {
            System.out.println("No internships applied yet.");
        }
    }


}
