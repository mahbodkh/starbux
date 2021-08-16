package app.bestseller.starbux.service;

import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Ebrahim Kh.
 */

@SpringBootTest
public class UserServiceTest {
    private @Autowired
    UserService userService;
    private @Autowired
    UserRepository userRepository;


    @Test
    @Transactional
    public void testInsertUser() throws Exception {
        var user = buildUserEntity();
        var save =
            userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        var userExist = userRepository.existsById(save.getId());
        assertTrue(userExist);
        assertEquals(UserEntity.Status.PENDING, save.getStatus());
        assertEquals(user.getUsername(), save.getUsername());
        assertEquals(user.getEmail(), save.getEmail());
        assertEquals(user.getName(), save.getName());
        assertEquals(user.getFamily(), save.getFamily());
        assertTrue(save.getAuthorities().contains(UserEntity.Authority.USER));
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
    public void testLoadUserByUsername() throws Exception {
        var user = buildUserEntity();
        var save = userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        var userByUsername = userService.loadByUsername(user.getUsername());

        assertEquals(save.getId(), userByUsername.getId());
        assertEquals(save.getUsername(), userByUsername.getUsername());
    }

    @Test
    @Transactional
    public void testLoadUsers() throws Exception {
        var user = buildUserEntity();
        userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        var users = userService.loadUsers(Pageable.ofSize(20));

        assertEquals(users.getContent().size(), 1);
        assertEquals(users.getTotalElements(), 1);
    }


    @Test
    @Transactional
    public void testEditUser() throws Exception {
        var user = buildUserEntity();
        var save = userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        var edit = new UserEntity();
        edit.setUsername("username_edited");
        edit.setName("name_edited");
        edit.setFamily("family_edited");
        edit.setEmail("email_edited@test.com");
        edit.setStatus(UserEntity.Status.ACTIVE);
        edit.setAuthorities(Set.of(UserEntity.Authority.ADMIN));
        var editSave =
            userService.editUser(save.getId(), edit.getUsername(), edit.getEmail(), edit.getName(), edit.getFamily(), edit.getStatus(), edit.getAuthorities(), true).get();

        assertEquals(save.getId(), editSave.getId());
        assertEquals(edit.getUsername(), editSave.getUsername());
        assertEquals(edit.getName(), editSave.getName());
        assertEquals(edit.getFamily(), editSave.getFamily());
        assertEquals(edit.getEmail(), editSave.getEmail());
        assertEquals(edit.getStatus(), editSave.getStatus());
        assertEquals(edit.getAuthorities(), editSave.getAuthorities());
    }


    @Test
    @Transactional
    public void testFrozenUser() throws Exception {
        var user = buildUserEntity();
        var save = userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        userService.frozenUser(save.getId());

        var userFrozen = userService.loadUser(save.getId());
        assertEquals(userFrozen.getStatus(), UserEntity.Status.FROZEN);
    }


    @Test
    @Transactional
    public void testBanUser() throws Exception {
        var user = buildUserEntity();
        var save = userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        userService.banUser(save.getId());

        var userBan = userService.loadUser(save.getId());
        assertEquals(userBan.getStatus(), UserEntity.Status.BANNED);
    }


    @Test
    @Transactional
    public void testSafeDeleteUser() throws Exception {
        var user = buildUserEntity();
        var save = userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        userService.safeDeleteUser(save.getId());

        var userDelete = userRepository.getById(save.getId());
        assertEquals(userDelete.getStatus(), UserEntity.Status.DELETED);
    }


    @Test
    @Transactional
    public void testDeleteUser() throws Exception {
        var user = buildUserEntity();
        var save = userService.createUser(user.getUsername(), user.getAuthorities(), user.getEmail(), user.getName(), user.getFamily(), false);

        userService.deleteUser(save.getId());

        var userDeleted = userRepository.existsById(save.getId());
        assertFalse(userDeleted);
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
