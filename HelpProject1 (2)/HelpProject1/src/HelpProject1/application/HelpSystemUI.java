package HelpProject1.application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class HelpSystemUI {
    private Stage stage;
    private HelpSystem helpSystem;
    private AuthService authService;

    public HelpSystemUI(Stage stage) {
        this.stage = stage;
        this.helpSystem = new HelpSystem();
        this.authService = new AuthService(helpSystem);
    }

    public void show() {
        showWelcomeScreen();
    }

    private void showWelcomeScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to the CSE 360 Help System");

        Button loginButton = new Button("Login");
        Button signupButton = new Button("Signup");

        // Button actions
        loginButton.setOnAction(e -> showLoginScreen());

        signupButton.setOnAction(e -> {
            if (!helpSystem.hasAdmin()) {
                // No admin exists, direct to admin signup
                showAdminSignup();
            } else {
                // Admin exists, direct to regular signup
                showSignUpScreen(new HashSet<>());
            }
        });

        FlowPane buttons = new FlowPane(10, 0);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(loginButton, signupButton);

        root.getChildren().addAll(welcomeLabel, buttons);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void showAdminSignup() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label prompt = new Label("Create Admin Account");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button createAdminButton = new Button("Create Admin");

        createAdminButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "All fields are required.").showAndWait();
                return;
            }

            if (!password.equals(confirmPassword)) {
                new Alert(Alert.AlertType.ERROR, "Passwords do not match.").showAndWait();
                return;
            }

            if (helpSystem.getUser(username) != null) {
                new Alert(Alert.AlertType.ERROR, "Username already exists.").showAndWait();
                return;
            }

            byte[] passwordHash = authService.hashPassword(password);
            Admin admin = new Admin(email, username, passwordHash, "Admin", "User");

            helpSystem.addUser(admin);

            new Alert(Alert.AlertType.INFORMATION, "Admin account created successfully!").showAndWait();
            showLoginScreen();
        });

        root.getChildren().addAll(prompt, usernameField, passwordField, confirmPasswordField, emailField, createAdminButton);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showLoginScreen() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Login to the CSE 360 Help System");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField invitationCodeField = new TextField();
        invitationCodeField.setPromptText("Invitation Code (if any)");

        Button loginButton = new Button("Login");
        Button signupButton = new Button("Signup with Code");
        Button resetPasswordButton = new Button("Reset Password");

        FlowPane buttons = new FlowPane(10, 0);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(loginButton, signupButton, resetPasswordButton);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Both fields are required.").showAndWait();
                return;
            }

            boolean isAuthenticated = authService.authenticate(username, password);

            if (isAuthenticated) {
                User user = helpSystem.getUser(username);
                helpSystem.setCurrentUser(user);
                if (user.isFirstLogin()) {
                    showFinishAccountSetup(user);
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Login successful!").showAndWait();
                    showDashboard(user);
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid credentials.").showAndWait();
            }
        });

        signupButton.setOnAction(e -> {
            String invitationCode = invitationCodeField.getText().trim();
            if (helpSystem.validateInvitationCode(invitationCode)) {
                Set<User.Role> roles = helpSystem.getRolesForInvitationCode(invitationCode);
                showSignUpScreen(roles);
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid invitation code.").showAndWait();
            }
        });

        resetPasswordButton.setOnAction(e -> showResetPasswordRequestScreen());

        root.getChildren().addAll(welcomeLabel, usernameField, passwordField, invitationCodeField, buttons);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showSignUpScreen(Set<User.Role> roles) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label prompt = new Label("Create New Account");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button createAccountButton = new Button("Create Account");

        createAccountButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "All fields are required.").showAndWait();
                return;
            }

            if (!password.equals(confirmPassword)) {
                new Alert(Alert.AlertType.ERROR, "Passwords do not match.").showAndWait();
                return;
            }

            if (helpSystem.getUser(username) != null) {
                new Alert(Alert.AlertType.ERROR, "Username already exists.").showAndWait();
                return;
            }

            byte[] passwordHash = authService.hashPassword(password);
            User newUser = new User(email, username, passwordHash, "FirstName", "LastName");
            roles.forEach(newUser::addRole);
            helpSystem.addUser(newUser);

            new Alert(Alert.AlertType.INFORMATION, "Account created successfully!").showAndWait();
            showLoginScreen();
        });

        root.getChildren().addAll(prompt, usernameField, passwordField, confirmPasswordField, emailField, createAccountButton);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showFinishAccountSetup(User user) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label prompt = new Label("Finish Setting Up Your Account");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        TextField preferredNameField = new TextField();
        preferredNameField.setPromptText("Preferred Name (optional)");

        Button completeSetupButton = new Button("Complete Setup");

        completeSetupButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String preferredName = preferredNameField.getText().trim();

            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Email, first name, and last name are required.").showAndWait();
                return;
            }

            user.completeProfile(email, firstName, middleName, lastName, preferredName);
            helpSystem.updateUser(user);

            new Alert(Alert.AlertType.INFORMATION, "Account setup complete!").showAndWait();
            showDashboard(user);
        });

        root.getChildren().addAll(prompt, emailField, firstNameField, middleNameField, lastNameField, preferredNameField, completeSetupButton);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showDashboard(User user) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Welcome, " + user.getPreferredName() + "!");

        Button manageUsersButton = new Button("Manage Users");
        Button inviteUserButton = new Button("Invite User");
        Button manageArticlesButton = new Button("Manage Help Articles");
        Button searchArticlesButton = new Button("Search Help Articles");
        Button resetPasswordButton = new Button("Reset Password");
        Button deleteAccountButton = new Button("Delete Account");
        Button logoutButton = new Button("Logout");

        // Initially hide admin-specific buttons
        manageUsersButton.setVisible(false);
        inviteUserButton.setVisible(false);
        manageArticlesButton.setVisible(false);
        resetPasswordButton.setVisible(false);
        deleteAccountButton.setVisible(false);

        // Show buttons based on user roles
        if (user.getRoles().contains(User.Role.ADMIN)) {
            manageUsersButton.setVisible(true);
            inviteUserButton.setVisible(true);
            manageArticlesButton.setVisible(true);
            resetPasswordButton.setVisible(true);
            deleteAccountButton.setVisible(true);
        } else if (user.getRoles().contains(User.Role.INSTRUCTOR)) {
            manageArticlesButton.setVisible(true);
        }

        // Assign actions to buttons
        manageUsersButton.setOnAction(e -> showManageUsers(user));
        inviteUserButton.setOnAction(e -> showInviteUserScreen(user));
        manageArticlesButton.setOnAction(e -> showManageArticles(user));
        searchArticlesButton.setOnAction(e -> showSearchArticles(user));
        resetPasswordButton.setOnAction(e -> showResetPasswordScreen(user));
        deleteAccountButton.setOnAction(e -> showDeleteAccountScreen(user));
        logoutButton.setOnAction(e -> {
            helpSystem.setCurrentUser(null);
            showLoginScreen();
        });

        root.getChildren().addAll(welcomeLabel, manageUsersButton, inviteUserButton, manageArticlesButton, searchArticlesButton, resetPasswordButton, deleteAccountButton, logoutButton);
        Scene scene = new Scene(root, 400, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void showManageUsers(User user) {
        if (!user.getRoles().contains(User.Role.ADMIN)) {
            new Alert(Alert.AlertType.ERROR, "Access denied. Admins only.").showAndWait();
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Manage Users");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Admin", "Instructor", "Student");
        roleComboBox.setValue("Student");

        Button addUserButton = new Button("Add User");
        Button backButton = new Button("Back");

        addUserButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();
            String email = emailField.getText().trim();
            String role = roleComboBox.getValue();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "All fields are required.").showAndWait();
                return;
            }

            if (!password.equals(confirmPassword)) {
                new Alert(Alert.AlertType.ERROR, "Passwords do not match.").showAndWait();
                return;
            }

            if (helpSystem.getUser(username) != null) {
                new Alert(Alert.AlertType.ERROR, "Username already exists.").showAndWait();
                return;
            }

            byte[] passwordHash = authService.hashPassword(password);
            User newUser = null;

            switch (role) {
                case "Admin":
                    newUser = new Admin(email, username, passwordHash, "Admin", "User");
                    break;
                case "Instructor":
                    newUser = new Instructor(email, username, passwordHash, "Instructor", "User");
                    break;
                case "Student":
                    newUser = new Student(email, username, passwordHash, "Student", "User");
                    break;
                default:
                    break;
            }

            if (newUser != null) {
                helpSystem.addUser(newUser);
                new Alert(Alert.AlertType.INFORMATION, "User added successfully!").showAndWait();
                usernameField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
                emailField.clear();
            }
        });

        backButton.setOnAction(e -> showDashboard(user));

        root.getChildren().addAll(title, usernameField, emailField, passwordField, confirmPasswordField, roleComboBox, addUserButton, backButton);
        Scene scene = new Scene(root, 400, 450);
        stage.setScene(scene);
        stage.show();
    }

    private void showInviteUserScreen(User user) {
        if (!user.getRoles().contains(User.Role.ADMIN)) {
            new Alert(Alert.AlertType.ERROR, "Access denied. Admins only.").showAndWait();
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label prompt = new Label("Invite New User");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Admin", "Instructor", "Student");
        roleComboBox.setValue("Student");

        Button generateInviteButton = new Button("Generate Invitation Code");
        Button backButton = new Button("Back");

        generateInviteButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String roleStr = roleComboBox.getValue();

            if (username.isEmpty() || roleStr.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Username and role are required.").showAndWait();
                return;
            }

            User.Role role;
            try {
                role = User.Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid role selected.").showAndWait();
                return;
            }

            String invitationCode = UUID.randomUUID().toString();
            Set<User.Role> roles = new HashSet<>();
            roles.add(role);
            helpSystem.inviteUser(invitationCode, roles);

            // Display the invitation code in an Alert dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invitation Code Generated");
            alert.setHeaderText(null);
            alert.setContentText("Invitation code generated for " + username + ":\n\n" + invitationCode);
            alert.showAndWait();

            usernameField.clear();
        });

        backButton.setOnAction(e -> showDashboard(user));

        root.getChildren().addAll(prompt, usernameField, roleComboBox, generateInviteButton, backButton);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }


    private void showResetPasswordScreen(User user) {
        if (!user.getRoles().contains(User.Role.ADMIN)) {
            new Alert(Alert.AlertType.ERROR, "Access denied. Admins only.").showAndWait();
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label prompt = new Label("Reset User Password");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        Button resetPasswordButton = new Button("Reset Password");
        Button backButton = new Button("Back");

        resetPasswordButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Username is required.").showAndWait();
                return;
            }

            User targetUser = helpSystem.getUser(username);
            if (targetUser != null) {
                // Generate temporary password
                String tempPassword = UUID.randomUUID().toString().substring(0, 8);
                helpSystem.resetPassword(username, tempPassword);
                new Alert(Alert.AlertType.INFORMATION, "Temporary password: " + tempPassword + "\nIt expires in 24 hours.").showAndWait();
                usernameField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "User not found.").showAndWait();
            }
        });

        backButton.setOnAction(e -> showDashboard(user));

        root.getChildren().addAll(prompt, usernameField, resetPasswordButton, backButton);
        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void showDeleteAccountScreen(User user) {
        if (!user.getRoles().contains(User.Role.ADMIN)) {
            new Alert(Alert.AlertType.ERROR, "Access denied. Admins only.").showAndWait();
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label prompt = new Label("Delete User Account");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        Button deleteButton = new Button("Delete Account");
        Button backButton = new Button("Back");

        deleteButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Username is required.").showAndWait();
                return;
            }

            User targetUser = helpSystem.getUser(username);
            if (targetUser != null) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the account for '" + username + "'?");
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        helpSystem.deleteUser(username);
                        new Alert(Alert.AlertType.INFORMATION, "User account deleted.").showAndWait();
                        usernameField.clear();
                    }
                });
            } else {
                new Alert(Alert.AlertType.ERROR, "User not found.").showAndWait();
            }
        });

        backButton.setOnAction(e -> showDashboard(user));

        root.getChildren().addAll(prompt, usernameField, deleteButton, backButton);
        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void showSearchArticles(User user) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Search Help Articles");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter keyword or title");

        Button searchButton = new Button("Search");
        Button viewArticleButton = new Button("View Selected Article");
        Button backButton = new Button("Back");

        ListView<String> resultsList = new ListView<>();
        resultsList.setPrefHeight(200);

        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please enter a keyword or title to search.").showAndWait();
                return;
            }

            List<HelpArticle> results = helpSystem.searchArticles(keyword);
            resultsList.getItems().clear();

            if (results.isEmpty()) {
                resultsList.getItems().add("No articles found.");
            } else {
                for (HelpArticle article : results) {
                    resultsList.getItems().add(article.getId() + ": " + article.getTitle());
                }
            }
        });

        viewArticleButton.setOnAction(e -> {
            String selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected == null || selected.equals("No articles found.")) {
                new Alert(Alert.AlertType.ERROR, "Please select a valid article to view.").showAndWait();
                return;
            }

            // Extract ID from the selected string
            String[] parts = selected.split(":");
            if (parts.length < 2) {
                new Alert(Alert.AlertType.ERROR, "Invalid selection.").showAndWait();
                return;
            }

            long id;
            try {
                id = Long.parseLong(parts[0].trim());
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid article ID.").showAndWait();
                return;
            }

            HelpArticle article = helpSystem.getArticleById(id);
            if (article != null) {
                showArticleDetails(article);
            } else {
                new Alert(Alert.AlertType.ERROR, "Article not found.").showAndWait();
            }
        });

        backButton.setOnAction(e -> showDashboard(user));

        root.getChildren().addAll(title, searchField, searchButton, resultsList, viewArticleButton, backButton);
        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    
    private void showResetPasswordRequestScreen() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label prompt = new Label("Reset Password");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmNewPasswordField = new PasswordField();
        confirmNewPasswordField.setPromptText("Confirm New Password");

        Button resetPasswordButton = new Button("Reset Password");
        Button backButton = new Button("Back");

        resetPasswordButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String confirmNewPassword = confirmNewPasswordField.getText().trim();

            if (username.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "All fields are required.").showAndWait();
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                new Alert(Alert.AlertType.ERROR, "Passwords do not match.").showAndWait();
                return;
            }

            User userToReset = helpSystem.getUser(username);
            if (userToReset != null) {
                byte[] newHashedPassword = authService.hashPassword(newPassword);
                userToReset.setPasswordHash(newHashedPassword);
                userToReset.setOneTimePassword(false); // Reset one-time password flag
                helpSystem.updateUser(userToReset); // Save the updated user

                new Alert(Alert.AlertType.INFORMATION, "Password reset successfully. Please login with your new password.").showAndWait();
                showLoginScreen();
            } else {
                new Alert(Alert.AlertType.ERROR, "User not found.").showAndWait();
            }
        });

        backButton.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(prompt, usernameField, newPasswordField, confirmNewPasswordField, resetPasswordButton, backButton);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    // Include the updated showManageArticles method

    private void showManageArticles(User user) {
        if (!(user.getRoles().contains(User.Role.ADMIN) || user.getRoles().contains(User.Role.INSTRUCTOR))) {
            new Alert(Alert.AlertType.ERROR, "Access denied. Admins and Instructors only.").showAndWait();
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Manage Help Articles");

        TextField idField = new TextField();
        idField.setPromptText("ID (for Edit/Delete)");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Keywords (separated by ';')");

        TextField groupsField = new TextField();
        groupsField.setPromptText("Groups (separated by ';')");

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Content");
        contentArea.setPrefRowCount(5);

        ComboBox<String> levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        levelComboBox.setValue("Intermediate");

        Button addArticleButton = new Button("Add Article");
        Button editArticleButton = new Button("Edit Article");
        Button deleteArticleButton = new Button("Delete Article");
        Button listArticlesButton = new Button("List Articles by Group");
        Button backupButton = new Button("Backup Articles");
        Button restoreButton = new Button("Restore Articles");
        Button viewArticleButton = new Button("View Article by ID");
        Button backButton = new Button("Back");

        addArticleButton.setOnAction(e -> {
            String articleTitle = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String keywordsText = keywordsField.getText().trim();
            String groupsText = groupsField.getText().trim();
            String content = contentArea.getText().trim();
            String level = levelComboBox.getValue();

            if (articleTitle.isEmpty() || description.isEmpty() || keywordsText.isEmpty() || content.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "All fields except ID and groups are required.").showAndWait();
                return;
            }

            List<String> keywords = Arrays.asList(keywordsText.split(";"));
            HelpArticle article = new HelpArticle(articleTitle, description, keywords, content, level);

            if (!groupsText.isEmpty()) {
                List<String> groups = Arrays.asList(groupsText.split(";"));
                article.setGroups(groups);
            }

            helpSystem.addArticle(article);

            new Alert(Alert.AlertType.INFORMATION, "Article added successfully! ID: " + article.getId()).showAndWait();
            idField.clear();
            titleField.clear();
            descriptionField.clear();
            keywordsField.clear();
            groupsField.clear();
            contentArea.clear();
            levelComboBox.setValue("Intermediate");
        });

        editArticleButton.setOnAction(e -> {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "ID is required to edit an article.").showAndWait();
                return;
            }
            long id;
            try {
                id = Long.parseLong(idText);
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid ID format.").showAndWait();
                return;
            }
            HelpArticle articleToEdit = helpSystem.getArticleById(id);
            if (articleToEdit != null) {
                // Populate fields with article data for editing
                titleField.setText(articleToEdit.getTitle());
                descriptionField.setText(articleToEdit.getDescription());
                keywordsField.setText(String.join(";", articleToEdit.getKeywords()));
                groupsField.setText(String.join(";", articleToEdit.getGroups()));
                contentArea.setText(articleToEdit.getContent());
                levelComboBox.setValue(articleToEdit.getLevel());

                // Update the article on save
                addArticleButton.setText("Save Changes");
                addArticleButton.setOnAction(event -> {
                    String articleTitle = titleField.getText().trim();
                    String descriptionText = descriptionField.getText().trim();
                    String keywordsTextNew = keywordsField.getText().trim();
                    String groupsTextNew = groupsField.getText().trim();
                    String contentText = contentArea.getText().trim();
                    String levelText = levelComboBox.getValue();

                    if (articleTitle.isEmpty() || descriptionText.isEmpty() || keywordsTextNew.isEmpty() || contentText.isEmpty()) {
                        new Alert(Alert.AlertType.ERROR, "All fields except ID and groups are required.").showAndWait();
                        return;
                    }

                    List<String> keywordsNew = Arrays.asList(keywordsTextNew.split(";"));
                    articleToEdit.setTitle(articleTitle);
                    articleToEdit.setDescription(descriptionText);
                    articleToEdit.setKeywords(keywordsNew);
                    articleToEdit.setContent(contentText);
                    articleToEdit.setLevel(levelText);

                    if (!groupsTextNew.isEmpty()) {
                        List<String> groupsNew = Arrays.asList(groupsTextNew.split(";"));
                        articleToEdit.setGroups(groupsNew);
                    }

                    helpSystem.updateArticle(articleToEdit);

                    new Alert(Alert.AlertType.INFORMATION, "Article updated successfully!").showAndWait();
                    idField.clear();
                    titleField.clear();
                    descriptionField.clear();
                    keywordsField.clear();
                    groupsField.clear();
                    contentArea.clear();
                    levelComboBox.setValue("Intermediate");
                    addArticleButton.setText("Add Article");
                    // Reset the addArticleButton's action to its original functionality
                    addArticleButton.setOnAction(addArticleButton.getOnAction());
                });
            } else {
                new Alert(Alert.AlertType.ERROR, "Article not found.").showAndWait();
            }
        });

        deleteArticleButton.setOnAction(e -> {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "ID is required to delete an article.").showAndWait();
                return;
            }
            long id;
            try {
                id = Long.parseLong(idText);
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid ID format.").showAndWait();
                return;
            }
            boolean success = helpSystem.deleteArticleById(id);
            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Article deleted successfully!").showAndWait();
                idField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Article not found.").showAndWait();
            }
        });

        listArticlesButton.setOnAction(e -> showListArticlesByGroup(user));

        backupButton.setOnAction(e -> showBackupArticlesScreen(user));

        restoreButton.setOnAction(e -> showRestoreArticlesScreen(user));

        viewArticleButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("View Article");
            dialog.setHeaderText("Enter the ID of the article you want to view:");
            dialog.setContentText("Article ID:");

            dialog.showAndWait().ifPresent(idStr -> {
                long id;
                try {
                    id = Long.parseLong(idStr.trim());
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid ID format.").showAndWait();
                    return;
                }

                HelpArticle article = helpSystem.getArticleById(id);
                if (article != null) {
                    showArticleDetails(article);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Article not found.").showAndWait();
                }
            });
        });

        backButton.setOnAction(e -> showDashboard(user));

        root.getChildren().addAll(title, idField, titleField, descriptionField, keywordsField, groupsField, contentArea,
                levelComboBox, addArticleButton, editArticleButton, deleteArticleButton, listArticlesButton,
                backupButton, restoreButton, viewArticleButton, backButton);
        Scene scene = new Scene(root, 500, 700);
        stage.setScene(scene);
        stage.show();
    }

    private void showListArticlesByGroup(User user) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("List Articles by Group");

        TextField groupField = new TextField();
        groupField.setPromptText("Enter group(s) separated by ';'");

        Button listButton = new Button("List Articles");
        Button viewArticleButton = new Button("View Selected Article");
        Button backButton = new Button("Back");

        ListView<String> resultsList = new ListView<>();
        resultsList.setPrefHeight(200);

        listButton.setOnAction(e -> {
            String groupsText = groupField.getText().trim();
            if (groupsText.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please enter at least one group.").showAndWait();
                return;
            }
            List<String> groups = Arrays.asList(groupsText.split(";"));
            List<HelpArticle> results = helpSystem.getArticlesByGroups(groups);
            resultsList.getItems().clear();

            if (results.isEmpty()) {
                resultsList.getItems().add("No articles found for the specified group(s).");
            } else {
                for (HelpArticle article : results) {
                    resultsList.getItems().add(article.getId() + ": " + article.getTitle());
                }
            }
        });

        viewArticleButton.setOnAction(e -> {
            String selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected == null || selected.equals("No articles found for the specified group(s).")) {
                new Alert(Alert.AlertType.ERROR, "Please select a valid article to view.").showAndWait();
                return;
            }

            // Extract ID from the selected string
            String[] parts = selected.split(":");
            if (parts.length < 2) {
                new Alert(Alert.AlertType.ERROR, "Invalid selection.").showAndWait();
                return;
            }

            long id;
            try {
                id = Long.parseLong(parts[0].trim());
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid article ID.").showAndWait();
                return;
            }

            HelpArticle article = helpSystem.getArticleById(id);
            if (article != null) {
                showArticleDetails(article);
            } else {
                new Alert(Alert.AlertType.ERROR, "Article not found.").showAndWait();
            }
        });

        backButton.setOnAction(e -> showManageArticles(user));

        root.getChildren().addAll(title, groupField, listButton, resultsList, viewArticleButton, backButton);
        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void showBackupArticlesScreen(User user) {
        if (!(user.getRoles().contains(User.Role.ADMIN) || user.getRoles().contains(User.Role.INSTRUCTOR))) {
            new Alert(Alert.AlertType.ERROR, "Access denied. Admins and Instructors only.").showAndWait();
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Backup Articles");

        TextField filenameField = new TextField();
        filenameField.setPromptText("Enter filename for backup");

        TextField groupsField = new TextField();
        groupsField.setPromptText("Enter group(s) to backup (optional, separated by ';')");

        Button backupButton = new Button("Backup");
        Button backButton = new Button("Back");

        backupButton.setOnAction(e -> {
            String filename = filenameField.getText().trim();
            String groupsText = groupsField.getText().trim();

            if (filename.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Filename is required.").showAndWait();
                return;
            }

            List<String> groups = null;
            if (!groupsText.isEmpty()) {
                groups = Arrays.asList(groupsText.split(";"));
            }

            helpSystem.backupArticles(filename, groups);

            new Alert(Alert.AlertType.INFORMATION, "Backup completed successfully!").showAndWait();
            filenameField.clear();
            groupsField.clear();
        });

        backButton.setOnAction(e -> showManageArticles(user));

        root.getChildren().addAll(title, filenameField, groupsField, backupButton, backButton);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void showRestoreArticlesScreen(User user) {
        if (!(user.getRoles().contains(User.Role.ADMIN) || user.getRoles().contains(User.Role.INSTRUCTOR))) {
            new Alert(Alert.AlertType.ERROR, "Access denied. Admins and Instructors only.").showAndWait();
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Restore Articles");

        TextField filenameField = new TextField();
        filenameField.setPromptText("Enter backup filename");

        CheckBox removeExistingCheckBox = new CheckBox("Remove existing articles before restore");
        removeExistingCheckBox.setSelected(false); // Default to merging

        Button restoreButton = new Button("Restore");
        Button backButton = new Button("Back");

        restoreButton.setOnAction(e -> {
            String filename = filenameField.getText().trim();
            if (filename.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Filename is required.").showAndWait();
                return;
            }

            boolean removeExisting = removeExistingCheckBox.isSelected();
            helpSystem.restoreArticles(filename, removeExisting);
            new Alert(Alert.AlertType.INFORMATION, "Restore completed successfully!").showAndWait();
            filenameField.clear();
            removeExistingCheckBox.setSelected(false);
        });

        backButton.setOnAction(e -> showManageArticles(user));

        root.getChildren().addAll(title, filenameField, removeExistingCheckBox, restoreButton, backButton);
        Scene scene = new Scene(root, 400, 350);
        stage.setScene(scene);
        stage.show();
    }

    private void showArticleDetails(HelpArticle article) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Title: " + article.getTitle());
        Label descriptionLabel = new Label("Description: " + article.getDescription());
        Label keywordsLabel = new Label("Keywords: " + String.join(", ", article.getKeywords()));
        Label groupsLabel = new Label("Groups: " + String.join(", ", article.getGroups()));
        Label levelLabel = new Label("Level: " + article.getLevel());

        TextArea contentArea = new TextArea(article.getContent());
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefHeight(300);

        Button backButton = new Button("Back");

        backButton.setOnAction(e -> showSearchArticles(helpSystem.getCurrentUser()));

        root.getChildren().addAll(titleLabel, descriptionLabel, keywordsLabel, groupsLabel, levelLabel, new Label("Content:"), contentArea, backButton);
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.show();
    }
}