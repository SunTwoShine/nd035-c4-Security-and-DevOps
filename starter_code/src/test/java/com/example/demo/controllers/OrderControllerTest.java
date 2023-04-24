package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void test_submitOrder() {

        Item item1 = new Item(1L, "Flower", new BigDecimal(1.98), "Rose");
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        User user = new User("test");
        String username = user.getUsername();
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(item1);
        user.setCart(cart);
        when(userRepository.findByUsername(username)).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();

        assertNotNull(userOrder);
        assertEquals("test", userOrder.getUser().getUsername());
        assertTrue(userOrder.getItems().contains(item1));
    }
}