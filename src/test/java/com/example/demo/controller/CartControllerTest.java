package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    private CartRepository cartRepository = Mockito.mock(CartRepository.class);

    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart_successful() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("test");
        m.setItemId(1L);
        m.setQuantity(1);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test");
        mockUser.setPassword("hashedPassword");
        mockUser.setCart(new Cart());

        Item mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("test1");
        mockItem.setPrice(new BigDecimal(200));

        Mockito.when(userRepository.findByUsername(m.getUsername())).thenReturn(mockUser);
        Mockito.when(itemRepository.findById(m.getItemId())).thenReturn(Optional.of(mockItem));

        final ResponseEntity<Cart> response = cartController.addTocart(m);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertEquals(1, cart.getItems().size());
        assertEquals(true, cart.getItems().get(0).getId().equals(new Long(1)));
    }

    @Test
    public void add_to_cart_user_not_found() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("test");
        m.setItemId(1L);
        m.setQuantity(1);

        Mockito.when(userRepository.findByUsername(m.getUsername())).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(m);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void add_to_cart_item_not_found() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("test");
        m.setItemId(1L);
        m.setQuantity(1);

        Mockito.when(itemRepository.findById(m.getItemId())).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.addTocart(m);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_successful() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("test");
        m.setItemId(1L);
        m.setQuantity(1);

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
        mockCart.setTotal(new BigDecimal(200));
        mockUser.setCart(mockCart);

        Mockito.when(userRepository.findByUsername(m.getUsername())).thenReturn(mockUser);
        Mockito.when(itemRepository.findById(m.getItemId())).thenReturn(Optional.of(mockItem));

        final ResponseEntity<Cart> response = cartController.removeFromcart(m);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertEquals(0, cart.getItems().size());
        assertEquals(true, cart.getTotal().equals(new BigDecimal(0)));
        assertEquals("test", cart.getUser().getUsername());
    }

    @Test
    public void remove_from_cart_user_not_found() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("test");
        m.setItemId(1L);
        m.setQuantity(1);

        Mockito.when(userRepository.findByUsername(m.getUsername())).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.removeFromcart(m);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_item_not_found() {
        ModifyCartRequest m = new ModifyCartRequest();
        m.setUsername("test");
        m.setItemId(1L);
        m.setQuantity(1);

        Mockito.when(itemRepository.findById(m.getItemId())).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.removeFromcart(m);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
