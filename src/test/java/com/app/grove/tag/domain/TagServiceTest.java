package com.app.grove.tag.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.tag.dto.TagRequest;
import com.app.grove.tag.dto.TagResponse;
import com.app.grove.tag.infrastructure.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    private TagService tagService;

    @BeforeEach
    void setUp() {
        tagService = new TagService(tagRepository, new ModelMapper());
    }

    @Test
    void shouldCreateTagWhenValid() {
        TagRequest request = new TagRequest();
        request.setName("Java");
        request.setDescription("Java language");
        request.setColor("#f89820");

        Tag saved = new Tag();
        saved.setId("t1");
        saved.setName("Java");
        saved.setDescription("Java language");
        saved.setColor("#f89820");

        when(tagRepository.save(org.mockito.ArgumentMatchers.any(Tag.class))).thenReturn(saved);

        TagResponse response = tagService.createTag(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("t1");
        assertThat(response.getName()).isEqualTo("Java");
    }

    @Test
    void shouldThrowWhenTagNotFound() {
        when(tagRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.getTagById("missing"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Tag no encontrado");
    }

    @Test
    void shouldSearchTagsByName() {
        Tag java = new Tag();
        java.setId("t1");
        java.setName("Java");
        java.setDescription("Java language");
        java.setColor("#f89820");

        var pageable = PageRequest.of(0, 10);
        when(tagRepository.searchByName("java", pageable))
                .thenReturn(new PageImpl<>(List.of(java), pageable, 1));

        var results = tagService.searchByName("java", pageable);

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getName()).isEqualTo("Java");
    }
}
