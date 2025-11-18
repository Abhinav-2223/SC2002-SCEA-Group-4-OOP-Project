import database.FilePaths;
import enums.InternshipLevel;
import enums.OpportunityStatus;
import enums.RepRegistrationStatus;

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
            System.out.println("1. View All Internships");
            System.out.println("2. Approve/Reject Company Representative");
            System.out.println("3. Approve Internship Opportunity");
            System.out.println("4. Reject Internship Opportunity");
            System.out.println("5. Approve Withdrawal Request");
            System.out.println("6. Generate Report");
            System.out.println("7. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    viewAllInternships(scanner);
                    break;
                case "2":
                    // show pending company reps before prompting
                    showPendingCompanyReps();
                    System.out.print("Enter Company Rep ID to process: ");
                    String repId = scanner.nextLine().trim();
                    System.out.print("Approve or Reject? (A/R): ");
                    String decision = scanner.nextLine().trim().toUpperCase();
                    if (decision.equals("A")) {
                        approveOrRejectCompanyRep(repId, true);
                    } else if (decision.equals("R")) {
                        approveOrRejectCompanyRep(repId, false);
                    } else {
                        System.out.println("Invalid choice. Operation cancelled.");
                    }
                    break;
                case "3":
                    // show pending internships before prompting
                    showPendingInternships();
                    System.out.print("Enter Internship Title to approve: ");
                    String internTitle = scanner.nextLine().trim();
                    approveInternship(internTitle);
                    break;
                case "4":
                    // show pending internships before prompting for rejection
                    showPendingInternships();
                    System.out.print("Enter Internship Title to reject: ");
                    String rejectTitle = scanner.nextLine().trim();
                    rejectInternship(rejectTitle);
                    break;
                case "5":
                    approveWithdrawalRequestMenu(scanner);
                    break;
                case "6":
                    generateReport();
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

    // approve or reject company rep by ID
    private void approveOrRejectCompanyRep(String repId, boolean approve) {
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
                    // update registration status to APPROVED or REJECTED
                    if (regStatusCol >= 0 && row.length > regStatusCol) {
                        row[regStatusCol] = approve ? RepRegistrationStatus.APPROVED.name() : RepRegistrationStatus.REJECTED.name();
                    }
                    lines.add(String.join(",", row));
                    System.out.println("Company Rep " + repId + " has been " + (approve ? "approved" : "rejected") + ".");
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

    // reject internship opportunity and update status to rejected
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
        // read all pending withdrawals from applications CSV (status = PENDING_WITHDRAWAL)
        List<String[]> pendingWithdrawals = new ArrayList<>();
        String[] header = null;
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            String line;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
            }
            
            if (header == null) return;
            
            // find column indices
            int idCol = -1, internshipCol = -1, statusCol = -1;
            for (int i = 0; i < header.length; i++) {
                String h = header[i].trim().toLowerCase();
                if (h.equals("id")) {
                    idCol = i;
                } else if (h.equals("appliedinternship")) {
                    internshipCol = i;
                } else if (h.equals("applicationstatus")) {
                    statusCol = i;
                }
            }
            
            // Validate that we found all required columns
            if (idCol < 0 || internshipCol < 0 || statusCol < 0) {
                System.out.println("Error: Required columns not found in CSV.");
                return;
            }
            
            // read applications with PENDING_WITHDRAWAL status
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);
                
                String status = statusCol >= 0 && row.length > statusCol ? 
                               row[statusCol].trim() : "";
                
                if (status.equalsIgnoreCase("PENDING_WITHDRAWAL")) {
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
        
        // Re-read to get column indices for display (they were local to try block)
        int displayIdCol = -1, displayInternshipCol = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIP_APPLICATIONS_CSV))) {
            String line = br.readLine();
            if (line != null) {
                String[] headerRow = line.split(",", -1);
                for (int i = 0; i < headerRow.length; i++) {
                    String h = headerRow[i].trim().toLowerCase();
                    if (h.equals("id")) displayIdCol = i;
                    else if (h.equals("appliedinternship")) displayInternshipCol = i;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading header: " + e.getMessage());
            return;
        }
        
        for (int i = 0; i < pendingWithdrawals.size(); i++) {
            String[] row = pendingWithdrawals.get(i);
            String studentId = displayIdCol >= 0 && row.length > displayIdCol ? row[displayIdCol] : "Unknown";
            String internship = displayInternshipCol >= 0 && row.length > displayInternshipCol ? row[displayInternshipCol] : "Unknown";
            System.out.println((i + 1) + ". Student ID: " + studentId + ", Internship: " + internship + ", Status: PENDING_WITHDRAWAL");
        }
        
        System.out.print("Select withdrawal request to process (0 to cancel): ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        if (choice <= 0 || choice > pendingWithdrawals.size()) {
            System.out.println("Cancelled.");
            return;
        }
        
        System.out.print("Approve withdrawal? (yes/no): ");
        String decision = scanner.nextLine().trim().toLowerCase();
        
        // update the application status based on decision
        String[] selectedRow = pendingWithdrawals.get(choice - 1);
        String studentId = displayIdCol >= 0 && selectedRow.length > displayIdCol ? selectedRow[displayIdCol] : "";
        String internship = displayInternshipCol >= 0 && selectedRow.length > displayInternshipCol ? selectedRow[displayInternshipCol] : "";
        
        if (decision.equals("yes")) {
            // Approve: Change status to WITHDRAWN
            StudentApplication.updateApplicationStatus(studentId, internship, "WITHDRAWN");
            System.out.println("Withdrawal approved. Application status changed to WITHDRAWN.");
            
            // Return slot to internship
            if (SlotManager.updateSlotCount(internship, +1)) {
                System.out.println("Slot returned for internship: " + internship);
            }
        } else {
            // Reject: Revert status back to PENDING
            StudentApplication.updateApplicationStatus(studentId, internship, "PENDING");
            System.out.println("Withdrawal rejected. Application status reverted to PENDING.");
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

    // view all internships with filtering options
    private void viewAllInternships(Scanner scanner) {
        // Load all internships
        List<Internships> allInternships = CSVUtils.readInternshipsFromCSV(null);
        
        if (allInternships.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }
        
        // Sort alphabetically by title (A-Z)
        allInternships.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        
        // Display all internships first (default alphabetical view)
        System.out.println("\n=== All Internships (Alphabetically) ===");
        for (Internships internship : allInternships) {
            System.out.println("------------------------------");
            System.out.println("Title: " + internship.getTitle());
            System.out.println("Company: " + internship.getCompanyName());
            System.out.println("Description: " + internship.getDescription());
            System.out.println("Level: " + internship.getInternshipLevel());
            System.out.println("Major: " + internship.getPreferredMajor());
            System.out.println("Year: " + internship.getPreferredYear());
            System.out.println("Closing Date: " + internship.getClosingDate());
            System.out.println("Status: " + internship.getOpportunityStatus());
            System.out.println("Slots: " + internship.getSlots());
            System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No"));
            System.out.println("Approved: " + (internship.isApprovedByStaff() ? "Yes" : "No"));
        }
        System.out.println("------------------------------");
        
        // Ask if user wants to filter/sort
        System.out.println("\n=== Filter/Sort Options ===");
        System.out.println("1. Keep current view (Alphabetically)");
        System.out.println("2. Filter by Status");
        System.out.println("3. Filter by Major");
        System.out.println("4. Filter by Level");
        System.out.println("5. Sort by Closing Date");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        
        List<Internships> displayList = new ArrayList<>(allInternships);
        
        switch (choice) {
            case "1" -> {
                // Already displayed, just return
                return;
            }
            case "2" -> {
                System.out.print("Enter status (PENDING/VACANT/FILLED/REJECTED): ");
                String status = scanner.nextLine().trim().toUpperCase();
                displayList = allInternships.stream()
                    .filter(i -> i.getOpportunityStatus().name().equalsIgnoreCase(status))
                    .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                    .collect(java.util.stream.Collectors.toList());
                setFilterPreference("status", status);
            }
            case "3" -> {
                System.out.print("Enter major: ");
                String major = scanner.nextLine().trim();
                displayList = allInternships.stream()
                    .filter(i -> i.getPreferredMajor().equalsIgnoreCase(major))
                    .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                    .collect(java.util.stream.Collectors.toList());
                setFilterPreference("major", major);
            }
            case "4" -> {
                System.out.print("Enter level (BASIC/INTERMEDIATE/ADVANCED): ");
                String level = scanner.nextLine().trim().toUpperCase();
                displayList = allInternships.stream()
                    .filter(i -> i.getInternshipLevel().name().equalsIgnoreCase(level))
                    .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                    .collect(java.util.stream.Collectors.toList());
                setFilterPreference("level", level);
            }
            case "5" -> {
                // Sort by closing date (earliest first)
                displayList.sort((a, b) -> Integer.compare(a.getClosingDate(), b.getClosingDate()));
                setFilterPreference("closingdate", "applied");
            }
            case "0" -> {
                return;
            }
            default -> {
                System.out.println("Invalid choice!");
                return;
            }
        }
        
        // Display filtered/sorted internships
        if (displayList.isEmpty()) {
            System.out.println("\nNo internships match the criteria.");
            return;
        }
        
        System.out.println("\n=== Filtered/Sorted Internships ===");
        for (Internships internship : displayList) {
            System.out.println("------------------------------");
            System.out.println("Title: " + internship.getTitle());
            System.out.println("Company: " + internship.getCompanyName());
            System.out.println("Description: " + internship.getDescription());
            System.out.println("Level: " + internship.getInternshipLevel());
            System.out.println("Major: " + internship.getPreferredMajor());
            System.out.println("Year: " + internship.getPreferredYear());
            System.out.println("Closing Date: " + internship.getClosingDate());
            System.out.println("Status: " + internship.getOpportunityStatus());
            System.out.println("Slots: " + internship.getSlots());
            System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No"));
            System.out.println("Approved: " + (internship.isApprovedByStaff() ? "Yes" : "No"));
        }
        System.out.println("------------------------------");
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
    
    // display all company representatives with pending registration status
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
    
    // display all internship opportunities with pending approval status
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