import database.FilePaths;
import enums.InternshipLevel;
import enums.OpportunityStatus;
import enums.RepRegistrationStatus;
import enums.WithdrawalDecision;

import java.io.*;
import java.util.*;

public class CareerCenStaff extends User{
    @SuppressWarnings("unused")
    private final String role;
    @SuppressWarnings("unused")
    private final String department;
    @SuppressWarnings("unused")
    private final String email;

    //constructor
    public CareerCenStaff(String userID, String name, String password, String role, String department, String email) {
        super(userID, name, password, "staff");
        this.role = role;
        this.department = department;
        this.email = email;
    }

    @Override
    public void runUserUi(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Career Centre Staff Dashboard ===");
            System.out.println("1. Authorize Company Representative");
            System.out.println("2. Approve Internship Opportunity");
            System.out.println("3. Reject Internship Opportunity");
            System.out.println("4. Approve Withdrawal Request");
            System.out.println("5. Generate Report");
            System.out.println("6. Filter Internships");
            System.out.println("7. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    // QoL: Show list of pending reps
                    showPendingCompanyReps();
                    System.out.print("Enter Company Rep ID to authorize: ");
                    String repId = scanner.nextLine().trim();
                    authorizeCompanyRep(repId);
                    break;
                case "2":
                    // QoL: Show list of pending internships
                    showPendingInternships();
                    System.out.print("Enter Internship Title to approve: ");
                    String internTitle = scanner.nextLine().trim();
                    approveInternship(internTitle);
                    break;
                case "3":
                    // TC15 Fix: Add rejection functionality
                    showPendingInternships();
                    System.out.print("Enter Internship Title to reject: ");
                    String rejectTitle = scanner.nextLine().trim();
                    rejectInternship(rejectTitle);
                    break;
                case "4":
                    approveWithdrawalRequestMenu(scanner);
                    break;
                case "5":
                    generateReport();
                    break;
                case "6":
                    filterInternshipsMenu(scanner);
                    break;
                case "7":
                    changePassword(this.getUserId(), "staff", scanner);
                    break;
                case "0":
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // authorize company rep by ID
    private void authorizeCompanyRep(String repId) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.REPS_CSV))) {
            String line;
            String[] header = null;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                lines.add(line); // keep header
            }
            
            if (header == null) {
                System.out.println("Error: Invalid CSV format");
                return;
            }
            
            // find RegStatus column
            int regStatusCol = -1;
            for (int i = 0; i < header.length; i++) {
                if (header[i].trim().equalsIgnoreCase("RegStatus")) {
                    regStatusCol = i;
                    break;
                }
            }
            
            // read and update rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] row = line.split(",", -1);
                
                if (row.length > 0 && row[0].trim().equals(repId)) {
                    found = true;
                    // update registration status to APPROVED
                    if (regStatusCol >= 0 && row.length > regStatusCol) {
                        row[regStatusCol] = RepRegistrationStatus.APPROVED.name();
                    }
                    lines.add(String.join(",", row));
                    System.out.println("Company Rep " + repId + " has been approved.");
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading company reps CSV: " + e.getMessage());
            return;
        }
        
        if (!found) {
            System.out.println("Company Rep with ID " + repId + " not found.");
            return;
        }
        
        // write back to CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FilePaths.REPS_CSV))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to company reps CSV: " + e.getMessage());
        }
    }

    // approve internship by title
    private void approveInternship(String internshipTitle) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            String[] header = null;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                lines.add(line); // keep header
            }
            
            if (header == null) {
                System.out.println("Error: Invalid CSV format");
                return;
            }
            
            // find Title and OpportunityStatus columns
            int titleCol = -1, statusCol = -1;
            for (int i = 0; i < header.length; i++) {
                String h = header[i].trim();
                if (h.equalsIgnoreCase("Title")) titleCol = i;
                else if (h.equalsIgnoreCase("OpportunityStatus")) statusCol = i;
            }
            
            // read and update rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] row = line.split(",", -1);
                
                String title = titleCol >= 0 && row.length > titleCol ? row[titleCol].trim() : "";
                
                if (title.equalsIgnoreCase(internshipTitle)) {
                    found = true;
                    // update opportunity status to VACANT (approved)
                    if (statusCol >= 0 && row.length > statusCol) {
                        row[statusCol] = OpportunityStatus.VACANT.name();
                    }
                    lines.add(String.join(",", row));
                    System.out.println("Internship '" + internshipTitle + "' has been approved.");
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading internships CSV: " + e.getMessage());
            return;
        }
        
        if (!found) {
            System.out.println("Internship with title '" + internshipTitle + "' not found.");
            return;
        }
        
        // write back to CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to internships CSV: " + e.getMessage());
        }
    }

    // TC15 Fix: reject internship by title
    private void rejectInternship(String internshipTitle) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            String[] header = null;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                lines.add(line); // keep header
            }
            
            if (header == null) {
                System.out.println("Error: Invalid CSV format");
                return;
            }
            
            // find Title and OpportunityStatus columns
            int titleCol = -1, statusCol = -1;
            for (int i = 0; i < header.length; i++) {
                String h = header[i].trim();
                if (h.equalsIgnoreCase("Title")) titleCol = i;
                else if (h.equalsIgnoreCase("OpportunityStatus")) statusCol = i;
            }
            
            // read and update rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] row = line.split(",", -1);
                
                String title = titleCol >= 0 && row.length > titleCol ? row[titleCol].trim() : "";
                
                if (title.equalsIgnoreCase(internshipTitle)) {
                    found = true;
                    // update opportunity status to REJECTED
                    if (statusCol >= 0 && row.length > statusCol) {
                        row[statusCol] = OpportunityStatus.REJECTED.name();
                    }
                    lines.add(String.join(",", row));
                    System.out.println("Internship '" + internshipTitle + "' has been rejected.");
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading internships CSV: " + e.getMessage());
            return;
        }
        
        if (!found) {
            System.out.println("Internship with title '" + internshipTitle + "' not found.");
            return;
        }
        
        // write back to CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FilePaths.INTERNSHIPS_LIST_CSV))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to internships CSV: " + e.getMessage());
        }
    }

    // menu for approving withdrawal requests
    private void approveWithdrawalRequestMenu(Scanner scanner) {
        // read all pending withdrawals from applications CSV
        List<String[]> pendingWithdrawals = new ArrayList<>();
        String[] header = null;
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            String line;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
            }
            
            if (header == null) return;
            
            // find column index for withdrawal decision
            int withdrawCol = -1;
            for (int i = 0; i < header.length; i++) {
                String h = header[i].trim().toLowerCase();
                if (h.equals("withdrawaldecision")) {
                    withdrawCol = i;
                    break;
                }
            }
            
            // read applications with pending withdrawals
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                
                String withdrawStatus = withdrawCol >= 0 && row.length > withdrawCol ? 
                                       row[withdrawCol].trim() : "NONE";
                
                if (withdrawStatus.equalsIgnoreCase("PENDING")) {
                    pendingWithdrawals.add(row);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading applications CSV: " + e.getMessage());
            return;
        }
        
        if (pendingWithdrawals.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            return;
        }
        
        // display pending withdrawals
        System.out.println("\n=== Pending Withdrawal Requests ===");
        for (int i = 0; i < pendingWithdrawals.size(); i++) {
            String[] row = pendingWithdrawals.get(i);
            System.out.println((i + 1) + ". Student: " + row[0] + ", Internship: " + row[1]);
        }
        
        System.out.print("Select withdrawal request to process (0 to cancel): ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        if (choice <= 0 || choice > pendingWithdrawals.size()) {
            System.out.println("Cancelled.");
            return;
        }
        
        System.out.print("Approve withdrawal? (yes/no): ");
        String decision = scanner.nextLine().trim().toLowerCase();
        
        WithdrawalDecision finalDecision = decision.equals("yes") ? 
                                           WithdrawalDecision.APPROVED : 
                                           WithdrawalDecision.REJECTED;
        
        // update the application
        String[] selectedRow = pendingWithdrawals.get(choice - 1);
        updateWithdrawalDecision(selectedRow[0], selectedRow[1], finalDecision);
    }

    // update withdrawal decision in CSV
    private void updateWithdrawalDecision(String studentId, String internshipTitle, WithdrawalDecision decision) {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            String line;
            String[] header = null;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                lines.add(line);
            }
            
            if (header == null) return;
            
            // find column indices
            int studentIdCol = -1, titleCol = -1, withdrawCol = -1;
            for (int i = 0; i < header.length; i++) {
                String h = header[i].trim().toLowerCase();
                if (h.equals("studentid")) studentIdCol = i;
                else if (h.equals("internshiptitle")) titleCol = i;
                else if (h.equals("withdrawaldecision")) withdrawCol = i;
            }
            
            // read and update rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] row = line.split(",", -1);
                
                String sid = studentIdCol >= 0 && row.length > studentIdCol ? row[studentIdCol].trim() : "";
                String title = titleCol >= 0 && row.length > titleCol ? row[titleCol].trim() : "";
                
                if (sid.equals(studentId) && title.equalsIgnoreCase(internshipTitle)) {
                    // update withdrawal decision
                    if (withdrawCol >= 0 && row.length > withdrawCol) {
                        row[withdrawCol] = decision.name();
                    }
                    lines.add(String.join(",", row));
                    System.out.println("Withdrawal decision updated to " + decision + ".");
                    
                    // Slot management: Return slot if withdrawal approved
                    if (decision == WithdrawalDecision.APPROVED) {
                        if (SlotManager.updateSlotCount(internshipTitle, +1)) {
                            System.out.println("Slot returned for internship: " + internshipTitle);
                        }
                    }
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading applications CSV: " + e.getMessage());
            return;
        }
        
        // write back to CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to applications CSV: " + e.getMessage());
        }
    }

    // generate report of all applications
    private void generateReport() {
        System.out.println("\n=== Applications Report ===");
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            String line;
            String[] header = null;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                // print header
                System.out.println(String.join(" | ", header));
                System.out.println("-".repeat(80));
            }
            
            // print all applications
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                System.out.println(String.join(" | ", row));
            }
        } catch (IOException e) {
            System.out.println("Error reading applications CSV: " + e.getMessage());
        }
    }

    // menu for filtering internships
    private void filterInternshipsMenu(Scanner scanner) {
        System.out.println("\n=== Filter Internships ===");
        System.out.println("1. Filter by Status");
        System.out.println("2. Filter by Major");
        System.out.println("3. Filter by Level");
        System.out.println("4. Filter by Company");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        String filterType = "";
        
        switch (choice) {
            case "1": filterType = "status"; break;
            case "2": filterType = "major"; break;
            case "3": filterType = "level"; break;
            case "4": filterType = "company"; break;
            default:
                System.out.println("Invalid choice!");
                return;
        }
        
        System.out.print("Enter filter value: ");
        String filterValue = scanner.nextLine().trim();
        
        List<Internships> filtered = filteringInternships(filterType, filterValue);
        
        if (filtered.isEmpty()) {
            System.out.println("No internships match the filter.");
        } else {
            System.out.println("\n=== Filtered Internships ===");
            for (Internships internship : filtered) {
                System.out.println("Title: " + internship.getTitle());
                System.out.println("Company: " + internship.getCompanyName());
                System.out.println("Level: " + internship.getInternshipLevel());
                System.out.println("Major: " + internship.getPreferredMajor());
                System.out.println("Status: " + internship.getOpportunityStatus());
                System.out.println("---");
            }
        }
        
        // save filter preference
        setFilterPreference(filterType, filterValue);
    }

    @Override
    public List<Internships> filteringInternships(String filterType, String filterValue) {
        List<Internships> allInternships = new ArrayList<>();
        
        // read all internships from CSV
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            String[] header = null;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
            }
            
            if (header == null) return allInternships;
            
            // find column indices
            int titleCol = -1, descCol = -1, levelCol = -1, majorCol = -1, yearCol = -1;
            int openCol = -1, closeCol = -1, statusCol = -1, companyCol = -1, slotsCol = -1, visCol = -1;
            
            for (int i = 0; i < header.length; i++) {
                String h = header[i].trim().toLowerCase();
                if (h.equals("title")) titleCol = i;
                else if (h.equals("description")) descCol = i;
                else if (h.equals("internlevel")) levelCol = i;
                else if (h.equals("preferredmajor")) majorCol = i;
                else if (h.equals("openingdate")) openCol = i;
                else if (h.equals("closingdate")) closeCol = i;
                else if (h.equals("opportunitystatus")) statusCol = i;
                else if (h.equals("companyname")) companyCol = i;
                else if (h.equals("slots")) slotsCol = i;
                else if (h.equals("visibility")) visCol = i;
            }
            
            // read data rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] row = line.split(",", -1);
                
                // parse fields
                String title = titleCol >= 0 && row.length > titleCol ? row[titleCol].trim() : "";
                String desc = descCol >= 0 && row.length > descCol ? row[descCol].trim() : "";
                InternshipLevel level = InternshipLevel.BASIC;
                try {
                    if (levelCol >= 0 && row.length > levelCol) {
                        level = InternshipLevel.valueOf(row[levelCol].trim().toUpperCase());
                    }
                } catch (IllegalArgumentException e) { /* use default */ }
                
                String major = majorCol >= 0 && row.length > majorCol ? row[majorCol].trim() : "";
                int year = 1;
                try {
                    if (yearCol >= 0 && row.length > yearCol) {
                        year = Integer.parseInt(row[yearCol].trim());
                    }
                } catch (NumberFormatException e) { /* use default */ }
                
                int openDate = openCol >= 0 && row.length > openCol ? parseDate(row[openCol].trim()) : 0;
                int closeDate = closeCol >= 0 && row.length > closeCol ? parseDate(row[closeCol].trim()) : 0;
                
                OpportunityStatus status = OpportunityStatus.PENDING;
                try {
                    if (statusCol >= 0 && row.length > statusCol) {
                        status = OpportunityStatus.valueOf(row[statusCol].trim().toUpperCase());
                    }
                } catch (IllegalArgumentException e) { /* use default */ }
                
                String company = companyCol >= 0 && row.length > companyCol ? row[companyCol].trim() : "";
                int slots = 1;
                try {
                    if (slotsCol >= 0 && row.length > slotsCol) {
                        slots = Integer.parseInt(row[slotsCol].trim());
                    }
                } catch (NumberFormatException e) { /* use default */ }
                
                boolean visible = visCol >= 0 && row.length > visCol && 
                                 row[visCol].trim().equalsIgnoreCase("true");
                
                Internships internship = new Internships(title, desc, level, major, year,
                        openDate, closeDate, status, company, new String[0], slots, visible);
                allInternships.add(internship);
            }
        } catch (IOException e) {
            System.out.println("Error reading internships CSV: " + e.getMessage());
        }
        
        // filter based on filterType and filterValue
        List<Internships> filtered = new ArrayList<>();
        for (Internships internship : allInternships) {
            boolean matches = false;
            switch (filterType.toLowerCase()) {
                case "status":
                    matches = internship.getOpportunityStatus().name().equalsIgnoreCase(filterValue);
                    break;
                case "major":
                    matches = internship.getPreferredMajor().equalsIgnoreCase(filterValue);
                    break;
                case "level":
                    matches = internship.getInternshipLevel().name().equalsIgnoreCase(filterValue);
                    break;
                case "company":
                    matches = internship.getCompanyName().equalsIgnoreCase(filterValue);
                    break;
                default:
                    matches = true; // no filter
            }
            if (matches) filtered.add(internship);
        }
        
        return filtered;
    }
    
    // helper method to parse date string (YYYY-MM-DD) to integer (YYYYMMDD)
    private int parseDate(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                return Integer.parseInt(parts[0]) * 10000 + 
                       Integer.parseInt(parts[1]) * 100 + 
                       Integer.parseInt(parts[2]);
            }
        } catch (Exception e) { /* ignore */ }
        return 0;
    }
    
    // QoL: Show list of PENDING company reps
    private void showPendingCompanyReps() {
        System.out.println("\n--- Pending Company Representatives ---");
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.REPS_CSV))) {
            String line;
            String[] header = null;
            int idCol = -1, nameCol = -1, companyCol = -1, statusCol = -1;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].trim().equalsIgnoreCase("ID")) idCol = i;
                    else if (header[i].trim().equalsIgnoreCase("Name")) nameCol = i;
                    else if (header[i].trim().equalsIgnoreCase("CompanyName")) companyCol = i;
                    else if (header[i].trim().equalsIgnoreCase("RegStatus")) statusCol = i;
                }
            }
            
            if (idCol == -1 || statusCol == -1) {
                System.out.println("CSV format error.");
                return;
            }
            
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                
                if (row.length > statusCol && row[statusCol].trim().equalsIgnoreCase("PENDING")) {
                    found = true;
                    String id = row.length > idCol ? row[idCol] : "N/A";
                    String name = row.length > nameCol ? row[nameCol] : "N/A";
                    String company = row.length > companyCol ? row[companyCol] : "N/A";
                    System.out.printf("ID: %s | Name: %s | Company: %s%n", id, name, company);
                }
            }
            
            if (!found) {
                System.out.println("No pending company representatives.");
            }
            System.out.println("-----------------------------------");
        } catch (IOException e) {
            System.out.println("Error reading company reps: " + e.getMessage());
        }
    }
    
    // QoL: Show list of PENDING internships
    private void showPendingInternships() {
        System.out.println("\n--- Pending Internship Opportunities ---");
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            String[] header = null;
            int titleCol = -1, companyCol = -1, statusCol = -1;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].trim().equalsIgnoreCase("Title")) titleCol = i;
                    else if (header[i].trim().equalsIgnoreCase("CompanyName")) companyCol = i;
                    else if (header[i].trim().equalsIgnoreCase("OpportunityStatus")) statusCol = i;
                }
            }
            
            if (titleCol == -1 || statusCol == -1) {
                System.out.println("CSV format error.");
                return;
            }
            
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                
                if (row.length > statusCol && row[statusCol].trim().equalsIgnoreCase("PENDING")) {
                    found = true;
                    String title = row.length > titleCol ? row[titleCol] : "N/A";
                    String company = row.length > companyCol ? row[companyCol] : "N/A";
                    System.out.printf("Title: %s | Company: %s%n", title, company);
                }
            }
            
            if (!found) {
                System.out.println("No pending internship opportunities.");
            }
            System.out.println("----------------------------------------");
        } catch (IOException e) {
            System.out.println("Error reading internships: " + e.getMessage());
        }
    }
}