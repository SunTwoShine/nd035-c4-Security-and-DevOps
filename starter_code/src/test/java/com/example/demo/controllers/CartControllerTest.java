package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private ItemRepository itemRepository = mock(ItemRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
    }

    @Test
    public void test_addToCart_HappyPath() {
        ModifyCartRequest request = new ModifyCartRequest();

        User user = new User("test");
        String username = user.getUsername();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        when(userRepository.findByUsername(username)).thenReturn(user);

        Item item1 = new Item(1L, "Flower", new BigDecimal(1.98), "Rose");
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        request.setUsername(user.getUsername());
        request.setItemId(item1.getId());
        request.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(request);
        Cart cartResponse = response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Flower", cartResponse.getItems().get(0).getName());
        assertEquals("test", cartResponse.getUser().getUsername());
    }

    @Test
    public void test_addTocart_ItemNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();

        User user = new User("test");
        String username = user.getUsername();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        when(userRepository.findByUsername(username)).thenReturn(user);

        Item item1 = new Item(1L, "Flower", new BigDecimal(1.98), "Rose");
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        request.setUsername(user.getUsername());
        request.setItemId(item1.getId()+1);
        request.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(request);
        Cart cartResponse = response.getBody();

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void test_addTocart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();

        User user = new User("test");
        String username = user.getUsername();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        when(userRepository.findByUsername(username)).thenReturn(user);

        Item item1 = new Item(1L, "Flower", new BigDecimal(1.98), "Rose");
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        request.setUsername("test_failure");
        request.setItemId(item1.getId());
        request.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(request);
        Cart cartResponse = response.getBody();

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void test_removeFromcart_HappyPath() {
        User user = new User("test");
        String username = user.getUsername();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        when(userRepository.findByUsername(username)).thenReturn(user);

        Item item1 = new Item(1L, "Flower", new BigDecimal(1.98), "Rose");
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        Item item2 = new Item(2L, "Bread", new BigDecimal(4.50), "Sunflower");
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item1.getId());
        request.setQuantity(2);
        final ResponseEntity<Cart> response = cartController.addTocart(request);

        ModifyCartRequest request2 = new ModifyCartRequest();
        request2.setUsername(user.getUsername());
        request2.setItemId(item2.getId());
        request2.setQuantity(1);
        final ResponseEntity<Cart> response2 = cartController.addTocart(request2);

        Cart cartResponse = response.getBody();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Flower", cartResponse.getItems().get(1).getName());
        assertEquals("test", cartResponse.getUser().getUsername());

        Cart cartResponse2 = response2.getBody();
        assertNotNull(response2);
        assertEquals(200, response2.getStatusCodeValue());
        assertEquals("Bread", cartResponse2.getItems().get(2).getName());
        assertEquals("test", cartResponse2.getUser().getUsername());

        final ResponseEntity<Cart> response_remove = cartController.removeFromcart(request2);

        Cart cartResponse_remove = response_remove.getBody();
        assertNotNull(response_remove);
        assertEquals(200, response_remove.getStatusCodeValue());
        assertFalse(cartResponse_remove.getItems().contains(item2));
        assertEquals("test", cartResponse_remove.getUser().getUsername());
    }

}
