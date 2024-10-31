package HelpProject1.application;

public class Instructor extends User {
    public Instructor(String email, String username, byte[] passwordHash, String firstName, String lastName) {
        super(email, username, passwordHash, firstName, lastName);
        addRole(Role.INSTRUCTOR);
    }

    // Additional instructor-specific methods if needed
}