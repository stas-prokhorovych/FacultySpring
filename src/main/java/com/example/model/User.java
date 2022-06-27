package com.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty(message = "login can't be empty")
    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    @NotEmpty(message = "password can't be empty")
    @Size(min = 3, max=64, message = "Password must be longer 3 characters")
    private String password;

    @NotEmpty(message = "Mail can't be empty")
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    private String role = "Student";

    @Column(name = "first_name", nullable = false)
    @NotEmpty(message = "First name can't be empty")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotEmpty(message = "Last name can't be empty")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "user_access", nullable = false)
    private boolean userAccess = true;

    @OneToMany(mappedBy = "id")
    private Set<Course> courses;

    @OneToMany(mappedBy = "student")
    private Set<CourseStudent> courseStudent = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userAccess;
    }
}
