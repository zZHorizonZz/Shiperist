package dev.shiperist.user.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@Entity
@Table(name = "user")
public class UserEntity extends PanacheEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "email_verified")
    private LocalDateTime emailVerified;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "user")
    private Set<AccountEntity> accounts;

    @OneToMany(mappedBy = "user")
    private Set<SessionEntity> sessions;

    public UserEntity() {
    }
}
