package com.app.grove.tag.infrastructure;

import com.app.grove.AbstractNeo4jTest;
import com.app.grove.tag.domain.Tag;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TagRepositoryTest extends AbstractNeo4jTest {

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        tagRepository.deleteAll();
    }

    @Test
    void shouldFindTagByName() {
        Tag tag = new Tag();
        tag.setName("Spring Boot");
        tag.setDescription("Spring framework tag");
        tag.setColor("#6DB33F");

        tagRepository.save(tag);

        Tag found = tagRepository.findByName("Spring Boot");

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Spring Boot");
    }

    @Test
    void shouldSearchTagsByNameContainingKeywordIgnoringCase() {
        Tag springTag = new Tag();
        springTag.setName("Spring Boot");
        springTag.setDescription("Spring tag");
        springTag.setColor("#6DB33F");

        Tag javaTag = new Tag();
        javaTag.setName("Java");
        javaTag.setDescription("Java language tag");
        javaTag.setColor("#007396");

        tagRepository.saveAll(List.of(springTag, javaTag));

        List<Tag> results = tagRepository.searchByName("spring");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Spring Boot");
    }
}
