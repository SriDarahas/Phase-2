package HelpProject1.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HelpArticle implements Serializable {
    private static final long serialVersionUID = 1L;
    private static long nextId = 1; // For unique ID generation

    private long id;
    private String title;
    private String description;
    private List<String> keywords;
    private String content;
    private String level;
    private List<String> groups;

    // Constructor without ID (for new articles)
    public HelpArticle(String title, String description, List<String> keywords, String content, String level) {
        //this.id = nextId++; // Assign a unique
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.content = content;
        this.level = level;
        this.groups = new ArrayList<>();
    }

    // Constructor with ID (for loading existing articles)
    public HelpArticle(long id, String title, String description, List<String> keywords, String content, String level) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.content = content;
        this.level = level;
        this.groups = new ArrayList<>();

        // Update nextId if necessary
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    // Getter methods
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public static long getNextId() {
        return nextId;
    }
    public static void setNextId(long nextId) {
        HelpArticle.nextId = nextId;
    }
    
    public static void incrementNextId() {
        nextId++;
    }

    public String getTitle() {
        return title;
    }
    
    

    public String getDescription() {
        return description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getContent() {
        return content;
    }

    public String getLevel() {
        return level;
    }

    public List<String> getGroups() {
        return groups;
    }

    // Setter methods
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    // Methods to add or remove groups
    public void addGroup(String group) {
        if (!groups.contains(group)) {
            groups.add(group);
        }
    }

    public void removeGroup(String group) {
        groups.remove(group);
    }
}