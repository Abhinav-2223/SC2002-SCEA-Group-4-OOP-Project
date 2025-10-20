public class CareerCenStaff extends User{
    //fields
    private String staffID;

    //methods

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String ID) {
        staffID = ID;
    }

    public void approveAccountCreationofCompRep(CompanyRep companyRep) {
        companyRep.setIsApproved(true);
    }


    public void approveIntern(CompanyRep rep, int index ) {
        Internship internship = rep.getInternships().get(index);
        internship.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
    }

    public void approveWithdrawalRequest(Student student, WithdrawalDecision decision) {
        student.getStudentApplication().setWithdrawalDecision(decision);  //same thing as before
    }

    public void generateReport(Student student) {
        System.out.println("Student application ID: " + student.getStudentApplication().getApplicationID());
        System.out.println("Student name: " + student.getUserID());
        System.out.println("Number of internships applied: " + student.getStudentApplication().getInternships().size());
        if (!student.getStudentApplication().getInternships().isEmpty()) {
            for (int i = 0; i < student.getStudentApplication().getInternships().size(); i++) {
                Internship internship = student.getStudentApplication().getInternships().get(i);
                System.out.println((i + 1) + ". " + internship.getTitle() + " - Status: " + internship.getApplicationStatus());
            }
        } else {
            System.out.println("No internships applied yet.");
        }
    }
}
