package HelpProject1.application;

import java.util.*;	
import java.time.LocalDateTime;

public class HelpSystem {
    private Map<String, User> users;
    private List<HelpArticle> articles;
    private boolean hasAdmin = false;
    private Map<String, Set<User.Role>> invitationCodes = new HashMap<>();
    private User currentUser;

    public HelpSystem() {
        users = FileManager.loadUsers();
        articles = FileManager.loadArticles();

        // Check if any existing user is an admin
        for (User user : users.values()) {
            if (user.getRoles().contains(User.Role.ADMIN)) {
                hasAdmin = true;
                break;
            }
        }
        if (!hasAdmin) {
            createInitialAdmin();
        }

        // Update nextId in HelpArticle class to ensure unique IDs
        updateNextArticleId();
    }

    private void updateNextArticleId() {
        long maxId = 0;
        for (HelpArticle article : articles) {
            if (article.getId() > maxId) {
                maxId = article.getId();
            }
        }
        HelpArticle.setNextId(maxId + 1);
    }

    public void createInitialAdmin() {
        System.out.println("No admin found. The first user to register will be assigned as Admin.");
    }

    public void addUser(User user) {
        if (user.getRoles().contains(User.Role.ADMIN) && !hasAdmin) {
            hasAdmin = true;
        }
        users.put(user.getUsername(), user);
        FileManager.saveUsers(users);
    }

    public boolean hasAdmin() {
        return hasAdmin;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void updateUser(User user) {
        users.put(user.getUsername(), user);
        FileManager.saveUsers(users);
    }

    public void deleteUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            FileManager.saveUsers(users);
        }
    }

    public void inviteUser(String invitationCode, Set<User.Role> roles) {
        invitationCodes.put(invitationCode, roles);
        System.out.println("Invitation code generated: " + invitationCode + " for roles: " + roles.toString());
    }

    public boolean validateInvitationCode(String code) {
        return invitationCodes.containsKey(code);
    }

    public Set<User.Role> getRolesForInvitationCode(String code) {
        return invitationCodes.get(code);
    }

    public void resetPassword(String username, String tempPassword) {
        User user = users.get(username);
        if (user != null) {
            user.setOneTimePassword(true);
            user.setOtpExpiry(LocalDateTime.now().plusHours(24));
            FileManager.saveUsers(users); // Save the updated user
        }
    }

    // Article management methods
    public void addArticle(HelpArticle article) {
        // Assign a unique ID automatically
        article.setId(HelpArticle.getNextId());
        HelpArticle.incrementNextId();

        // Check for duplicate ID (redundant since IDs are auto-generated, but kept for safety)
        for (HelpArticle existingArticle : articles) {
            if (existingArticle.getId() == article.getId()) {
                System.out.println("Article ID already exists.");
                return;
            }
        }
        articles.add(article);
        FileManager.saveArticles(articles);
    }

    public HelpArticle getArticleById(long id) {
        for (HelpArticle article : articles) {
            if (article.getId() == id) {
                return article;
            }
        }
        return null;
    }

    public boolean deleteArticleById(long id) {
        Iterator<HelpArticle> iterator = articles.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
                FileManager.saveArticles(articles);
                return true;
            }
        }
        return false;
    }

    public void updateArticle(HelpArticle updatedArticle) {
        for (int i = 0; i < articles.size(); i++) {
            if (articles.get(i).getId() == updatedArticle.getId()) {
                articles.set(i, updatedArticle);
                FileManager.saveArticles(articles);
                break;
            }
        }
    }

    public List<HelpArticle> getArticles() {
        return articles;
    }

    public List<HelpArticle> getArticlesByGroups(List<String> groups) {
        List<HelpArticle> result = new ArrayList<>();
        for (HelpArticle article : articles) {
            for (String group : groups) {
                if (article.getGroups().contains(group.trim())) {
                    result.add(article);
                    break; // Avoid duplicates
                }
            }
        }
        return result;
    }

    // Backup and restore methods
    public void backupArticles(String filename, List<String> groups) {
        List<HelpArticle> backupList = new ArrayList<>();
        for (HelpArticle article : articles) {
            if (groups == null || groups.isEmpty() || article.getGroups().stream().anyMatch(groups::contains)) {
                backupList.add(article);
            }
        }
        FileManager.backupArticles(backupList, filename);
    }

    public void restoreArticles(String filename, boolean removeExisting) {
        List<HelpArticle> backupArticles = FileManager.loadArticlesFromBackup(filename);
        if (removeExisting) {
            articles.clear();
        }
        for (HelpArticle backupArticle : backupArticles) {
            boolean exists = false;
            for (HelpArticle currentArticle : articles) {
                if (currentArticle.getId() == backupArticle.getId()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                articles.add(backupArticle);
            }
        }
        FileManager.saveArticles(articles);
    }

    // *Added searchArticles method*
    public List<HelpArticle> searchArticles(String keyword) {
        List<HelpArticle> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (HelpArticle article : articles) {
            if (article.getTitle().toLowerCase().contains(lowerKeyword) ||
                article.getDescription().toLowerCase().contains(lowerKeyword) ||
                article.getContent().toLowerCase().contains(lowerKeyword) ||
                article.getKeywords().stream().anyMatch(k -> k.toLowerCase().contains(lowerKeyword))) {
                result.add(article);
            }
        }
        return result;
    }
}