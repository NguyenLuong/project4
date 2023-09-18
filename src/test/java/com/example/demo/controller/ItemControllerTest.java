package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    private List<Item> items;

    @Before
    public void setUp() {
        items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("test1");
        item1.setPrice(new BigDecimal(200));
        item1.setDescription("ThisIsTest1");
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("test2");
        item2.setPrice(new BigDecimal(400));
        item2.setDescription("ThisIsTest2");
        items.add(item1);
        items.add(item2);

        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_all_items() {
        Mockito.when(itemRepository.findAll()).thenReturn(items);
        final ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertEquals(2, items.size());
    }

    @Test
    public void get_item_by_id_successful() {
        long id = 1;
        Optional<Item> mockItem = Optional.of(items.get(0));

        Mockito.when(itemRepository.findById(id)).thenReturn(mockItem);
        final ResponseEntity<Item> response = itemController.getItemById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(true, item.getId().equals(new Long(1)));
        assertEquals("test1", item.getName());
        assertEquals(new BigDecimal(200), item.getPrice());
        assertEquals("ThisIsTest1", item.getDescription());
    }

    @Test
    public void get_item_by_id_not_found() {
        long id = 3;
        Optional<Item> mockItem = Optional.empty();

        Mockito.when(itemRepository.findById(id)).thenReturn(mockItem);
        final ResponseEntity<Item> response = itemController.getItemById(id);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_item_by_name_successful() {
        List<Item> mockItems = new ArrayList<>();
        mockItems.add(items.get(1));
        Mockito.when(itemRepository.findByName("test2")).thenReturn(mockItems);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("test2");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void get_item_by_name_not_found() {
        long id = 3;

        Mockito.when(itemRepository.findById(id)).thenReturn(null);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("test3");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
