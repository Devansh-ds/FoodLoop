package com.devansh.integration;

import com.devansh.repo.UserRepository;
import com.devansh.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.LoadingCache;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoadingCache oneTimePasswordCache;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Register user")
    public void registerUser() throws Exception {

        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("u1@gmail.com");
        request.setPassword("u1");
        request.setFullname("u1");

        // when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        response
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.instanceOf(String.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("Register user (Exception) ")
    public void registerFailure() throws Exception {

        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("u1@gmail.com");
        request.setPassword("u1");
        request.setFullname("u1");

        // when
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @DisplayName("Simple auth")
    public void authenticateUser() throws Exception {
        // register user
        RegisterRequest request = new RegisterRequest();
        request.setEmail("u1@gmail.com");
        request.setPassword("u1");
        request.setFullname("u1");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // authenticate user
        // given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("u1@gmail.com");
        authenticationRequest.setPassword("u1");

        // when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)));

        // then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.instanceOf(String.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("Simple auth Ex: Email/password failure")
    public void authenticateUserException() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("u1@gmail.com");
        request.setPassword("u1");

        // when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("Reset password")
    public void resetPassword() throws Exception {
        // register user
        RegisterRequest request = new RegisterRequest();
        request.setEmail("u1@gmail.com");
        request.setPassword("u1");
        request.setFullname("u1");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(MockMvcResultMatchers.status().isOk());

        // reset password
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(request.getEmail());

        // Trigger reset password (sends otp)
        mockMvc.perform(MockMvcRequestBuilders
                .put("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Extract otp from cache
        @SuppressWarnings("unchecked")
        Map<String, Object> cachedData =(Map<String, Object>) oneTimePasswordCache.get(request.getEmail());
        Integer otp = (Integer) cachedData.get("otp");

        // submit otp and new password to verify output
        OtpVerificationRequest otpVerificationRequest = OtpVerificationRequest.builder()
                .emailId(request.getEmail())
                .context(OtpContext.RESET_PASSWORD)
                .oneTimePassword(otp)
                .newPassword("newpass")
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Trying to authenticate using new password
        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setEmail("u1@gmail.com");
        loginRequest.setPassword("newpass");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.instanceOf(String.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("Enable 2F auth")
    public void enable2FAuth() throws Exception {
        // register user
        RegisterRequest request = new RegisterRequest();
        request.setEmail("u1@gmail.com");
        request.setPassword("u1");
        request.setFullname("u1");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // enable 2f auth (sends otp)
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("u1@gmail.com");
        authenticationRequest.setPassword("u1");

        mockMvc.perform(MockMvcRequestBuilders
                .put("/auth/enable-double-auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // get otp generated for that request
        @SuppressWarnings("unchecked")
        Map<String, Object> cachedData =(Map<String, Object>) oneTimePasswordCache.get(request.getEmail());
        Integer otp = (Integer) cachedData.get("otp");

        // send otp to verify and enable 2f auth
        OtpVerificationRequest otpVerifyRequest = OtpVerificationRequest.builder()
                .emailId(request.getEmail())
                .context(OtpContext.ENABLE_TWO_FACT_AUTH)
                .oneTimePassword(otp)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpVerifyRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // check if 2f enabled or not by authenticating

        // send auth request (which sends otp at email)
        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setEmail("u1@gmail.com");
        loginRequest.setPassword("u1");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // get otp generated for that request
        @SuppressWarnings("unchecked")
        Map<String, Object> cachedDataAuth =(Map<String, Object>) oneTimePasswordCache.get(request.getEmail());
        Integer otp2 = (Integer) cachedDataAuth.get("otp");

        // send otp to verify and get authTokens
        OtpVerificationRequest otpVerifyRequestAuth = OtpVerificationRequest.builder()
                .emailId(request.getEmail())
                .context(OtpContext.LOGIN)
                .oneTimePassword(otp2)
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpVerifyRequestAuth)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.instanceOf(String.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());
    }
}