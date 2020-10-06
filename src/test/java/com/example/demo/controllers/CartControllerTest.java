package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
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
import static org.mockito.Mockito.*;

public class CartControllerTest {
    private static final Long   USER_ID  = 0L;
    private static final String USERNAME = "test";
    private static final String PASSWORD = "testPassword";
    private CartController cartController;

    private CartRepository cartRepo = mock(CartRepository.class);

    private UserRepository userRepo = mock(UserRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "cartRepository", cartRepo);
        TestUtils.injectObject(cartController, "userRepository", userRepo);
        TestUtils.injectObject(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void add_to_cart_success() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createMockUser());
        when(itemRepo.findById(1L)).thenReturn(Optional.of(createMockItem()));

        final ResponseEntity<Cart> response = cartController.addTocart(createMockCartRequestForAdding());

        // Test response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Test response body
        Cart cart = response.getBody();
        assertEquals(USERNAME, cart.getUser().getUsername());
        assertTrue(cart.getItems().size() > 0);
        assertEquals(BigDecimal.valueOf(2.99), cart.getItems().get(0).getPrice());
        assertEquals(BigDecimal.valueOf(2.99 + 2.99), cart.getTotal());
    }

    @Test
    public void add_to_cart_fail_by_invalid_user() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(createMockCartRequestForAdding());

        // Test response
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void add_to_cart_fail_by_invalid_item() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createMockUser());
        when(itemRepo.findById(1L)).thenReturn(Optional.ofNullable(null));

        final ResponseEntity<Cart> response = cartController.addTocart(createMockCartRequestForAdding());

        // Test response
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_success() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createMockUserWithCart());
        when(itemRepo.findById(1L)).thenReturn(Optional.of(createMockItem()));

        // Test remove one item response
        final ResponseEntity<Cart> responseOne = cartController.removeFromcart(createMockCartRequestForRemoving());
        //   Test response status
        assertNotNull(responseOne);
        assertEquals(200, responseOne.getStatusCodeValue());
        //   Test response body
        Cart cart = responseOne.getBody();
        assertEquals(USERNAME, cart.getUser().getUsername());
        assertEquals(1, cart.getItems().size());
        assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());

        // Test remove all item response
        final ResponseEntity<Cart> responseTwo = cartController.removeFromcart(createMockCartRequestForRemoving());
        //    Test response status
        assertNotNull(responseTwo);
        assertEquals(200, responseOne.getStatusCodeValue());
        //    Test response body
        cart = responseTwo.getBody();
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, cart.getTotal().intValue());
    }

    @Test
    public void remove_from_cart_invalid_user() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.removeFromcart(createMockCartRequestForRemoving());

        // Test response
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_invalid_item() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createMockUser());
        when(itemRepo.findById(1L)).thenReturn(Optional.ofNullable(null));

        final ResponseEntity<Cart> response = cartController.removeFromcart(createMockCartRequestForRemoving());

        // Test response
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }


    private ModifyCartRequest createMockCartRequestForAdding() {
        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(1L);
        r.setQuantity(2);
        r.setUsername(USERNAME);

        return r;
    }

    private ModifyCartRequest createMockCartRequestForRemoving() {
        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(1L);
        r.setQuantity(1);
        r.setUsername(USERNAME);

        return r;
    }

    private User createMockUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        return user;
    }

    private Item createMockItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item One");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("Item One Description.");

        return item;
    }

    private User createMockUserWithCart() {
        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(createMockItem());
        cart.addItem(createMockItem());

        user.setCart(cart);
        return user;
    }

}
