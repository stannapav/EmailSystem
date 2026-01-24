package com.stannapav.emailsystem.controllers;

import com.stannapav.emailsystem.db.entities.User;
import com.stannapav.emailsystem.db.enums.LogType;
import com.stannapav.emailsystem.db.services.MailService;
import com.stannapav.emailsystem.db.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MailController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class MailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private UserService userService;

    @Test
    public void MailController_SendMailToUser_ReturnAccepted() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("name");
        user.setEmail("name@gmail.com");

        when(userService.getUserById(any(Integer.class))).thenReturn(user);
        doNothing().when(mailService).sendUserMailAsync(any(User.class), any(LogType.class));

        ResultActions response = mockMvc.perform(post("/api/mails/send/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isAccepted());
    }
}
