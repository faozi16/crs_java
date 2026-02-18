package com.af.carrsvt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CustomerControllerTest {

    Logger log = LoggerFactory.getLogger(CustomerControllerTest.class);
    
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateCustomer() throws Exception {
        String customerJson = "{\n" + //
                        "        \"username\": \"umma\",\n" + //
                        "        \"password\": \"cantiksholehah\",\n" + //
                        "        \"email\": \"umma@farafamily.id\",\n" + //
                        "        \"phoneNumber\": null,\n" + //
                        "        \"status\": \"A\",\n" + //
                        "        \"paymentMethod1\": null,\n" + //
                        "        \"paymentMethod2\": null,\n" + //
                        "        \"detailPaymentMethod1\": null,\n" + //
                        "        \"detailPaymentMethod2\": null\n" + //
                        "    }";

        log.info("CustomerControllerTest - testCreateCustomer()");
        log.info(customerJson);
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("umma"));
    }
    // Add more tests for other endpoints
}

