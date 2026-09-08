package com.peersphere.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @Entity — tells JPA/Hibernate this class maps to a database table.
 * Without this, Spring has no idea this is a database object.
 *
 * @Table — specifies the actual table name in MySQL. If omitted,
 * Hibernate uses the class name ("User"), which can conflict with
 * MySQL's reserved word. So we explicitly name it "users".
 *
 * implements UserDetails — this is Spring Security's interface.
 * By implementing it, Spring Security can use our User object
 * directly for authentication checks. It requires us to provide
 * methods like getPassword(), getUsername(), isAccountNonExpired() etc.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    /**
     * @Id — marks this field as the primary key.
     * @GeneratedValue — MySQL auto-increments this value.
     * We never set the ID manually; the database handles it.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    /**
     * unique = true — no two users can have the same email.
     * This is enforced at the database level, not just in Java code.
     */
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // stored as BCrypt hash, never plain text

    private String department;

    private String semester;

    @Column(length = 500)
    private String bio;

    private String profilePicture;
    /**
     * @ElementCollection — tells JPA this is a collection of simple values
     * (Strings), not a collection of other entities.
     *
     * @CollectionTable — creates a separate table "user_skills" with a
     * foreign key back to the users table.
     *
     * @Column(name = "skill") — the column name in the new table.
     *
     * FetchType.EAGER — loads skills immediately when user is loaded.
     * For small collections like this, EAGER is fine.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // stores "ROLE_USER" in DB, not a number
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * @PrePersist — runs automatically BEFORE the entity is first saved.
     * We use this to set createdAt automatically. The developer never
     * needs to remember to set it manually.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * @PreUpdate — runs automatically BEFORE the entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── UserDetails methods (required by Spring Security) ──────────

    /**
     * Spring Security uses this as the "username" for authentication.
     * We use email as our unique identifier, not a username string.
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns what permissions/roles this user has.
     * "ROLE_USER" means a regular student.
     * "ROLE_ADMIN" would mean an administrator.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // These are all true for now — we'll add account locking later if needed
    @Override public boolean isAccountNonExpired()   { return true; }
    @Override public boolean isAccountNonLocked()    { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()              { return true; }
}