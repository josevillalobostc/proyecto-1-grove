package com.app.grove.workspace.application;

import com.app.grove.exceptions.GlobalExceptionHandler;
import com.app.grove.workspace.domain.WorkspaceService;
import com.app.grove.workspace.dto.WorkspaceRequest;
import com.app.grove.workspace.dto.WorkspaceResponse;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WorkspaceControllerTest {

    @Mock
    private WorkspaceService workspaceService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new WorkspaceController(workspaceService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateWorkspaceWhenRequestIsValid() throws Exception {
        WorkspaceRequest request = new WorkspaceRequest();
        request.setName("Study Group");
        request.setDescription("Study together");
        request.setPublic(true);

        WorkspaceResponse response = new WorkspaceResponse();
        response.setId("w1");
        response.setName("Study Group");
        response.setPublic(true);
        response.setCreatedAt(LocalDateTime.now());

        when(workspaceService.createWorkspace(any(WorkspaceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/workspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("w1"))
            .andExpect(jsonPath("$.name").value("Study Group"));
    }

    @Test
    void shouldReturnPublicWorkspaces() throws Exception {
        WorkspaceResponse response = new WorkspaceResponse();
        response.setId("w1");
        response.setName("Public Group");
        response.setPublic(true);

        when(workspaceService.getPublicWorkspaces()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/workspaces/public"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("w1"));
    }
}
