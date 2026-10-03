package com.example.insurance.controller.customer;

import com.example.insurance.repository.customer.CustomerRepository;
import com.example.insurance.repository.policy.PolicyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "app.seed-data=false")
@AutoConfigureMockMvc
class CustomerApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired CustomerRepository customers;
    @Autowired PolicyRepository policies;

    @BeforeEach
    void clean() {
        policies.deleteAll();
        customers.deleteAll();
    }

    private long customer(String name, String email) throws Exception {
        String body = mapper.writeValueAsString(java.util.Map.of("name", name, "email", email));
        String result = mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
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

    @Test
    void rejectsMalformedCustomerRequestsAndInvalidPagination() throws Exception {
        mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{" )).andExpect(status().isBadRequest());
        mvc.perform(get("/api/customers/not-a-number")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/customers").param("page", "-1")).andExpect(status().isBadRequest());
    }
}
