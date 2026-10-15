package com.inc.chatbot.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.VIEWER;

    @Column(name = "email", length = 255)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public enum UserRole {
        VIEWER, CONTRIBUTOR, APPROVER, ADMIN
    }

    // ---- Constructors ----
    public User() {}

    // ---- Getters ----
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getIsActive() { return isActive; }

    // ---- Setters ----
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(UserRole role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    // ---- Builder ----
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final User user = new User();

        public Builder username(String v)   { user.username = v; return this; }
        public Builder password(String v)   { user.password = v; return this; }
        public Builder role(UserRole v)     { user.role = v; return this; }
        public Builder email(String v)      { user.email = v; return this; }
        public Builder isActive(Boolean v)  { user.isActive = v; return this; }
        public User build()                 { return user; }
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role + "}";
    }
}
