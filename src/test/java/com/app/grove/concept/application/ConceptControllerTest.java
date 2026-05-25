package com.app.grove.concept.application;

import com.app.grove.concept.domain.ConceptService;
import com.app.grove.concept.dto.ConceptRequest;
import com.app.grove.concept.dto.ConceptResponse;
import com.app.grove.concept.dto.ConceptUpdateRequest;
import com.app.grove.exceptions.GlobalExceptionHandler;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConceptControllerTest {

    @Mock
    private ConceptService conceptService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new ConceptController(conceptService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateConceptWhenRequestIsValid() throws Exception {
        ConceptRequest request = new ConceptRequest();
        request.setTitle("Graph Theory");
        request.setContent("Nodes and edges");
        request.setWorkspaceId("w1");

        ConceptResponse response = new ConceptResponse();
        response.setId("c1");
        response.setTitle("Graph Theory");
        response.setContent("Nodes and edges");
        response.setCreatedAt(LocalDateTime.now());
        response.setWorkspaceId("w1");

        when(conceptService.createConcept(any(ConceptRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/concepts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("c1"))
            .andExpect(jsonPath("$.title").value("Graph Theory"));
    }

    @Test
    void shouldReturnNotFoundWhenConceptDoesNotExist() throws Exception {
        when(conceptService.getConceptById("missing")).thenThrow(new ResourceNotFoundException("Concepto no encontrado"));

        mockMvc.perform(get("/api/v1/concepts/missing"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Concepto no encontrado"));
    }
}
