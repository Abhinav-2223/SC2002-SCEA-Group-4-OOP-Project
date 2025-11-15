import database.FilePaths;
import enums.InternshipLevel;
import enums.OpportunityStatus;

import java.io.*;
import java.util.*;

/**
 * Utility class for common CSV operations to reduce code duplication.
 * Provides methods for reading internships, finding column indices, and parsing data.
 */
public class CSVUtils {
    
    /**
     * Reads all internships from CSV that match a filter condition.
     * @param filter Predicate to filter internships (null for all)
     * @return List of matching internships
     */
    public static List<Internships> readInternshipsFromCSV(java.util.function.Predicate<Internships> filter) {
        List<Internships> internships = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(FilePaths.INTERNSHIPS_LIST_CSV))) {
            String line;
            String[] header = null;
            
            if ((line = br.readLine()) != null) {
                header = line.split(",", -1);
            }
            
            if (header == null) return internships;
            
            // find column indices
            Map<String, Integer> columnMap = buildColumnMap(header);
            
            // read data rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] row = line.split(",", -1);
                Internships internship = parseInternshipRow(row, columnMap);
                
                if (internship != null && (filter == null || filter.test(internship))) {
                    internships.add(internship);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading internships CSV: " + e.getMessage());
        }
        
        return internships;
    }
    
    /**
     * Builds a map of column names to their indices.
     */
    private static Map<String, Integer> buildColumnMap(String[] header) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            columnMap.put(header[i].trim().toLowerCase(), i);
        }
        return columnMap;
    }
    
    /**
     * Parses a CSV row into an Internships object.
     */
    private static Internships parseInternshipRow(String[] row, Map<String, Integer> columnMap) {
        try {
            String title = getColumnValue(row, columnMap, "title", "");
            String desc = getColumnValue(row, columnMap, "description", "");
            
            InternshipLevel level = InternshipLevel.BASIC;
            try {
                level = InternshipLevel.valueOf(
                    getColumnValue(row, columnMap, "internlevel", "BASIC").toUpperCase()
                );
            } catch (IllegalArgumentException ignored) {}
            
            String major = getColumnValue(row, columnMap, "preferredmajor", "");
            int year = getIntValue(row, columnMap, "preferredyear", 1);
            int openDate = parseDate(getColumnValue(row, columnMap, "openingdate", ""));
            int closeDate = parseDate(getColumnValue(row, columnMap, "closingdate", ""));
            
            OpportunityStatus status = OpportunityStatus.PENDING;
            try {
                status = OpportunityStatus.valueOf(
                    getColumnValue(row, columnMap, "opportunitystatus", "PENDING").toUpperCase()
                );
            } catch (IllegalArgumentException ignored) {}
            
            String company = getColumnValue(row, columnMap, "companyname", "");
            int slots = getIntValue(row, columnMap, "slots", 1);
            boolean visible = getColumnValue(row, columnMap, "visibility", "false")
                .trim().equalsIgnoreCase("true");
            
            return new Internships(title, desc, level, major, year, openDate, closeDate,
                    status, company, new String[0], slots, visible);
        } catch (Exception e) {
            return null; // skip malformed rows
        }
    }
    
    /**
     * Gets a string value from a row using column map.
     */
    private static String getColumnValue(String[] row, Map<String, Integer> columnMap, 
                                        String columnName, String defaultValue) {
        Integer colIndex = columnMap.get(columnName);
        if (colIndex != null && row.length > colIndex) {
            return row[colIndex].trim();
        }
        return defaultValue;
    }
    
    /**
     * Gets an integer value from a row using column map.
     */
    private static int getIntValue(String[] row, Map<String, Integer> columnMap, 
                                   String columnName, int defaultValue) {
        try {
            String value = getColumnValue(row, columnMap, columnName, "");
            return value.isEmpty() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Parses a date string (YYYY-MM-DD) to integer format (YYYYMMDD).
     */
    public static int parseDate(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                return Integer.parseInt(parts[0]) * 10000 + 
                       Integer.parseInt(parts[1]) * 100 + 
                       Integer.parseInt(parts[2]);
            }
        } catch (Exception ignored) {}
        return 0;
    }
    
    /**
     * Formats an integer date (YYYYMMDD) to string format (YYYY-MM-DD).
     */
    public static String formatDate(int dateInt) {
        if (dateInt == 0) return "N/A";
        int year = dateInt / 10000;
        int month = (dateInt % 10000) / 100;
        int day = dateInt % 100;
        return String.format("%04d-%02d-%02d", year, month, day);
    }
    
    /**
     * Updates a CSV file by applying a row transformer function.
     * @param filePath Path to the CSV file
     * @param transformer Function that takes a row and returns updated row (or null to skip)
     * @return true if update succeeded
     */
    public static boolean updateCSV(String filePath, 
                                   java.util.function.Function<String[], String[]> transformer) {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            // keep header
            if ((line = br.readLine()) != null) {
                lines.add(line);
            }
            
            // transform rows
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] row = line.split(",", -1);
                String[] updated = transformer.apply(row);
                
                if (updated != null) {
                    lines.add(String.join(",", updated));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
            return false;
        }
        
        // write back
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Finds an internship by title in a list.
     */
    public static Internships findInternshipByTitle(List<Internships> internships, String title) {
        for (Internships internship : internships) {
            if (internship.getTitle().equalsIgnoreCase(title)) {
                return internship;
            }
        }
        return null;
    }
}
