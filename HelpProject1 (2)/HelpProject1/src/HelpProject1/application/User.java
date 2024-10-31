package HelpProject1.application;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String username;
    private byte[] passwordHash;
    private boolean isOneTimePassword;
    private LocalDateTime otpExpiry;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private Map<String, String> topicProficiencyLevels;
    private Set<Role> roles;
    private boolean isFirstLogin;
    private String oneTimeInvitationCode;

    public User(String username, byte[] passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = new HashSet<>();
        this.isFirstLogin = true;
        this.topicProficiencyLevels = new HashMap<>();
    }

    public User(String email, String username, byte[] passwordHash, String firstName, String lastName) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = "";
        this.preferredName = "";
        this.topicProficiencyLevels = new HashMap<>();
        this.roles = new HashSet<>();
        this.isFirstLogin = true;
    }

    public void completeProfile(String email, String firstName, String middleName, String lastName, String preferredName) {
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.preferredName = preferredName.isEmpty() ? firstName : preferredName;
        this.isFirstLogin = false;
    }

    // Getter and Setter methods
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isOneTimePassword() {
        return isOneTimePassword;
    }

    public void setOneTimePassword(boolean isOneTimePassword) {
        this.isOneTimePassword = isOneTimePassword;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public String getOneTimeInvitationCode() {
        return oneTimeInvitationCode;
    }

    public void setOneTimeInvitationCode(String oneTimeInvitationCode) {
        this.oneTimeInvitationCode = oneTimeInvitationCode;
    }

    public String getPreferredName() {
        return preferredName.isEmpty() ? firstName : preferredName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public void setProficiencyLevelForTopic(String topic, String level) {
        topicProficiencyLevels.put(topic, level);
    }

    public String getProficiencyLevelForTopic(String topic) {
        return topicProficiencyLevels.getOrDefault(topic, "Intermediate");
    }

    public enum Role {
        ADMIN,
        INSTRUCTOR,
        STUDENT
    }
}
