package com.bennett.studentmanagement.util;

import com.bennett.studentmanagement.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:student_management.db";
    private static Connection connection;
    private static final Logger logger = Logger.getLogger(DatabaseUtil.class.getName());
    private static final int INITIAL_ADMISSION_NUMBER = 1001;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            logger.info("Database connection established successfully");
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize database connection", e);
            e.printStackTrace();
        }
    }

    private static void createTables() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" +
                "admission_number TEXT PRIMARY KEY," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL," +
                "email TEXT UNIQUE," +
                "phone_number TEXT," +
                "department TEXT," +
                "program TEXT," +
                "semester INTEGER," +
                "address TEXT," +
                "date_of_birth TEXT" +
                ")";
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            logger.info("Students table created/verified successfully");
        }
    }

    public static String generateNextAdmissionNumber() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(admission_number AS INTEGER)), 1000) FROM students";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            if (resultSet.next()) {
                int lastNumber = resultSet.getInt(1);
                int nextNumber = lastNumber + 1;
                logger.info("Generated next admission number: " + nextNumber);
                return String.valueOf(nextNumber);
            }
        }
        
        logger.info("No existing students, starting with initial admission number: " + INITIAL_ADMISSION_NUMBER);
        return String.valueOf(INITIAL_ADMISSION_NUMBER);
    }

    public static void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (admission_number, first_name, last_name, email, phone_number, " +
                "department, program, semester, address, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getAdmissionNumber());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setString(4, student.getEmail());
            statement.setString(5, student.getPhoneNumber());
            statement.setString(6, student.getDepartment());
            statement.setString(7, student.getProgram());
            statement.setInt(8, student.getSemester());
            statement.setString(9, student.getAddress());
            statement.setString(10, student.getDateOfBirth());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Added student with admission number: " + student.getAdmissionNumber() + 
                       ", rows affected: " + rowsAffected);
        }
    }

    public static List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                Student student = new Student();
                student.setAdmissionNumber(resultSet.getString("admission_number"));
                student.setFirstName(resultSet.getString("first_name"));
                student.setLastName(resultSet.getString("last_name"));
                student.setEmail(resultSet.getString("email"));
                student.setPhoneNumber(resultSet.getString("phone_number"));
                student.setDepartment(resultSet.getString("department"));
                student.setProgram(resultSet.getString("program"));
                student.setSemester(resultSet.getInt("semester"));
                student.setAddress(resultSet.getString("address"));
                student.setDateOfBirth(resultSet.getString("date_of_birth"));
                
                students.add(student);
            }
            logger.info("Retrieved " + students.size() + " students from database");
        }
        
        return students;
    }

    public static void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, email = ?, phone_number = ?, " +
                "department = ?, program = ?, semester = ?, address = ?, date_of_birth = ? " +
                "WHERE admission_number = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getEmail());
            statement.setString(4, student.getPhoneNumber());
            statement.setString(5, student.getDepartment());
            statement.setString(6, student.getProgram());
            statement.setInt(7, student.getSemester());
            statement.setString(8, student.getAddress());
            statement.setString(9, student.getDateOfBirth());
            statement.setString(10, student.getAdmissionNumber());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Updated student with admission number: " + student.getAdmissionNumber() + 
                       ", rows affected: " + rowsAffected);
        }
    }

    public static void deleteStudent(String admissionNumber) throws SQLException {
        String sql = "DELETE FROM students WHERE admission_number = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, admissionNumber);
            int rowsAffected = statement.executeUpdate();
            logger.info("Deleted student with admission number: " + admissionNumber + 
                       ", rows affected: " + rowsAffected);
        }
    }

    public static Student getStudentByAdmissionNumber(String admissionNumber) throws SQLException {
        String sql = "SELECT * FROM students WHERE admission_number = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, admissionNumber);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Student student = new Student();
                student.setAdmissionNumber(resultSet.getString("admission_number"));
                student.setFirstName(resultSet.getString("first_name"));
                student.setLastName(resultSet.getString("last_name"));
                student.setEmail(resultSet.getString("email"));
                student.setPhoneNumber(resultSet.getString("phone_number"));
                student.setDepartment(resultSet.getString("department"));
                student.setProgram(resultSet.getString("program"));
                student.setSemester(resultSet.getInt("semester"));
                student.setAddress(resultSet.getString("address"));
                student.setDateOfBirth(resultSet.getString("date_of_birth"));
                
                logger.info("Found student with admission number: " + admissionNumber);
                return student;
            }
        }
        
        logger.info("No student found with admission number: " + admissionNumber);
        return null;
    }
} 