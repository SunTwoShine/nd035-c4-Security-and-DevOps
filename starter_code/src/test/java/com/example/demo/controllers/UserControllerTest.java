package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void test_createUserHappyPath() throws Exception{
        when(encoder.encode("testpassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testpassword");
        request.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());

    }

    @Test
    public void test_createUserFailure() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testpassword");
        request.setConfirmPassword("testpass");

        final ResponseEntity<User> response = userController.createUser(request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void test_findByUserName() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testpassword");
        request.setConfirmPassword("testpassword");
        final ResponseEntity<User> responseSave = userController.createUser(request);

        assertNotNull(responseSave);
        assertEquals(200, responseSave.getStatusCodeValue());

        User user = responseSave.getBody();
        String username = user.getUsername();

        when(userRepository.findByUsername(username)).thenReturn(user);
        final ResponseEntity<User> responseFind = userController.findByUserName(username);

        assertNotNull(responseFind);
        assertEquals(200, responseFind.getStatusCodeValue());

        user = responseFind.getBody();
        assertEquals("test", user.getUsername());
    }

    @Test
    public void test_findById() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testpassword");
        request.setConfirmPassword("testpassword");
        final ResponseEntity<User> responseSave = userController.createUser(request);

        assertNotNull(responseSave);
        assertEquals(200, responseSave.getStatusCodeValue());

        User user = responseSave.getBody();
        Long userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        final ResponseEntity<User> responseFind = userController.findById(userId);

        assertNotNull(responseFind);
        assertEquals(200, responseFind.getStatusCodeValue());

        user = responseFind.getBody();
        assertEquals("test", user.getUsername());
    }
}
