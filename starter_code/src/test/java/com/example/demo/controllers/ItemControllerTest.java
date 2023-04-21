package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void test_getItems() {
        Item item1 = new Item(1L, "Flower", new BigDecimal(1.98), "Rose");
        Item item2 = new Item(2L, "Bread", new BigDecimal(4.50), "Sunflower");

        when(itemRepository.findAll()).thenReturn(Lists.list(item1, item2));

        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();

        assertNotNull(items.isEmpty());
        assertEquals(item1, items.get(0));
        assertEquals("Bread", items.get(1).getName());
    }

    @Test
    public void test_getItemById() {
        Item item1 = new Item(1L, "Flower", new BigDecimal(1.98), "Rose");
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        ResponseEntity<Item> response = itemController.getItemById(item1.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item item = response.getBody();

        assertNotNull(item);
        assertEquals("Rose", item.getDescription());
        assertEquals("Flower", item.getName());
    }

    @Test
    public void test_getItemsByName_HappyPath(){
        Item item1 = new Item(1L, "Flower", new BigDecimal(1.98), "Rose");
        when(itemRepository.findByName(item1.getName())).thenReturn(Lists.list(item1));

        ResponseEntity<List<Item>> response = itemController.getItemsByName(item1.getName());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();

        assertNotNull(items.isEmpty());
        assertEquals(item1, items.get(0));
        assertEquals("Flower", items.get(0).getName());
    }

    @Test
    public  void test_getItemsByName_Empty() {
        when(itemRepository.findByName(anyString())).thenReturn(Lists.list());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Flower");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
