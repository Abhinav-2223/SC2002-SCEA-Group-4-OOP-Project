import java.util.Scanner;

public class StudentMenu {
    private Student student;
    private Scanner scanner;

    public StudentMenu(Student student) {
        this.student = student;
        this.scanner = new Scanner(System.in);
    }

    public void startDashboard() {
        int choice = -1;
        do {
            System.out.println("\n=== Student Dashboard ===");
            System.out.println("Welcome, " + student.getName() + " (" + student.getUserId() + ")");
            System.out.println("1. View Internship List");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View Application Status");
            System.out.println("4. Accept Internship Placement");
            System.out.println("5. Request Withdrawal");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> student.viewInternshipList();
                case 2 -> applyInternship();
                case 3 -> student.viewApplicationStatus();
                case 4 -> acceptInternship();
                case 5 -> withdrawApplication();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 0);
    }

    // === helper methods for user input ===
    private void applyInternship() {
        System.out.println("\n--- Apply for Internship ---");
        System.out.print("Enter internship title: ");
        String title = scanner.nextLine();

        Internships targetInternship = CSVUtils.findInternshipByTitle(
            Internships.getAllVisibleInternships(), title
        );
        
        if (targetInternship == null) {
            System.out.println("Internship not found or not available.");
            return;
        }
        
        student.applyForInternship(targetInternship);
    }

    private void acceptInternship() {
        System.out.println("\n--- Accept Internship ---");
        System.out.print("Enter internship title to accept: ");
        String title = scanner.nextLine();

        Internships targetInternship = CSVUtils.findInternshipByTitle(
            Internships.getAllVisibleInternships(), title
        );
        
        if (targetInternship == null) {
            System.out.println("Internship not found or not available.");
            return;
        }
        
        student.acceptInternshipPlacement(targetInternship);
    }

    private void withdrawApplication() {
        System.out.println("\n--- Request Withdrawal ---");
        System.out.print("Enter internship title to withdraw from: ");
        String title = scanner.nextLine();

        Internships targetInternship = CSVUtils.findInternshipByTitle(
            Internships.getAllVisibleInternships(), title
        );
        
        if (targetInternship == null) {
            System.out.println("Internship not found or not available.");
            return;
        }
        
        student.requestWithdrawal(targetInternship);
    }
}
