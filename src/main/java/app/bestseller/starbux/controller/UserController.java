package app.bestseller.starbux.controller;


import app.bestseller.starbux.controller.validator.PaginationValidator;
import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.exception.BadRequestException;
import app.bestseller.starbux.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

/**
 * Created by Ebrahim Kh.
 */

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/user", produces = "application/json")
public class UserController {
    private final UserService userService;

    // ==============================================
    //                     CLIENT
    // ==============================================
    @GetMapping("/{id}/")
    public ResponseEntity<UserReply> getUser(@PathVariable("id") Long user) {
        return ResponseEntity.of(userService.loadUser(user)
            .map(reply ->
                new UserReply(reply.getId(),
                    reply.getUsername(),
                    reply.getName(),
                    reply.getFamily(),
                    reply.getEmail(),
                    reply.getCreated(),
                    reply.getChanged(),
                    reply.getStatus().name(),
                    reply.getAuthorities())
            ));
    }

    // ==============================================
    //                     ADMIN
    // ==============================================
    @PostMapping("/admin/create/")
    @ResponseStatus(HttpStatus.OK)
    public void createUserByAdmin(@Valid @RequestBody CreateRequest request) throws BadRequestException {
        userService.createUser(request.getUsername(),
            request.getAuthorities(),
            request.getEmail(),
            request.getName(),
            request.getFamily(),
            Boolean.TRUE);
    }

    @GetMapping("/admin/all/")
    public ResponseEntity<Page<UserReply>> getAllUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "20") int size)
        throws BadRequestException {
        var users = userService.loadUsers(PaginationValidator.validatePaginationOrThrow(page, size));
        var reply = users.map(user ->
            new UserReply(user.getId(),
                user.getUsername(),
                user.getName(),
                user.getFamily(),
                user.getEmail(),
                user.getCreated(),
                user.getChanged(),
                user.getStatus().name(),
                user.getAuthorities()
            ));
        return ResponseEntity.ok(reply);
    }

    @PutMapping("/admin/{id}/edit/")
    @ResponseStatus(HttpStatus.OK)
    public void editUser(@PathVariable("id") Long user, @Valid @RequestBody CreateRequest request) {
        userService.editUser(
            user,
            request.getUsername(),
            request.getEmail(),
            request.getName(),
            request.getFamily(),
            request.getStatus(),
            request.getAuthorities(),
            Boolean.TRUE
        );
    }

    @PutMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("id") Long user) {
        userService.safeDeleteUser(user);
    }

    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRealUser(@PathVariable("id") Long user) {
        userService.deleteUser(user);
    }

    @PutMapping("/admin/{id}/ban/")
    @ResponseStatus(HttpStatus.OK)
    public void banUser(@PathVariable("id") Long user) {
        userService.banUser(user);
    }

    @PutMapping("/admin/{id}/frozen/")
    @ResponseStatus(HttpStatus.OK)
    public void frozenUser(@PathVariable("id") Long user) {
        userService.frozenUser(user);
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class RegisterRequest {
        @NotNull
        @Size(min = 5, max = 50, message = "username must be lower that 50 character.")
        @NotBlank
        private String username;
        @NotNull
        @Size(max = 50, message = "name must be lower that 50 character.")
        @NotBlank
        private String name;
        @NotNull
        @Size(max = 50, message = "family must be lower that 50 character.")
        @NotBlank
        private String family;
        @NotNull
        @Size(max = 50, message = "email must be lower that 50 character.")
        @Email
        private String email;
        @Pattern(regexp = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
        private String ipAddress;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    public static class UserReply {
        private Long id;
        private String username;
        private String name;
        private String family;
        private String email;
        private Date created;
        private Date changed;
        private String status;
        private Set<UserEntity.Authority> authorities;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class CreateRequest {
        private String username;
        private String name;
        private String family;
        private String email;
        private String status;
        private Set<UserEntity.Authority> authorities;
    }
}
