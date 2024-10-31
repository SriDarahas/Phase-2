package HelpProject1.application;

public class Admin extends User {
    public Admin(String email, String username, byte[] passwordHash, String firstName, String lastName) {
        super(email, username, passwordHash, firstName, lastName);
        addRole(Role.ADMIN);
    }

    // Additional admin-specific methods if required
}
