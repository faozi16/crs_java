package com.af.carrsvt.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.carrsvt.dto.CustomerDto;
import com.af.carrsvt.entity.Customer;
import com.af.carrsvt.mapper.CustomerMapper;
import com.af.carrsvt.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerMapper customerMapper;

    @PostMapping("/create")
    public ResponseEntity<CustomerDto> createCustomer(@jakarta.validation.Valid @RequestBody CustomerDto customerDto) {
        Customer entity = customerMapper.customerDtoToCustomer(customerDto);
        Customer saved = customerService.saveCustomer(entity);
        return ResponseEntity.ok(customerMapper.customerToCustomerDto(saved));
    }

    @GetMapping("/get")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<Customer> all = customerService.getAllCustomers();
        List<CustomerDto> dtos = all.stream().map(customerMapper::customerToCustomerDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        Customer c = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerMapper.customerToCustomerDto(c));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, @jakarta.validation.Valid @RequestBody CustomerDto customerDetails) {
        Customer entity = customerMapper.customerDtoToCustomer(customerDetails);
        Customer updated = customerService.updateCustomer(id, entity);
        return ResponseEntity.ok(customerMapper.customerToCustomerDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
