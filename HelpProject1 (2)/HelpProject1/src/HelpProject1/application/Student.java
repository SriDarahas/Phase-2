package HelpProject1.application;

import HelpProject1.application.User;

public class Student extends User {
    public Student(String email, String username, byte[] passwordHash, String firstName, String lastName) {
        super(email, username, passwordHash, firstName, lastName);
    }

    // Additional student-specific methods if needed
}
