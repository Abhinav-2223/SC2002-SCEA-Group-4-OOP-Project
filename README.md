# SC2002-SCEA-Group-4-OOP-Project


Core classes:


## User (abstract)

Methods:
    - public login(String userId, String password)
    - public logout()
    - public changePassword(String newPassword)
Fields:
    - private String password
    - private String userId
    - private String name


## Student (inherits User)

Methods:
    - public viewInternshipList()
    - public applyInternships(String company1, String company2, String company3) (from the csv)
    - public viewApplicationStatus()
    - public acceptInternshipPlacement()
    - public requestWithdrawal()
Fields:
    - private String studentId
    - private String studyYear
    - private String major
    - private InternshipLevel internshipLevel


## CompanyRep (inherits User)
Methods:
    - public createInternships()
    - public registerCompany() -> (goes to career cen staff to approve)
    - public approveRegistration()
    - public toggleInternshipVisibility(Internship x)
Fields:
    - private string companyName	
    - private boolean isApproved


## CareerCenStaff (inherits User)
Methods:
    - public boolean approveIntern()
    - public boolean approveWithdrawalRequest(StudentApplication)
    - public generateReport()
    - public authorizeCompanyRep(companyName)
Fields:
    - private String staffID



# ------------- Other Classes: -------------

## Internship (associated with CompanyRep)

Methods:
    - 

Fields:
    - private String title
    - private String description
    - private InternshipLevel internLevel (InternshipLevel is an enum)
    - private String preferredMajor major
    - private int openingDate (can use ddmmyy)
    - private int closingDate
    - private ApplicationStatus appStatus (ApplicationStatus is an enum)
    - private String companyName
    - private String[] companyReps
    - private int slots

## StudentApplication

Fields:
    - private String applicationId
    - private Student student
    - private Internship internship
    - private ApplicationStatus appStatus (enum)
    - private WithdrawalDecision withdrawDecision (enum)
Methods: 



 

ENUMS: (for the different returns values)
InternshipLevel { BASIC, INTERMEDIATE, ADVANCED }
OpportunityStatus { PENDING, APPROVED, REJECTED, FILLED }
ApplicationStatus { PENDING, SUCCESSFUL, UNSUCCESSFUL }
WithdrawalDecision { NONE, PENDING, APPROVED, REJECTED }










