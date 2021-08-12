package app.bestseller.starbux.service;


import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.exception.NotFoundException;
import app.bestseller.starbux.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public Optional<UserEntity> editUser(Long user, String username, String email, String name, String family, Set<UserEntity.Authority> authorities,
                                         Boolean isAdmin) {
        return Optional.of(userRepository.findById(user))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(reply -> {
                if (!email.isEmpty() && !email.isBlank())
                    reply.setEmail(email.toLowerCase());
                if (!family.isEmpty() && !family.isBlank())
                    reply.setFamily(family);
                if (!name.isEmpty() && !name.isBlank())
                    reply.setName(name);
                if (!username.isEmpty() && !username.isBlank())
                    reply.setUsername(username);
                if (isAdmin && !authorities.isEmpty())
                    reply.setAuthorities(authorities);
                var save = userRepository.save(reply);
                log.debug("The user has updated: {}", save);
                return save;
            });
    }


    @Cacheable(key = "'userById/' + #user.toString()")
    @Transactional(readOnly = true)
    public Optional<UserEntity> loadUser(Long user) {
        return getOptionalUserEntity(user);
    }

    @Cacheable(key = "'userByUsername/' + #username")
    @Transactional(readOnly = true)
    public UserEntity loadByUsername(String username) {
        return Optional.of(userRepository.findByUsername(username))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException("Username (" + username + ") not found."));
    }

    @Transactional(readOnly = true)
    public List<UserEntity> loadUsersAdmin() {
        return userRepository.findAllByAuthoritiesIn(Set.of(UserEntity.Authority.ADMIN));
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
                }
            );
    }

    private final UserRepository userRepository;
}
