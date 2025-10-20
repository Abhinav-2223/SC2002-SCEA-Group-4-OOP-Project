
public class CompanyRep {

<<<<<<< Updated upstream
=======
    // instance variables
    private String companyName;
    private boolean isApproved;


    // methods
    public String getCompanyName() {
        return companyName;
    }

    public void registerCompany(String companyName) {
        if (isApproved) {
            System.out.println("Company is already registered and approved.");
        } else {
            this.companyName = companyName;
            this.isApproved = false; // registration pending approval
            System.out.println("Company registration submitted for approval.");
        }
    }

    public void createinternships(){

    }

    public void approveRegistration(){

    }

    public void toggleInternshipVisibility(){

    }

    

>>>>>>> Stashed changes
}
