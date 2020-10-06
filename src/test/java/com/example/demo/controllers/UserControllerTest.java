package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private static final String USERNAME = "test";
    private static final String PASSWORD = "testPassword";
    private static final Long   USER_ID  = 0L;

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepo);
        TestUtils.injectObject(userController, "cartRepository", cartRepo);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user() throws Exception {
        when(encoder.encode(PASSWORD)).thenReturn("thisIsHashed");

        final ResponseEntity<User> response = userController.createUser(createMockUserRequest());

        // Test response status
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Test response body
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals(USERNAME, u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void find_user_by_valid_id() throws Exception {
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(createMockUser()));

        final ResponseEntity<User> response = userController.findById(USER_ID);

        // Test find user by id response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Test response body
        User testUser = response.getBody();
        assertEquals(USER_ID, Optional.of(testUser.getId()).get());
        assertEquals(USERNAME, testUser.getUsername());
    }

    @Test
    public void find_user_by_invalid_id() throws Exception {
        when(userRepo.findById(0L)).thenReturn(Optional.ofNullable(null));

        final ResponseEntity<User> response = userController.findById(0L);

        // Test find by username response
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_valid_name() throws Exception {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createMockUser());

        final ResponseEntity<User> response = userController.findByUserName(USERNAME);

        // Test find by username response
        User testUser = response.getBody();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Test response body
        assertEquals(USER_ID, Optional.of(testUser.getId()).get());
        assertEquals(USERNAME, response.getBody().getUsername());
    }

    @Test
    public void find_user_by_invalid_name() throws Exception {
        when(userRepo.findByUsername("not found")).thenReturn(null);

        final ResponseEntity<User> response = userController.findByUserName("not found");

        // Test find by username response
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private CreateUserRequest createMockUserRequest() {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername(USERNAME);
        req.setPassword(PASSWORD);
        req.setConfirmPassword(PASSWORD);

        return req;
    }

    private User createMockUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        return user;
    }
}
