package com.app.grove.comment.application;

import com.app.grove.comment.domain.CommentService;
import com.app.grove.comment.dto.CommentRequestDTO;
import com.app.grove.comment.dto.CommentResponseDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new CommentController(commentService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateCommentWhenRequestIsValid() throws Exception {
        CommentRequestDTO request = new CommentRequestDTO();
        request.setText("Great explanation");
        request.setAuthorId("u1");
        request.setConceptId("c1");

        CommentResponseDTO response = new CommentResponseDTO();
        response.setId("cm1");
        response.setText("Great explanation");
        response.setAuthorId("u1");
        response.setConceptId("c1");
        response.setCreatedAt(LocalDateTime.now());

        when(commentService.createComment(any(CommentRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("cm1"))
            .andExpect(jsonPath("$.text").value("Great explanation"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingComment() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Comentario no encontrado"))
            .when(commentService)
            .deleteComment("missing");

        mockMvc.perform(delete("/api/comments/missing"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Comentario no encontrado"));
    }
}
