package app.bestseller.starbux.controller;

import app.bestseller.starbux.domain.UserEntity;
import app.bestseller.starbux.repository.UserRepository;
import app.bestseller.starbux.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Ebrahim Kh.
 */


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper
        objectMapper = new ObjectMapper();

//    @Autowired
//    private UserController userController;
//    @Autowired
//    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
            .alwaysDo(print())
            .build();
    }

    @Test
    @Transactional
    void testGetUser_whenValidInput_thenReturnsAndExpectResponse() throws Exception {
        var userEntity = buildUserEntityFirst();
        var save = userRepository.save(userEntity);

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/user/" + save.getId() + "/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(save.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(save.getUsername()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(save.getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.family").value(save.getFamily()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(save.getEmail()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(save.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.authorities")
                .value(save.getAuthorities().stream().map(Enum::name).findFirst().get()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.authorities").isArray());
    }


    //    @WithMockUser(username = "username", roles = {"USER", "ADMIN"})
    @Test
    @Transactional
    void testCreateUser_whenValidInput_thenReturns201() throws Exception {
        var createRequest = new UserController.CreateRequest();
        ReflectionTestUtils.setField(createRequest, "username", "username");
        ReflectionTestUtils.setField(createRequest, "name", "name");
        ReflectionTestUtils.setField(createRequest, "family", "family");
        ReflectionTestUtils.setField(createRequest, "email", "test@email.com");
        ReflectionTestUtils.setField(createRequest, "authorities",
            Set.of(UserEntity.Authority.ADMIN.name()));

        mockMvc.perform(MockMvcRequestBuilders
            .post("/v1/user/admin/create/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }


    @Test
    @Transactional
    void testGetAllUsers_whenValidInput_thenReturnsAndExpectedResponses() throws Exception {
        var saveFirst = userRepository.save(buildUserEntityFirst());
        var saveSecond = userRepository.save(buildUserEntitySecond());

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/user/admin/all/")
            .param("size", "20")
            .param("page", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(20))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(2))

            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]id").value(saveFirst.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]username").value(saveFirst.getUsername()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]name").value(saveFirst.getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]family").value(saveFirst.getFamily()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]email").value(saveFirst.getEmail()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]status").value(saveFirst.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]authorities")
                .value(saveFirst.getAuthorities().stream().map(Enum::name).findFirst().get()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]authorities").isArray())

            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]id").value(saveSecond.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]username").value(saveSecond.getUsername()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]name").value(saveSecond.getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]family").value(saveSecond.getFamily()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]email").value(saveSecond.getEmail()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]created").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]changed").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]status").value(saveSecond.getStatus().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]authorities")
                .value(saveSecond.getAuthorities().stream().map(Enum::name).findFirst().get()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.[1]authorities").isArray());
    }


    @Test
    @Transactional
    void testPutEditUser_whenValidInput_thenReturns() throws Exception {
        var save = userRepository.save(buildUserEntityFirst());

        var editRequest = new UserController.CreateRequest();
        ReflectionTestUtils.setField(editRequest, "username", "username");
        ReflectionTestUtils.setField(editRequest, "name", "name");
        ReflectionTestUtils.setField(editRequest, "family", "family");
        ReflectionTestUtils.setField(editRequest, "email", "test@email.com");

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/user/admin/" + save.getId() + "/edit/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testPutSafeDeleteUser_whenValidInput_thenReturns() throws Exception {
        var save = userRepository.save(buildUserEntityFirst());

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/user/admin/" + save.getId() + "/delete/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testDeleteUser_whenValidInput_thenReturns() throws Exception {
        var save = userRepository.save(buildUserEntityFirst());

        mockMvc.perform(MockMvcRequestBuilders
            .delete("/v1/user/admin/" + save.getId() + "/delete/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testBanUser_whenValidInput_thenReturns() throws Exception {
        var save = userRepository.save(buildUserEntityFirst());

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/user/admin/" + save.getId() + "/ban/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testFrozenUser_whenValidInput_thenReturns() throws Exception {
        var save = userRepository.save(buildUserEntityFirst());

        mockMvc.perform(MockMvcRequestBuilders
            .put("/v1/user/admin/" + save.getId() + "/frozen/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }


    private UserEntity buildUserEntityFirst() {
        var user = new UserEntity();
        user.setUsername("username_first");
        user.setName("first_name_first");
        user.setFamily("last_family_first");
        user.setAuthorities(Set.of(UserEntity.Authority.USER));
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setEmail("email_first@email.com");
        return user;
    }

    private UserEntity buildUserEntitySecond() {
        var user = new UserEntity();
        user.setUsername("username_second");
        user.setName("first_name_second");
        user.setFamily("last_family_second");
        user.setAuthorities(Set.of(UserEntity.Authority.ADMIN));
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setEmail("email_second@email.com");
        return user;
    }

}
