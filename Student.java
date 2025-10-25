

public class Student {
    
    // instance variables
    private String studentId;
    private String name;
    private String major;
    private int studyYear;

    // constructor
    public Student(String studentId, String name, String major, int studyYear) {
        this.studentId = studentId;
        this.name = name;
        this.major = major;
        this.studyYear = studyYear;
    }

    // getters and setters
    public String getStudentId() {
        return studentId;
    }
    public String getName() {
        return name;
    }
    public String getMajor() {
        return major;
    }
    public int getStudyYear() {
        return studyYear;
    }

    // methods 

    // Student can view the list of available internships
    public void viewInternshipList() {

    }


    // Student can apply for an internship
    public void applyForInternship(Internships internship) {
        
    }

    // Student can view the status of their applications
    public void viewApplicationStatus() {
        
    }
    
    // Student can accept an internship placement offer
    public void acceptInternshipPlacement(Internships internship) {
        
    }



    // Student can withdraw their application
    public void requestWithdrawal(Internships internship) {
        
    }

    







}
