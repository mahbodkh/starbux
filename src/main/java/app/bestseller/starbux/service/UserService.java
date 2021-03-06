package app.bestseller.starbux.service;


import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;


/**
 * Created by Ebrahim Kh.
 */

@Slf4j
@Service
@CacheConfig(cacheNames = "user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    @Transactional
    @Caching(
        cacheable = {
            @Cacheable(key = "'userByUsername/' + #username"),
            @Cacheable(key = "'userByEmail/' + #email")
        }
    )
    public UserEntity createUser(String username, Set<UserEntity.Authority> authorities,
                                 String email, String name, String family, Boolean isAdmin) throws BadRequestException {
        var oldUser = userRepository.findByUsername(username);
        if (oldUser.isPresent()) throw new BadRequestException("Your username created before, you can try to login");
        var user = UserEntity.builder()
            .username(username)
            .name(name)
            .family(family)
            .email(email);
        if (isAdmin) {
            user.status(UserEntity.Status.ACTIVE);
            user.authorities(authorities);
        } else {
            user.status(UserEntity.Status.PENDING);
            user.authorities(Set.of(UserEntity.Authority.USER));
        }
        var save = userRepository.save(user.build());
        log.debug("The user has saved: {}", save);
        return save;
    }


    @Caching(evict = {
        @CacheEvict(key = "'userById/' + #user.toString()"),
        @CacheEvict(key = "'userByUsername/' + #username"),
        @CacheEvict(key = "'userByEmail/' + #email")
    })
    @Transactional
    public Optional<UserEntity> editUser(Long user, String username, String email, String name, String family,
                                         UserEntity.Status status, Set<UserEntity.Authority> authorities, Boolean isAdmin) {
        return Optional.of(userRepository.findById(user))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(reply -> {
                if (username != null && !username.isEmpty() && !username.isBlank())
                    reply.setUsername(username);
                if (email != null && !email.isEmpty() && !email.isBlank())
                    reply.setEmail(email.toLowerCase());
                if (name != null && !name.isEmpty() && !name.isBlank())
                    reply.setName(name);
                if (family != null && !family.isEmpty() && !family.isBlank())
                    reply.setFamily(family);
                if (status != null && isAdmin)
                    reply.setStatus(status);
                if (authorities != null && !authorities.isEmpty() && isAdmin)
                    reply.setAuthorities(authorities);
                var save = userRepository.save(reply);
                log.debug("The user has been edited: {}", save);
                return save;
            });
    }


    @Cacheable(key = "'userById/' + #user.toString()")
    @Transactional(readOnly = true)
    public UserEntity loadUser(Long user) {
        return Optional.of(getOptionalUserEntity(user))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException("User (" + user + ") not found."));
    }

    @Cacheable(key = "'userByUsername/' + #username")
    @Transactional(readOnly = true)
    public UserEntity loadByUsername(String username) {
        return Optional.of(userRepository.findByUsername(username))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException("Username (" + username + ") not found."));
    }

    private Optional<UserEntity> getOptionalUserEntity(Long user) {
        return userRepository.findById(user);
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> loadUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /* danger it must only by admin invoke */
    @CacheEvict(key = "'userById/' + #user.toString()")
    @Transactional
    public void deleteUser(Long user) {
        userRepository
            .findById(user)
            .ifPresent(
                entity -> {
                    userRepository.delete(entity);
                    log.debug("Deleted User: {}", entity);
                    clearCaches(
                        "'userByUsername/'" + entity.getUsername(), "'userByEmail/'" + entity.getEmail()
                    );
                }
            );
    }

    @CacheEvict(key = "'userById/' + #user.toString()")
    @Transactional
    public void safeDeleteUser(Long user) {
        userRepository
            .findById(user)
            .ifPresent(
                entity -> {
                    entity.setStatus(UserEntity.Status.DELETED);
                    userRepository.save(entity);
                    log.debug("Safe deleted User: {}", entity);
                    clearCaches(
                        "'userByUsername/'" + entity.getUsername(), "'userByEmail/'" + entity.getEmail()
                    );
                }
            );
    }

    @CacheEvict(key = "'userById/' + #user.toString()")
    @Transactional
    public void banUser(Long user) {
        userRepository
            .findById(user)
            .ifPresent(
                entity -> {
                    entity.setStatus(UserEntity.Status.BANNED);
                    userRepository.save(entity);
                    log.debug("Banned User: {}", entity);
                    clearCaches(
                        "'userByUsername/'" + entity.getUsername(), "'userByEmail/'" + entity.getEmail()
                    );
                }
            );
    }

    @CacheEvict(key = "'userById/' + #user.toString()")
    @Transactional
    public void frozenUser(Long user) {
        userRepository
            .findById(user)
            .ifPresent(
                entity -> {
                    entity.setStatus(UserEntity.Status.FROZEN);
                    userRepository.save(entity);
                    log.debug("Frozen User: {}", entity);
                    clearCaches(
                        "'userByUsername/'" + entity.getUsername(), "'userByEmail/'" + entity.getEmail()
                    );
                }
            );
    }

    private void clearCaches(String... values) {
        for (var value : values) {
            Objects.requireNonNull(cacheManager.getCache(value)).clear();
        }
    }

    private final UserRepository userRepository;
    private final CacheManager cacheManager;
}
