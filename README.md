# Student Management System

A JavaFX-based desktop application for managing student records at Bennett University.

## Features

- Add new students with automatic admission number generation (starting from 1001)
- View all students in a table format
- Edit existing student records
- Delete student records
- Search for students by admission number
- Store data in a local SQLite database

## Technical Details

- Built with JavaFX 21.0.2
- Uses SQLite for data persistence
- Maven for dependency management
- Follows MVC (Model-View-Controller) architecture

## Prerequisites

- Java 17 or higher
- Maven 3.8.1 or higher

## Project Structure

```
student-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── bennett/
│   │   │           └── studentmanagement/
│   │   │               ├── Main.java
│   │   │               ├── controller/
│   │   │               │   ├── MainController.java
│   │   │               │   └── StudentFormController.java
│   │   │               ├── model/
│   │   │               │   └── Student.java
│   │   │               └── util/
│   │   │                   └── DatabaseUtil.java
│   │   └── resources/
│   │       └── com/
│   │           └── bennett/
│   │               └── studentmanagement/
│   │                   ├── styles/
│   │                   │   └── main.css
│   │                   └── views/
│   │                       ├── MainView.fxml
│   │                       └── StudentForm.fxml
│   └── test/
│       └── java/
│           └── com/
│               └── bennett/
│                   └── studentmanagement/
│                       └── tests/
├── pom.xml
└── README.md
```

## Building and Running

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   mvn clean javafx:run
   ```

## Database

The application uses SQLite and creates a database file named `student_management.db` in the project root directory. The database contains a single table:

```sql
CREATE TABLE students (
    admission_number TEXT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT UNIQUE,
    phone_number TEXT,
    department TEXT,
    program TEXT,
    semester INTEGER,
    address TEXT,
    date_of_birth TEXT
)
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 