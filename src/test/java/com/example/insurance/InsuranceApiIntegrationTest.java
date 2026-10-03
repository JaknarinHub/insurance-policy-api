package com.example.insurance;
import com.example.insurance.customer.CustomerRepository;
import com.example.insurance.policy.PolicyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties = "app.seed-data=false")
@AutoConfigureMockMvc
class InsuranceApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired CustomerRepository customers;
    @Autowired PolicyRepository policies;
    @BeforeEach void clean() { policies.deleteAll(); customers.deleteAll(); }
    private long customer(String name, String email) throws Exception {
        String body = mapper.writeValueAsString(java.util.Map.of("name", name, "email", email));
        String result = mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated()).andExpect(header().exists("Location")).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }
    private String policy(long customerId, String number, String amount, String end) {
        return "{\"customerId\":" + customerId + ",\"policyNumber\":\"" + number + "\",\"sumAssured\":" + amount +
            ",\"startDate\":\"2026-01-01\",\"endDate\":\"" + end + "\",\"status\":\"ACTIVE\"}";
    }
    @Test void customerCrudAndSearch() throws Exception {
        long id = customer("Alex Demo", "ALEX@example.com");
        mvc.perform(get("/api/customers/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("alex@example.com"));
        mvc.perform(get("/api/customers").param("name", "aLeX").param("size", "1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].id").value(id));
        mvc.perform(put("/api/customers/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Alex Updated\",\"email\":\"updated@example.com\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Alex Updated"));
        mvc.perform(delete("/api/customers/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/customers/" + id)).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Customer not found"));
    }
    @Test void policyCrudFiltersAndCustomerDeleteProtection() throws Exception {
        long customerId = customer("Alex Demo", "alex@example.com");
        long otherId = customer("Sam Sample", "sam@example.com");
        String result = mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policy(customerId, "demo-100", "100000.00", "2027-01-01")))
            .andExpect(status().isCreated()).andExpect(header().exists("Location")).andExpect(jsonPath("$.policyNumber").value("DEMO-100"))
            .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(result).get("id").asLong();
        mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policy(otherId, "DEMO-200", "20000", "2027-01-01"))).andExpect(status().isCreated());
        mvc.perform(get("/api/policies/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.customerId").value(customerId));
        mvc.perform(get("/api/policies").param("customerId", "" + customerId).param("status", "ACTIVE"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/policies").param("status", "CANCELLED")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(delete("/api/customers/" + customerId)).andExpect(status().isConflict());
        mvc.perform(put("/api/policies/" + id).contentType(MediaType.APPLICATION_JSON).content(policy(customerId, "DEMO-100", "120000.00", "2028-01-01")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.sumAssured").value(120000.00));
        mvc.perform(delete("/api/policies/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/policies/" + id)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/customers/" + customerId)).andExpect(status().isNoContent());
    }
    @Test void rejectsInvalidAndDuplicateCustomers() throws Exception {
        long id = customer("Alex", "alex@example.com");
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\" \",\"email\":\"bad\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.name").exists()).andExpect(jsonPath("$.errors.email").exists());
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Other\",\"email\":\"ALEX@example.com\"}"))
            .andExpect(status().isConflict());
        long other = customer("Sam", "sam@example.com");
        mvc.perform(put("/api/customers/" + other).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Sam\",\"email\":\"alex@example.com\"}"))
            .andExpect(status().isConflict());
        mvc.perform(put("/api/customers/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Alex\",\"email\":\"alex@example.com\"}"))
            .andExpect(status().isOk());
    }
    @Test void rejectsInvalidPoliciesAndUnknownCustomer() throws Exception {
        long id = customer("Alex", "alex@example.com");
        for (String amount : new String[]{"0", "-1", "1.001", "10000000000000"}) {
            mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policy(id, "DEMO-001", amount, "2027-01-01")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.sumAssured").exists());
        }
        mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policy(id, "DEMO-001", "100", "2025-01-01")))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("End date must be on or after start date"));
        mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policy(999999, "DEMO-001", "100", "2027-01-01"))).andExpect(status().isNotFound());
        mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.customerId").exists());
    }
    @Test void rejectsDuplicatePolicyOnCreateAndUpdate() throws Exception {
        long id = customer("Alex", "alex@example.com");
        String first = mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policy(id, "DEMO-001", "100", "2027-01-01")))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long firstId = mapper.readTree(first).get("id").asLong();
        mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policy(id, "demo-001", "100", "2027-01-01"))).andExpect(status().isConflict());
        String second = mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policy(id, "DEMO-002", "100", "2027-01-01")))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long secondId = mapper.readTree(second).get("id").asLong();
        mvc.perform(put("/api/policies/" + secondId).contentType(MediaType.APPLICATION_JSON).content(policy(id, "DEMO-001", "100", "2027-01-01"))).andExpect(status().isConflict());
        mvc.perform(put("/api/policies/" + firstId).contentType(MediaType.APPLICATION_JSON).content(policy(id, "DEMO-001", "100", "2025-01-01"))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/policies/" + firstId)).andExpect(jsonPath("$.endDate").value("2027-01-01"));
    }
    @Test void rejectsMalformedRequestsAndInvalidPagination() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{" )).andExpect(status().isBadRequest());
        mvc.perform(get("/api/customers/not-a-number")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/customers").param("page", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/policies").param("size", "101")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/policies").param("status", "UNKNOWN")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/policies").param("customerId", "0")).andExpect(status().isBadRequest());
        mvc.perform(delete("/api/policies/999999")).andExpect(status().isNotFound());
    }
}
