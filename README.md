# SC2002-SCEA-Group-4-OOP-Project

## Internship Placement Management System

A comprehensive Java-based system for managing internship opportunities, student applications, and company representative interactions with career center oversight.

---

## Table of Contents

- [System Overview](#system-overview)
- [Core Features](#core-features)
- [Architecture](#architecture)
- [Class Structure](#class-structure)
- [Design Principles](#design-principles)
- [Installation](#installation)
- [Usage](#usage)
- [Additional Features](#additional-features)

---

## System Overview

The Internship Placement Management System serves as a centralized platform connecting three types of users:

- **Students**: Browse and apply for internship opportunities
- **Company Representatives**: Create and manage internship postings
- **Career Center Staff**: Oversee and approve internships and registrations

### User Authentication

- **Students**: ID format `U#######X` (e.g., U2345123F)
- **Company Representatives**: Company email address
- **Career Center Staff**: NTU account
- **Default Password**: `password` (changeable by users)

---

## Core Features

### All Users
- Secure login/logout functionality
- Password change capability
- Role-based access control

### Students
- View internship opportunities filtered by:
  - Year of study (1-4)
  - Major
  - Internship level (Basic/Intermediate/Advanced)
- Apply for up to 3 internships simultaneously
- Year-based restrictions:
  - Year 1-2: Basic level only
  - Year 3-4: All levels
- View application status (Pending/Successful/Unsuccessful/Withdrawn)
- Accept internship placement (one placement only)
- Request application withdrawal (requires staff approval)

### Company Representatives
- Register company account (requires staff approval)
- Create up to 5 internship opportunities with details:
  - Title, description, level
  - Preferred major and year
  - Opening/closing dates
  - Number of slots (max 10)
- View all created internship opportunities
- View all internships in the system (with filtering)
- Approve/reject student applications
- Toggle internship visibility
- Delete internship postings
- View application details for each internship

### Career Center Staff
- Authorize company representative registrations
- Approve/reject internship opportunities
- Approve/reject student withdrawal requests
- Generate comprehensive reports with filters:
  - Status
  - Preferred major
  - Internship level
  - Company
- View all internships with filtering options

---

## Architecture

### Core Classes

#### User (Abstract Base Class)
```
Fields:
- userId: String
- password: String
- name: String
- domain: String
- filterPreferences: Map<String, String>

Methods:
- userLogin(String id, String password, String domain): boolean
- logout(): void
- changePassword(String userId, String domain, Scanner): void
- runUserUi(Scanner): void (abstract)
- filteringInternships(String filterType, String filterValue): List<Internships> (abstract)
```

#### Student extends User
```
Fields:
- studyYear: int
- major: String
- acceptedApplications: String

Key Methods:
- viewInternshipList(): void
- applyForInternship(Internships): void
- viewApplicationStatus(): void
- acceptInternship(Scanner): void
- withdrawApplication(Scanner): void
```

#### CompanyRep extends User
```
Fields:
- repId: String
- repName: String
- companyName: String
- department: String
- position: String
- regStatus: RepRegistrationStatus
- internshipsCreated: int

Key Methods:
- createInternships(Scanner): void
- viewCreatedInternships(): void
- viewAllInternships(Scanner): void
- deleteInternship(Scanner): void
- approveRejectInternship(Scanner): void
- toggleVisibility(Scanner): void
```

#### CareerCenStaff extends User
```
Fields:
- role: String
- department: String

Key Methods:
- viewAllInternships(Scanner): void
- authorizeCompanyRep(String repId): void
- approveInternship(String internshipTitle): void
- rejectInternship(String internshipTitle): void
- approveWithdrawalRequestMenu(Scanner): void
- generateReport(): void
```

### Supporting Classes

#### Internships
```
Fields:
- title: String
- description: String
- internshipLevel: InternshipLevel
- preferredMajor: String
- preferredYear: int
- openingDate: int
- closingDate: int
- oppStatus: OpportunityStatus
- companyName: String
- companyReps: String[]
- slots: int
- isVisible: boolean
- isApprovedByStaff: boolean

Methods:
- canApply(): boolean
- getAllVisibleInternships(): List<Internships>
```

#### StudentApplication
```
Fields:
- applicationID: String
- student: Student
- internships: Internships
- appStatus: ApplicationStatus
- withdrawDecision: WithdrawalDecision

Methods:
- displayApplicationDetails(): void
- loadApplicationsFromCSV(String studentId): List<StudentApplication>
- updateApplicationStatus(String studentId, String internshipTitle, String newStatus): void
```

### Utility Classes

#### CSVUtils
- Centralized CSV reading operations
- Predicate-based filtering
- Column mapping utilities
- Internship parsing

#### Helper
- CSV field extraction
- User loading factory pattern
- Password verification

#### SlotManager
- Manage internship slot counts
- Automatic status updates when slots filled

#### CompanyRepHelper
- Company representative specific utilities
- Registration status management

### Enumerations

```java
InternshipLevel { BASIC, INTERMEDIATE, ADVANCED }
OpportunityStatus { PENDING, VACANT, REJECTED, FILLED }
ApplicationStatus { PENDING, PENDING_WITHDRAWAL, SUCCESSFUL, UNSUCCESSFUL, WITHDRAWN, ACCEPTED }
RepRegistrationStatus { PENDING, APPROVED, REJECTED }
WithdrawalDecision { NONE, PENDING, APPROVED, REJECTED }
```

---

## Design Principles

### SOLID Principles Implementation

**Single Responsibility Principle**
- User: Authentication and user data management
- CSVUtils: CSV operations only
- SlotManager: Slot count management only
- Each class has one clear responsibility

**Open/Closed Principle**
- Abstract User class allows new user types without modification
- Filter system extensible through HashMap
- Predicate-based filtering open for new conditions

**Liskov Substitution Principle**
- Student, CompanyRep, CareerCenStaff interchangeable with User
- Factory pattern in Helper.loadUserFromCSV()
- Polymorphic method calls work across all user types

**Interface Segregation Principle**
- Abstract methods only when needed
- No fat interfaces forcing unnecessary implementations
- Utility classes provide focused functionality

**Dependency Inversion Principle**
- Code depends on abstract User, not concrete implementations
- Factory pattern decouples object creation
- FilePaths abstraction for data source independence

### Additional Design Patterns

**Factory Pattern**
- Helper.loadUserFromCSV() creates appropriate user types

**Strategy Pattern**
- filteringInternships() implemented differently per user type

**Template Method Pattern**
- Abstract runUserUi() with user-specific implementations

### Code Quality Features

**Encapsulation**
- Protected fields with public getters
- Private helper methods

**DRY Principle**
- Reusable utility methods
- Method overloading for different CSV types

**Separation of Concerns**
- Database operations separate from business logic
- UI code separate from data access

---

## Installation

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Command-line terminal

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Abhinav-2223/SC2002-SCEA-Group-4-OOP-Project.git
   ```

2. Navigate to the project directory:
   ```bash
   cd SC2002-SCEA-Group-4-OOP-Project
   ```

3. Compile the Java files:
   ```bash
   javac -d bin *.java database/*.java enums/*.java
   ```

4. Run the application:
   ```bash
   java -cp bin IPMSystemApp
   ```

---

## Usage

### Initial Setup
The system initializes with CSV files containing:
- Student list (student_list.csv)
- Staff list (staff_list.csv)
- Company representatives (company_reps_list.csv)
- Internships (internships_list.csv)
- Applications (applications_list.csv)

### Logging In
1. Select user domain (Student/Company Rep/Staff)
2. Enter User ID
3. Enter password (default: `password`)

### Student Workflow
1. Login with student credentials
2. View available internships (filtered by profile)
3. Apply for internships (max 3)
4. Check application status
5. Accept successful placement
6. Request withdrawal if needed

### Company Representative Workflow
1. Register account (awaits staff approval)
2. Create internship opportunities (max 5)
3. View and manage created internships
4. Review student applications
5. Approve/reject applications
6. Toggle internship visibility

### Career Center Staff Workflow
1. Authorize company representative accounts
2. Review and approve internship opportunities
3. Process withdrawal requests
4. Generate reports with filters
5. Monitor system activity

---

## Additional Features

Beyond the core requirements, this system includes:

### Enhanced Filtering System
- Default alphabetical (A-Z) sorting
- Filter by status, major, level, closing date
- Filter persistence across menu navigation
- Urgency-based closing date sorting (earliest first)

### Slot Management
- Automatic slot decrement on acceptance
- Automatic status change to FILLED when slots exhausted
- Slot return on withdrawal approval

### Pending Item Displays
- Career Staff can preview pending items before processing
- Clear visibility into system state

### View All Internships
- Company Reps and Staff can view all internships system-wide
- Filtering and sorting capabilities
- Not restricted to created internships only

### Robust Validation
- Empty field validation
- Date format validation
- Enum value validation
- Duplicate application prevention

### Comprehensive Status Tracking
- Application status progression
- Withdrawal request tracking (PENDING_WITHDRAWAL status)
- Internship approval workflow

### Auto-Withdrawal
- Automatic withdrawal of other applications when accepting placement
- Maintains system consistency

### Enhanced Reports
- Multiple filter combinations
- Accurate data aggregation
- Export-ready format

---

## Project Structure

```
SC2002-SCEA-Group-4-OOP-Project/
├── IPMSystemApp.java          # Main application entry point
├── User.java                  # Abstract base class
├── Student.java               # Student user implementation
├── CompanyRep.java            # Company representative implementation
├── CareerCenStaff.java        # Career center staff implementation
├── Internships.java           # Internship entity
├── StudentApplication.java    # Application entity
├── Helper.java                # Utility helper methods
├── CSVUtils.java              # CSV operations utility
├── SlotManager.java           # Slot management utility
├── CompanyRepHelper.java      # Company rep utilities
├── database/
│   ├── FilePaths.java         # File path constants
│   ├── student_list.csv       # Student data
│   ├── staff_list.csv         # Staff data
│   ├── company_reps_list.csv  # Company rep data
│   ├── internships_list.csv   # Internship data
│   ├── applications_list.csv  # Application data
│   └── internships_reps_map.csv # Internship-rep mapping
├── enums/
│   ├── ApplicationStatus.java
│   ├── InternshipLevel.java
│   ├── OpportunityStatus.java
│   ├── RepRegistrationStatus.java
│   └── WithdrawalDecision.java
└── README.md
```

---

## Contributors

SC2002 SCEA Group 4
- Abhinav-2223

---

## License

This project is developed as part of the SC2002 Object-Oriented Design and Programming course assignment.









