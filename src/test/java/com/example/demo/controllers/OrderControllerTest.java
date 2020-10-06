package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private static final String USERNAME = "test";
    private static final String PASSWORD = "testPassword";
    private static final Long   USER_ID  = 0L;

    private OrderController orderController;

    private UserRepository userRepo = mock(UserRepository.class);

    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepo);
        TestUtils.injectObject(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submit_order_success() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createMockUser());

        final ResponseEntity<UserOrder> response = orderController.submit(USERNAME);

        // Test response status
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Test response body
        UserOrder order = response.getBody();
        assertFalse(order.getItems().isEmpty());
        assertEquals(USERNAME, order.getUser().getUsername());
        assertEquals("Item One", order.getItems().get(0).getName());
        assertEquals(BigDecimal.valueOf(2.99), order.getTotal());
    }

    @Test
    public void submit_order_fail_invalid_user() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit(USERNAME);

        // Test response status
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_user_order_history_success() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(createMockUserWithOrderList());

        User user = userRepo.findByUsername(USERNAME);

        when(orderRepo.findByUser(user)).thenReturn(createMockOrderList(user));

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);

        // Test response status
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Test response body
        List<UserOrder> userOrderList = response.getBody();
        assertFalse(userOrderList.isEmpty());
        //    Test the username of each order
        assertEquals(USERNAME, userOrderList.get(0).getUser().getUsername());
        assertEquals(USERNAME, userOrderList.get(1).getUser().getUsername());
        //    Test the total of each order
        assertEquals(BigDecimal.valueOf(22.45), userOrderList.get(0).getTotal());
        assertEquals(BigDecimal.valueOf(10.99), userOrderList.get(1).getTotal());
        //    Test the total number of items in each order
        assertEquals(3, userOrderList.get(0).getItems().size());
        assertEquals(1, userOrderList.get(1).getItems().size());
    }

    @Test
    public void get_user_order_history_invalid_username() {
        when(userRepo.findByUsername(USERNAME)).thenReturn(null);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);

        // Test response status
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private User createMockUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item One");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("Testing item one");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(item);

        user.setCart(cart);

        return user;
    }

    private User createMockUserWithOrderList() {
        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        return user;
    }

    private List<UserOrder> createMockOrderList(User user) {
        Item item1 = new Item();
        item1.setId(0L);
        item1.setName("Item1");
        item1.setPrice(BigDecimal.valueOf(0.50));

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Item2");
        item2.setPrice(BigDecimal.valueOf(9.50));

        Item item3 = new Item();
        item3.setId(2L);
        item3.setName("Item3");
        item3.setPrice(BigDecimal.valueOf(12.45));

        List<Item> itemList1 = new ArrayList<>();
        itemList1.add(item1);
        itemList1.add(item2);
        itemList1.add(item3);

        UserOrder order1 = new UserOrder();
        order1.setItems(itemList1);
        order1.setUser(user);
        order1.setTotal(BigDecimal.valueOf(22.45));

        Item item4 = new Item();
        item4.setId(3L);
        item4.setName("Item4");
        item4.setPrice(BigDecimal.valueOf(10.99));

        List<Item> itemList2 = new ArrayList<>();
        itemList2.add(item4);

        UserOrder order2 = new UserOrder();
        order2.setItems(itemList2);
        order2.setUser(user);
        order2.setTotal(BigDecimal.valueOf(10.99));

        List<UserOrder> orderList = new ArrayList<>();
        orderList.add(order1);
        orderList.add(order2);

        return orderList;
    }
}
