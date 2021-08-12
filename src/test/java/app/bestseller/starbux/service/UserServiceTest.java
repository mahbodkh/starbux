package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Ebrahim Kh.
 */

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void testInsertUser() throws Exception {
        var user = buildUserEntity();
        var userPersisted =
            userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        var userExist = userRepository.existsById(userPersisted.getId());
        assertTrue(userExist);
        assertEquals(UserEntity.Status.PENDING, userPersisted.getStatus());
        assertEquals(user.getUsername(), userPersisted.getUsername());
        assertEquals(user.getEmail(), userPersisted.getEmail());
        assertEquals(user.getName(), userPersisted.getName());
        assertEquals(user.getFamily(), userPersisted.getFamily());
        assertTrue(userPersisted.getAuthorities().contains(UserEntity.Authority.USER));
    }


    @Test
    @Transactional
    public void testDuplicateUsernameError() {
        var user = buildUserEntity();
        try {
            for (int i = 0; i < 1; i++) {
                userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);
            }
        } catch (BadRequestException e) {
            Assert.hasText(e.getMessage(), "Your username before has created, you can try to login");
        }
    }

    @Test
    @Transactional
    public void testLoadUser() throws Exception {
        var user = buildUserEntity();

        var save = userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        var result = userRepository.getById(save.getId());
        assertEquals(save.getId(), result.getId());
        assertEquals(save.getName(), result.getName());
        assertEquals(save.getFamily(), result.getFamily());
        assertEquals(save.getUsername(), result.getUsername());
        assertEquals(save.getEmail(), result.getEmail());
    }

    @Test
    @Transactional
    public void testLoadUsers() throws Exception {
        var user = buildUserEntity();
        userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        Page<UserEntity> users = userService.loadUsers(Pageable.ofSize(20));

        assertEquals(users.getContent().size(), 1);
        assertEquals(users.getTotalElements(), 1);
    }


    @Test
    @Transactional
    public void testLoadUserAdmin() throws Exception {
        var user = buildUserEntity();
        user.setAuthorities(Set.of(UserEntity.Authority.ADMIN));
        userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        List<UserEntity> userAdmins = userService.loadUsersAdmin();
        assertEquals(userAdmins.size(), 1);
        assertEquals(user.getUsername(), "username");
        assertEquals(user.getName(), "first_name");
        assertEquals(user.getFamily(), "last_family");
        assertEquals(user.getEmail(), "email@email.com");
        Set<UserEntity.Authority> authorities = user.getAuthorities();
        assertEquals(authorities.isEmpty(), Boolean.FALSE);
        assertEquals(authorities.size(), 1);
        assertEquals(authorities.contains(UserEntity.Authority.ADMIN), Boolean.TRUE);
    }

    @Test
    @Transactional
    public void testEditUser() throws Exception {
        var user = buildUserEntity();
        userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        var edit = new UserEntity();
        edit.setUsername("username_edited");
        edit.setName("name_edited");
        edit.setFamily("family_edited");
        edit.setEmail("email_edited");
        edit.setAuthorities(Set.of(UserEntity.Authority.ADMIN));
        userService.editUser(user.getId(), edit.getUsername(), edit.getEmail(), edit.getName(), edit.getFamily(), edit.getAuthorities(), false);

        UserEntity userEntity = userService.loadUser(user.getId()).get();
        assertEquals(user.getId(), userEntity.getId());
        assertEquals(edit.getName(), userEntity.getName());
        assertEquals(edit.getFamily(), userEntity.getFamily());
        assertEquals(edit.getEmail(), userEntity.getEmail());
        assertEquals(edit.getAuthorities(), userEntity.getAuthorities());
    }

    @Test
    @Transactional
    public void testFrozenUser() throws Exception {
        var user = buildUserEntity();
        userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        userService.frozenUser(user.getId());

        UserEntity userEntity = userService.loadUser(user.getId()).get();
        assertEquals(userEntity.getStatus(), UserEntity.Status.FROZEN);
    }

    @Test
    @Transactional
    public void testBanUser() throws Exception {
        var user = buildUserEntity();
        userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        userService.banUser(user.getId());

        UserEntity userEntity = userService.loadUser(user.getId()).get();
        assertEquals(userEntity.getStatus(), UserEntity.Status.BANNED);
    }

    @Test
    @Transactional
    public void testSafeDeleteUser() throws Exception {
        var user = buildUserEntity();
        var save = userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        userService.safeDeleteUser(save.getId());

        UserEntity userEntity = userRepository.getById(save.getId());
        assertEquals(userEntity.getStatus(), UserEntity.Status.DELETED);
    }

    @Test
    @Transactional
    public void testDeleteUser() throws Exception {
        var user = buildUserEntity();
        userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        userService.deleteUser(user.getId());

        var userExist = userRepository.existsById(user.getId());
        assertEquals(userExist, Boolean.FALSE);
    }


    private UserEntity buildUserEntity() {
        var user = new UserEntity();
        user.setUsername("username");
        user.setName("first_name");
        user.setFamily("last_family");
        user.setAuthorities(Set.of(UserEntity.Authority.USER));
        user.setEmail("email@email.com");
        return user;
    }
}
