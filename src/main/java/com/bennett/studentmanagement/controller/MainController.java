package com.bennett.studentmanagement.controller;

import com.bennett.studentmanagement.model.Student;
import com.bennett.studentmanagement.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class MainController {
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> admissionNumberColumn;
    @FXML
    private TableColumn<Student, String> firstNameColumn;
    @FXML
    private TableColumn<Student, String> lastNameColumn;
    @FXML
    private TableColumn<Student, String> emailColumn;
    @FXML
    private TableColumn<Student, String> departmentColumn;
    @FXML
    private TableColumn<Student, String> programColumn;
    @FXML
    private TableColumn<Student, Integer> semesterColumn;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAllStudents();
    }

    private void setupTableColumns() {
        admissionNumberColumn.setCellValueFactory(cellData -> cellData.getValue().admissionNumberProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        departmentColumn.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        programColumn.setCellValueFactory(cellData -> cellData.getValue().programProperty());
        semesterColumn.setCellValueFactory(cellData -> cellData.getValue().semesterProperty().asObject());

        // Add context menu for table rows
        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> handleEditStudent(row.getItem()));

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> handleDeleteStudent(row.getItem()));

            contextMenu.getItems().addAll(editItem, deleteItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void loadAllStudents() {
        try {
            studentList.setAll(DatabaseUtil.getAllStudents());
            studentTable.setItems(studentList);
        } catch (SQLException e) {
            showErrorDialog("Error", "Failed to load students: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddStudent() {
        showStudentFormDialog(new Student());
    }

    @FXML
    private void handleViewAllStudents() {
        loadAllStudents();
    }

    @FXML
    private void handleSearchStudent() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Student");
        dialog.setHeaderText("Enter Admission Number");
        dialog.setContentText("Admission Number:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(admissionNumber -> {
            try {
                Student student = DatabaseUtil.getStudentByAdmissionNumber(admissionNumber);
                if (student != null) {
                    studentList.clear();
                    studentList.add(student);
                } else {
                    showErrorDialog("Not Found", "Student with admission number " + admissionNumber + " not found.");
                }
            } catch (SQLException e) {
                showErrorDialog("Error", "Failed to search student: " + e.getMessage());
            }
        });
    }

    private void handleEditStudent(Student student) {
        showStudentFormDialog(student);
    }

    private void handleDeleteStudent(Student student) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Student");
        alert.setContentText("Are you sure you want to delete this student?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DatabaseUtil.deleteStudent(student.getAdmissionNumber());
                studentList.remove(student);
                showSuccessDialog("Success", "Student deleted successfully.");
            } catch (SQLException e) {
                showErrorDialog("Error", "Failed to delete student: " + e.getMessage());
            }
        }
    }

    private void showStudentFormDialog(Student student) {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(student.getAdmissionNumber() == null ? "Add New Student" : "Edit Student");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bennett/studentmanagement/views/StudentForm.fxml"));
            VBox form = loader.load();
            
            StudentFormController controller = loader.getController();
            controller.setStudent(student);
            controller.setMainController(this);
            controller.setDialog(dialog);

            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            
            dialog.showAndWait();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load student form: " + e.getMessage());
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshStudentList() {
        loadAllStudents();
    }
} 