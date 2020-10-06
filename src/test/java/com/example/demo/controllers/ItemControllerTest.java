package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private static final Long   ITEM_ID    = 1L;
    private static final String ITEM_NAME  = "Round Widget";
    private static final double ITEM_PRICE = 2.99;
    private static final String ITEM_DESC  = "A widget that is round";

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void get_items() throws Exception {
        when(itemRepo.findAll()).thenReturn(createMockItemList());

        // Test response
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Test response body
        assertFalse(response.getBody().isEmpty());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void get_item_by_id() throws Exception {
        when(itemRepo.findById(ITEM_ID)).thenReturn(Optional.of(createMockItem()));

        // Test response
        final ResponseEntity<Item> response = itemController.getItemById(ITEM_ID);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Test response body
        assertEquals(ITEM_NAME, response.getBody().getName());
        assertEquals(BigDecimal.valueOf(ITEM_PRICE), response.getBody().getPrice());
        assertEquals(ITEM_DESC, response.getBody().getDescription());
    }

    @Test
    public void get_item_by_name() {
        when(itemRepo.findByName("Item")).thenReturn(createMockItemList());

        // Test response
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Item");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Test response body
        Item itemOne = response.getBody().get(0);
        assertEquals("Item One", itemOne.getName());
        assertEquals(BigDecimal.valueOf(1.25), itemOne.getPrice());
        assertEquals("Item One Description", itemOne.getDescription());

        Item itemTwo = response.getBody().get(1);
        assertEquals("Item Two", itemTwo.getName());
        assertEquals(BigDecimal.valueOf(4.55), itemTwo.getPrice());
        assertEquals("Item Two Description", itemTwo.getDescription());
    }

    private Item createMockItem() {
        Item item = new Item();
        item.setId(ITEM_ID);
        item.setName(ITEM_NAME);
        item.setPrice(BigDecimal.valueOf(ITEM_PRICE));
        item.setDescription(ITEM_DESC);

        return item;
    }
    private List<Item> createMockItemList() {
        List<Item> itemList = new ArrayList<>();

        Item itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setName("Item One");
        itemOne.setPrice(BigDecimal.valueOf(1.25));
        itemOne.setDescription("Item One Description");

        Item itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setName("Item Two");
        itemTwo.setPrice(BigDecimal.valueOf(4.55));
        itemTwo.setDescription("Item Two Description");

        itemList.add(itemOne);
        itemList.add(itemTwo);

        return itemList;
    }
}
