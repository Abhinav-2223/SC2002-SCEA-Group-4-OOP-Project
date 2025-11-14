package database;

public final class FilePaths {
    private FilePaths() { } // prevent instantiation

    public static final String STUDENTS_CSV = "./database/student_list.csv";
    public static final String REPS_CSV     = "./database/company_reps_list.csv";
    public static final String STAFF_CSV    = "./database/staff_list.csv";
    public static final String INTERNSHIP_APPLICATIONS_CSV    = "./database/applications_list.csv";

    // contains entries of internship created by Company Reps
    public static final String INTERNSHIPS_LIST_CSV    = "./database/internships_list.csv";

    // contains entries of Title:Company Reps mappings
    public static final String INTERNSHIPS_REPS_MAP_CSV = "./database/internships_reps_map.csv";
}
