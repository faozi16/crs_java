package com.af.carrsvt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.af.carrsvt.entity.Customer;
import com.af.carrsvt.repository.CustomerRepository;
import com.af.carrsvt.service.CustomerService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @SuppressWarnings("removal")
    @MockBean
    private CustomerRepository customerRepository;

    @Test
    public void testSaveCustomer() {
        Customer customer = new Customer(null,"abah","pitersholeh","abah@farafamily.com","","A","","","","");

        Mockito.when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = customerService.saveCustomer(customer);
        assertEquals("abah", savedCustomer.getUsername());
    }

    // Add more tests for other service methods
}
