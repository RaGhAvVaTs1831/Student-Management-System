package com.bennett.studentmanagement.controller;

import com.bennett.studentmanagement.model.Student;
import com.bennett.studentmanagement.util.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentFormController {
    private static final Logger logger = Logger.getLogger(StudentFormController.class.getName());

    @FXML
    private TextField admissionNumberField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private ComboBox<String> programComboBox;
    @FXML
    private ComboBox<Integer> semesterComboBox;
    @FXML
    private TextArea addressField;
    @FXML
    private DatePicker dateOfBirthPicker;
    @FXML
    private Label errorLabel;

    private Student student;
    private MainController mainController;
    private Dialog<?> dialog;

    @FXML
    public void initialize() {
        // Initialize department options
        departmentComboBox.getItems().addAll(
                "Computer Science",
                "Electronics and Communication",
                "Mechanical Engineering",
                "Civil Engineering",
                "Electrical Engineering",
                "Business Administration",
                "Law",
                "Media Studies"
        );

        // Initialize program options
        programComboBox.getItems().addAll(
                "B.Tech",
                "M.Tech",
                "BBA",
                "MBA",
                "LLB",
                "LLM",
                "B.A.",
                "M.A."
        );

        // Initialize semester options
        for (int i = 1; i <= 8; i++) {
            semesterComboBox.getItems().add(i);
        }

        // Make admission number field read-only
        admissionNumberField.setEditable(false);
    }

    public void setStudent(Student student) {
        this.student = student;
        if (student != null && student.getAdmissionNumber() != null) {
            admissionNumberField.setText(student.getAdmissionNumber());
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());
            emailField.setText(student.getEmail());
            phoneNumberField.setText(student.getPhoneNumber());
            departmentComboBox.setValue(student.getDepartment());
            programComboBox.setValue(student.getProgram());
            semesterComboBox.setValue(student.getSemester());
            addressField.setText(student.getAddress());
            if (student.getDateOfBirth() != null && !student.getDateOfBirth().isEmpty()) {
                dateOfBirthPicker.setValue(LocalDate.parse(student.getDateOfBirth()));
            }
            logger.info("Form populated with existing student data: " + student.getAdmissionNumber());
        } else {
            // Clear all fields for new student
            clearForm();
            // Generate new admission number
            try {
                String nextAdmissionNumber = DatabaseUtil.generateNextAdmissionNumber();
                admissionNumberField.setText(nextAdmissionNumber);
                logger.info("Generated new admission number: " + nextAdmissionNumber);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to generate admission number", e);
                showError("Failed to generate admission number: " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        admissionNumberField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneNumberField.clear();
        departmentComboBox.setValue(null);
        programComboBox.setValue(null);
        semesterComboBox.setValue(null);
        addressField.clear();
        dateOfBirthPicker.setValue(null);
        errorLabel.setVisible(false);
        logger.info("Form cleared for new student");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setDialog(Dialog<?> dialog) {
        this.dialog = dialog;
    }

    @FXML
    private void handleSave() {
        logger.info("Save button clicked");
        if (validateForm()) {
            try {
                Student updatedStudent = new Student();
                updatedStudent.setAdmissionNumber(admissionNumberField.getText());
                updatedStudent.setFirstName(firstNameField.getText());
                updatedStudent.setLastName(lastNameField.getText());
                updatedStudent.setEmail(emailField.getText());
                updatedStudent.setPhoneNumber(phoneNumberField.getText());
                updatedStudent.setDepartment(departmentComboBox.getValue());
                updatedStudent.setProgram(programComboBox.getValue());
                updatedStudent.setSemester(semesterComboBox.getValue());
                updatedStudent.setAddress(addressField.getText());
                updatedStudent.setDateOfBirth(dateOfBirthPicker.getValue().format(DateTimeFormatter.ISO_DATE));

                logger.info("Attempting to save student with admission number: " + updatedStudent.getAdmissionNumber());
                
                if (student == null || student.getAdmissionNumber() == null) {
                    DatabaseUtil.addStudent(updatedStudent);
                    logger.info("New student added successfully");
                } else {
                    DatabaseUtil.updateStudent(updatedStudent);
                    logger.info("Existing student updated successfully");
                }

                mainController.refreshStudentList();
                dialog.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to save student", e);
                showError("Failed to save student: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        logger.info("Cancel button clicked");
        dialog.close();
    }

    private boolean validateForm() {
        logger.info("Validating form");
        if (admissionNumberField.getText().isEmpty()) {
            showError("Admission Number is required");
            return false;
        }
        if (firstNameField.getText().isEmpty()) {
            showError("First Name is required");
            return false;
        }
        if (lastNameField.getText().isEmpty()) {
            showError("Last Name is required");
            return false;
        }
        if (emailField.getText().isEmpty()) {
            showError("Email is required");
            return false;
        }
        if (departmentComboBox.getValue() == null) {
            showError("Department is required");
            return false;
        }
        if (programComboBox.getValue() == null) {
            showError("Program is required");
            return false;
        }
        if (semesterComboBox.getValue() == null) {
            showError("Semester is required");
            return false;
        }
        if (dateOfBirthPicker.getValue() == null) {
            showError("Date of Birth is required");
            return false;
        }

        logger.info("Form validation successful");
        return true;
    }

    private void showError(String message) {
        logger.warning("Validation error: " + message);
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
} 