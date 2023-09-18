package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_successful() throws Exception {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        when(bCryptPasswordEncoder.encode(r.getPassword())).thenReturn("thisIsHashed");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void create_user_pass_less_than_7char() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("test");
        r.setConfirmPassword("test");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull(u);
    }

    @Test
    public void create_user_pass_and_confirmPass_does_not_match() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("test");
        r.setConfirmPassword("testaaa");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull(u);
    }

    @Test
    public void find_user_by_id_successful() {
        long id = 1;
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setUsername("test");
        expectedUser.setPassword("hashedPassword");
        Optional<User> mockUser = Optional.of(expectedUser);

        when(userRepository.findById(id)).thenReturn(mockUser);

        final ResponseEntity<User> response = userController.findById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(expectedUser.getId(), u.getId());
        assertEquals(expectedUser.getUsername(), u.getUsername());
        assertEquals(expectedUser.getPassword(), u.getPassword());
    }

    @Test
    public void find_user_by_id_not_found() {
        long id = 1;
        Optional<User> mockUser = Optional.empty();

        when(userRepository.findById(id)).thenReturn(mockUser);

        final ResponseEntity<User> response = userController.findById(id);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_by_name_successfull() {
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setUsername("test");
        expectedUser.setPassword("hashedPassword");

        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);

        final ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(expectedUser.getId(), u.getId());
        assertEquals(expectedUser.getUsername(), u.getUsername());
        assertEquals(expectedUser.getPassword(), u.getPassword());
    }

    @Test
    public void find_by_name_not_found() {
        when(userRepository.findByUsername("test")).thenReturn(null);

        final ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
