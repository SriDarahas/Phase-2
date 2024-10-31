package HelpProject1.application;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String USER_DATA_PATH = DATA_DIR + "/users.dat";
    private static final String ARTICLE_DATA_PATH = DATA_DIR + "/articles.dat";

    static {
        // Ensure the data directory exists
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // Load users from users.dat
    public static Map<String, User> loadUsers() {
        Map<String, User> users = new HashMap<>();
        File file = new File(USER_DATA_PATH);
        if (!file.exists()) {
            return users; // Return empty map if file doesn't exist
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_DATA_PATH))) {
            users = (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Save users to users.dat
    public static void saveUsers(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load articles from articles.dat (for regular load)
    public static List<HelpArticle> loadArticles() {
        File file = new File(ARTICLE_DATA_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARTICLE_DATA_PATH))) {
            return (List<HelpArticle>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Save articles to articles.dat (for regular save)
    public static void saveArticles(List<HelpArticle> articles) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARTICLE_DATA_PATH))) {
            oos.writeObject(articles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Backup articles to specified file
    public static void backupArticles(List<HelpArticle> articles, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(articles);
            System.out.println("Articles backed up successfully to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load articles from backup file
    public static List<HelpArticle> loadArticlesFromBackup(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Backup file not found.");
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<HelpArticle>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
