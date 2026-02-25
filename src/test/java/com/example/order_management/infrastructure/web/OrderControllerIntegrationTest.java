package com.example.order_management.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Crear pedido válido devuelve 201 con detalles del pedido")
    void createOrder_returnsCreatedWithOrderDetails() throws Exception {
        String payload = """
                {
                  "customerId": "00000000-0000-0000-0000-000000000001",
                  "items": [
                    {
                      "productId": "00000000-0000-0000-0000-000000000010",
                      "quantity": 2,
                      "unitPrice": 15.50
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(31.00))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("Crear pedido con lista de ítems vacía devuelve 400")
    void createOrder_withEmptyItems_returnsBadRequest() throws Exception {
        String payload = """
                {
                  "customerId": "00000000-0000-0000-0000-000000000001",
                  "items": []
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Consultar pedido existente devuelve detalles completos")
    void getOrder_returnsOrderDetails() throws Exception {
        String payload = """
                {
                  "customerId": "00000000-0000-0000-0000-000000000002",
                  "items": [
                    {
                      "productId": "00000000-0000-0000-0000-000000000020",
                      "quantity": 1,
                      "unitPrice": 50.00
                    }
                  ]
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createResponse = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String orderId = createResponse.get("orderId").asText();
        assertThat(orderId).isNotBlank();

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.customerId").value("00000000-0000-0000-0000-000000000002"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(50.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @DisplayName("Pagar pedido pendiente cambia estado a PAID")
    void payOrder_changesStatusToPaid() throws Exception {
        String payload = """
                {
                  "customerId": "00000000-0000-0000-0000-000000000003",
                  "items": [
                    {
                      "productId": "00000000-0000-0000-0000-000000000030",
                      "quantity": 2,
                      "unitPrice": 20.00
                    }
                  ]
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createResponse = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String orderId = createResponse.get("orderId").asText();

        mockMvc.perform(post("/api/v1/orders/{orderId}/pay", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("Pagar un pedido ya pagado devuelve 409 Conflict")
    void payOrder_whenAlreadyPaid_returnsConflict() throws Exception {
        String payload = """
                {
                  "customerId": "00000000-0000-0000-0000-000000000004",
                  "items": [
                    {
                      "productId": "00000000-0000-0000-0000-000000000040",
                      "quantity": 2,
                      "unitPrice": 20.00
                    }
                  ]
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createResponse = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String orderId = createResponse.get("orderId").asText();

        mockMvc.perform(post("/api/v1/orders/{orderId}/pay", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        mockMvc.perform(post("/api/v1/orders/{orderId}/pay", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}

