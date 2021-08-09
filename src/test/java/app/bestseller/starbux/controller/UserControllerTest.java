package app.bestseller.starbux.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Ebrahim Kh.
 */


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @InjectMocks
    private UserController.CreateRequest createRequest;
    private JacksonTester<UserController.CreateRequest> createRequestJacksonTester;

    @Test
    void whenValidInput_thenReturns200() throws Exception {
//        var createRequest = new UserController.CreateRequest();
        ReflectionTestUtils.setField(UserController.CreateRequest.class, "username", "username");
        ReflectionTestUtils.setField(UserController.CreateRequest.class, "name", "name");
        ReflectionTestUtils.setField(UserController.CreateRequest.class, "family", "family");
        ReflectionTestUtils.setField(UserController.CreateRequest.class, "email", "test@email.com");
        ReflectionTestUtils.setField(UserController.CreateRequest.class, "authorities", "['admin']");

        mockMvc.perform(post("/v1/user/admin/create/")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk());



//        assertEquals(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    void whenValidInput_getThenReturns200() throws Exception {
        mockMvc.perform(get("/v1/user/")
            .contentType("application/json"))
            .andExpect(status().isOk());
//        var actualResponseBody = mvcResult.getResponse().getContentAsString();
//        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
//            objectMapper.writeValueAsString(expectedResponseBody));


//        mockMvc.perform(post("/forums/{forumId}/register", 42L)
//            .contentType("application/json"))
//            .param("sendWelcomeMail", "true")
//            .content(objectMapper.writeValueAsString(user)))
//            .andExpect(status().isOk());
    }


}
