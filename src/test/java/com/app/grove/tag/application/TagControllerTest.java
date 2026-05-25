package com.app.grove.tag.application;

import com.app.grove.exceptions.GlobalExceptionHandler;
import com.app.grove.tag.domain.TagService;
import com.app.grove.tag.dto.TagRequest;
import com.app.grove.tag.dto.TagResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagService tagService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new TagController(tagService))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateTagWhenRequestIsValid() throws Exception {
        TagRequest request = new TagRequest();
        request.setName("Spring");
        request.setDescription("Spring framework");
        request.setColor("#6DB33F");

        TagResponse response = new TagResponse();
        response.setId("t1");
        response.setName("Spring");

        when(tagService.createTag(any(TagRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("t1"))
            .andExpect(jsonPath("$.name").value("Spring"));
    }

    @Test
    void shouldSearchTagsByKeyword() throws Exception {
        TagResponse response = new TagResponse();
        response.setId("t1");
        response.setName("Spring");

        var pageable = PageRequest.of(0, 20);
        when(tagService.searchByName(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/api/v1/tags/search").param("keyword", "spring"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value("t1"));
    }
}
