package com.app.grove.concept.infrastructure;

import com.app.grove.AbstractNeo4jTest;
import com.app.grove.concept.domain.Concept;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ConceptRepositoryTest extends AbstractNeo4jTest {

    @Autowired
    private ConceptRepository conceptRepository;

    @BeforeEach
    void setUp() {
        conceptRepository.deleteAll();
    }

    @Test
    void shouldFindConceptByTitle() {
        Concept concept = new Concept();
        concept.setTitle("Graph Theory");
        concept.setContent("Introduction to graphs");
        concept.setCreatedAt(LocalDateTime.now());
        concept.setUpdatedAt(LocalDateTime.now());

        conceptRepository.save(concept);

        Concept found = conceptRepository.findByTitle("Graph Theory");

        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Graph Theory");
        assertThat(found.getContent()).isEqualTo("Introduction to graphs");
    }

    @Test
    void shouldSearchConceptsByTitleContainingKeywordIgnoringCase() {
        Concept first = new Concept();
        first.setTitle("Spring Boot Basics");
        first.setContent("Learn Spring Boot");
        first.setCreatedAt(LocalDateTime.now());
        first.setUpdatedAt(LocalDateTime.now());

        Concept second = new Concept();
        second.setTitle("Advanced Spring Features");
        second.setContent("Deep dive into Spring");
        second.setCreatedAt(LocalDateTime.now());
        second.setUpdatedAt(LocalDateTime.now());

        conceptRepository.saveAll(List.of(first, second));

        var pageable = PageRequest.of(0, 10);
        var results = conceptRepository.searchByTitleContaining("spring", pageable).getContent();

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Concept::getTitle)
                .containsExactlyInAnyOrder("Spring Boot Basics", "Advanced Spring Features");
    }

    @Test
    void shouldFindAllPrerequisitesByConceptIdInPathOrder() {
        Concept core = new Concept();
        core.setTitle("Core Concepts");
        core.setContent("Base knowledge");
        core.setCreatedAt(LocalDateTime.now());
        core.setUpdatedAt(LocalDateTime.now());

        Concept intermediate = new Concept();
        intermediate.setTitle("Intermediate Concepts");
        intermediate.setContent("Builds on core");
        intermediate.setCreatedAt(LocalDateTime.now());
        intermediate.setUpdatedAt(LocalDateTime.now());
        intermediate.setPrerequisites(List.of(core));

        Concept advanced = new Concept();
        advanced.setTitle("Advanced Concepts");
        advanced.setContent("Builds on intermediate");
        advanced.setCreatedAt(LocalDateTime.now());
        advanced.setUpdatedAt(LocalDateTime.now());
        advanced.setPrerequisites(List.of(intermediate));

        conceptRepository.save(core);
        conceptRepository.save(intermediate);
        conceptRepository.save(advanced);

        List<Concept> prerequisites = conceptRepository.findAllPrerequisites(core.getId());

        assertThat(prerequisites).hasSize(2);
        assertThat(prerequisites.get(0).getTitle()).isEqualTo("Intermediate Concepts");
        assertThat(prerequisites.get(1).getTitle()).isEqualTo("Advanced Concepts");
    }
}
