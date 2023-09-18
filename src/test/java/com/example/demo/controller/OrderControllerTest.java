package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrderControllerTest {
    private OrderController orderController;

    private OrderRepository orderRepository = Mockito.mock(OrderRepository.class);

    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_successful() {
        Item mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("test1");
        mockItem.setPrice(new BigDecimal(200));

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test");
        mockUser.setPassword("hashedPassword");

        Cart mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setUser(mockUser);
        List<Item> mockItemsOfCart = new ArrayList<>();
        mockItemsOfCart.add(mockItem);
        mockCart.setItems(mockItemsOfCart);
        mockCart.setTotal(new BigDecimal(200));
        mockUser.setCart(mockCart);

        Mockito.when(userRepository.findByUsername("test")).thenReturn(mockUser);

        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertEquals("test", userOrder.getUser().getUsername());
        assertEquals(true, userOrder.getTotal().equals(new BigDecimal(200)));
        assertEquals(1, userOrder.getItems().size());
    }

    @Test
    public void submit_user_not_found() {
        Mockito.when(userRepository.findByUsername("test")).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser_successful() {
        Item mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("test1");
        mockItem.setPrice(new BigDecimal(200));

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test");
        mockUser.setPassword("hashedPassword");

        List<Item> itemsOfOrder = new ArrayList<>();
        itemsOfOrder.add(mockItem);

        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setUser(mockUser);
        userOrder.setItems(itemsOfOrder);
        userOrder.setTotal(new BigDecimal(200));

        List<UserOrder> ordersOfUser = new ArrayList<>();
        ordersOfUser.add(userOrder);

        Mockito.when(userRepository.findByUsername("test")).thenReturn(mockUser);
        Mockito.when(orderRepository.findByUser(mockUser)).thenReturn(ordersOfUser);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertEquals(1, response.getBody().size());
        assertEquals("test", response.getBody().get(0).getUser().getUsername());
    }

    @Test
    public void getOrdersForUser_user_not_found() {
        Mockito.when(userRepository.findByUsername("test")).thenReturn(null);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
