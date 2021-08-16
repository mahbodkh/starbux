package app.bestseller.starbux.domain;


import app.bestseller.starbux.domain.util.SetAuthorityConverter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Ebrahim Kh.
 */

@Data
@Table(name = "\"best_users\""
    , uniqueConstraints = @UniqueConstraint(columnNames = {"username", "email"}, name = "user_entity_constraint"))
@NoArgsConstructor
@Entity
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 5, max = 50)
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;
    @Size(max = 50)
    @Column(name = "family", length = 50)
    private String family;
    @Email
    @Size(min = 5, max = 254)
    @Column(name = "email", length = 254, unique = true)
    private String email;
    @Convert(converter = SetAuthorityConverter.class)
    @Column(name = "authorities")
    private Set<Authority> authorities = new HashSet<>();
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @UpdateTimestamp
    private Date changed;


    // The equal & hashcode manually is more safe
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }


    @Builder(toBuilder = true)
    public UserEntity(
        Long id, String username, String name,
        String family, String email,
        Date created,
        Date changed, Set<Authority> authorities, Status status
    ) {
        setId(id);
        setUsername(username.toLowerCase());
        setName(name);
        setFamily(family);
        setEmail(email);
        setCreated(created);
        setChanged(changed);
        setAuthorities(authorities);
        setStatus(status);
    }

    public enum Status {
        ACTIVE,
        PENDING,
        DEACTIVATE,
        FROZEN,
        DELETED,
        BANNED
    }

    public enum Authority {
        SYSTEM,
        ADMIN,
        USER
    }


    @Transient
    public static UserEntity getBasicUser(Set<Authority> authorities, String username, String name, String family) {
        return UserEntity.builder()
            .authorities(authorities)
            .name(name)
            .family(family)
            .username(username)
            .build();
    }

}
