package com.af.carrsvt.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.af.carrsvt.entity.Customer;
import com.af.carrsvt.entity.Driver;
import com.af.carrsvt.entity.PaymentMethod;
import com.af.carrsvt.entity.Vehicle;
import com.af.carrsvt.entity.Reservation;
import com.af.carrsvt.repository.CustomerRepository;
import com.af.carrsvt.repository.DriverRepository;
import com.af.carrsvt.repository.PaymentMethodRepository;
import com.af.carrsvt.repository.VehicleRepository;
import com.af.carrsvt.repository.ReservationRepository;
import com.af.carrsvt.service.CustomerService;
import com.af.carrsvt.service.PaymentMethodService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ContainerConfiguration.class)
class IntegrationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private ObjectMapper objectMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        paymentMethodRepository.deleteAll();
        reservationRepository.deleteAll();
        vehicleRepository.deleteAll();
        driverRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void testCustomerCreationWithPasswordEncoding() {
        Customer c = new Customer();
        c.setFirstName("Test");
        c.setLastName("User");
        c.setUsername("testuser");
        c.setPassword("password123");
        c.setEmail("test@example.com");
        c.setPhoneNumber("1234567890");
        c.setStatus("A");
        Customer saved = customerService.saveCustomer(c);

        assertNotNull(saved.getCustomerId());
        assertEquals("testuser", saved.getUsername());
        assertNotEquals("password123", saved.getPassword()); // password should be hashed
        assertTrue(saved.getPassword().startsWith("$2a$")); // BCrypt hash prefix
    }

    @Test
    void testRetrieveCustomerById() {
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName("Doe");
        c.setUsername("john");
        c.setPassword(passwordEncoder.encode("password123"));
        c.setEmail("john@example.com");
        c.setPhoneNumber("555-1234");
        c.setStatus("A");
        Customer saved = customerRepository.save(c);

        Customer retrieved = customerService.getCustomerById(saved.getCustomerId());
        assertEquals(saved.getCustomerId(), retrieved.getCustomerId());
        assertEquals("john", retrieved.getUsername());
    }

    @Test
    void testPaymentMethodCreation() {
        // Create customer first
        Customer c = new Customer();
        c.setFirstName("Alice");
        c.setLastName("Tester");
        c.setUsername("alice");
        c.setPassword(passwordEncoder.encode("pass123"));
        c.setEmail("alice@test.com");
        c.setPhoneNumber("555-9999");
        c.setStatus("A");
        Customer savedCustomer = customerRepository.save(c);

        // Create payment method
        PaymentMethod pm = new PaymentMethod();
        pm.setCustomerId(savedCustomer.getCustomerId());
        pm.setMethodType("CARD");
        pm.setDetails("****1234");
        pm.setPrimaryMethod(true);

        PaymentMethod savedPm = paymentMethodService.savePaymentMethod(pm);

        assertNotNull(savedPm.getPaymentMethodId());
        assertEquals("CARD", savedPm.getMethodType());
        assertNotNull(savedPm.getCreatedAt());
    }

    @Test
    void testRetrievePaymentMethodsByCustomer() {
        Customer c = new Customer();
        c.setFirstName("Bob");
        c.setLastName("Tester");
        c.setUsername("bob");
        c.setPassword(passwordEncoder.encode("pass456"));
        c.setEmail("bob@test.com");
        c.setPhoneNumber("555-8888");
        c.setStatus("A");
        Customer savedCustomer = customerRepository.save(c);

        // Add multiple payment methods
        PaymentMethod pm1 = new PaymentMethod(null, savedCustomer.getCustomerId(), "CARD", "****5678", true, OffsetDateTime.now(), null);
        PaymentMethod pm2 = new PaymentMethod(null, savedCustomer.getCustomerId(), "PAYPAL", "bob@paypal.com", false, OffsetDateTime.now(), null);

        paymentMethodService.savePaymentMethod(pm1);
        paymentMethodService.savePaymentMethod(pm2);

        List<PaymentMethod> methods = paymentMethodService.getByCustomerId(savedCustomer.getCustomerId());

        assertEquals(2, methods.size());
    }

    @Test
    void testDriverCreationWithNewDateType() {
        Driver d = new Driver();
        d.setUsername("driver001");
        d.setPassword("driverpass123");
        d.setEmail("driver@company.com");
        d.setPhoneNumber("555-0001");
        d.setLicenseDriver("DL-12345");
        d.setDateOfBirth(LocalDate.of(1990, 5, 15));
        d.setPlaceOfBirth("New York");
        d.setAddress("123 Driver Lane");
        d.setStatus("A");

        Driver saved = driverRepository.save(d);

        assertNotNull(saved.getDriverId());
        assertEquals(LocalDate.of(1990, 5, 15), saved.getDateOfBirth());
    }

    @Test
    void testReservationWithOffsetDateTime() {
        // Create customer
        Customer c = new Customer();
        c.setFirstName("Charlie");
        c.setLastName("Tester");
        c.setUsername("charlie");
        c.setPassword(passwordEncoder.encode("pass789"));
        c.setEmail("charlie@test.com");
        c.setPhoneNumber("555-7777");
        c.setStatus("A");
        Customer savedCustomer = customerRepository.save(c);

        // Create vehicle
        Vehicle v = new Vehicle();
        v.setVehicleType("SUV");
        v.setLicensePlate("ABC-1234");
        v.setStatus("AVAILABLE");
        Vehicle savedVehicle = vehicleRepository.save(v);

        // Create reservation with OffsetDateTime
        Reservation res = new Reservation();
        res.setCustomerId(savedCustomer.getCustomerId());
        res.setVehicleId(savedVehicle.getVehicleId());
        res.setPickupTime(OffsetDateTime.now().plusHours(2));
        res.setPickupLocation("Downtown");
        res.setDropoffLocation("Airport");
        res.setStatus("PENDING");

        Reservation savedRes = reservationRepository.save(res);

        assertNotNull(savedRes.getReservationId());
        assertNotNull(savedRes.getPickupTime());
    }

    @Test
    void testEndToEndReservationFlow() {
        // 1. Create customer
        Customer cust = new Customer();
        cust.setFirstName("Frank");
        cust.setLastName("Tester");
        cust.setUsername("frank");
        cust.setPassword(passwordEncoder.encode("frankpass"));
        cust.setEmail("frank@test.com");
        cust.setPhoneNumber("555-6666");
        cust.setStatus("A");
        Customer savedCust = customerRepository.save(cust);

        // 2. Create vehicle
        Vehicle veh = new Vehicle();
        veh.setVehicleType("Sedan");
        veh.setLicensePlate("XYZ-9876");
        veh.setStatus("AVAILABLE");
        Vehicle savedVeh = vehicleRepository.save(veh);

        // 3. Create payment method
        PaymentMethod pm = new PaymentMethod(null, savedCust.getCustomerId(), "CARD", "****9999", true, OffsetDateTime.now(), null);
        PaymentMethod savedPm = paymentMethodService.savePaymentMethod(pm);

        // 4. Make reservation
        Reservation res = new Reservation();
        res.setCustomerId(savedCust.getCustomerId());
        res.setVehicleId(savedVeh.getVehicleId());
        res.setPickupTime(OffsetDateTime.now().plusHours(4));
        res.setPickupLocation("Hotel");
        res.setDropoffLocation("Station");
        res.setStatus("CONFIRMED");

        Reservation savedRes = reservationRepository.save(res);

        // Verify all entities exist
        assertNotNull(savedCust.getCustomerId());
        assertNotNull(savedVeh.getVehicleId());
        assertNotNull(savedPm.getPaymentMethodId());
        assertNotNull(savedRes.getReservationId());

        // Verify relationships
        assertEquals(savedCust.getCustomerId(), savedRes.getCustomerId());
        assertEquals(savedVeh.getVehicleId(), savedRes.getVehicleId());
        assertEquals(savedCust.getCustomerId(), savedPm.getCustomerId());
    }
}
