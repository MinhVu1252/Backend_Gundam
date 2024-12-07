package com.joyboy.userservice.domain.entities.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "first_name", length = 35)
    private String firstName;

    @Column(name = "last_name", length = 35)
    private String lastName;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "email", length = 150, unique = true)
    private String email;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "profile_image", length = 250)
    private String profileImage;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "facebook_account_id")
    @JsonIgnore
    private int facebookAccountId;

    @Column(name = "google_account_id")
    @JsonIgnore
    private int googleAccountId;

    @JsonIgnore
    private String otp;

    @JsonIgnore
    private LocalDateTime expiryOtp;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    private Set<Role> roles;

    @JsonIgnore
    private LocalDateTime create_at;

    @JsonIgnore
    private LocalDateTime update_at;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getNameRole()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        if (username != null && !username.isEmpty()) {
            return username;
        } else if (email != null && !email.isEmpty()) {
            return email;
        }
        return "";
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
