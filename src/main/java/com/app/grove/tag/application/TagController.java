package com.app.grove.tag.application;

import com.app.grove.tag.domain.TagService;
import com.app.grove.tag.dto.TagRequest;
import com.app.grove.tag.dto.TagResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(
        @Valid @RequestBody TagRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            tagService.createTag(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTag(@PathVariable String id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TagResponse>> searchByName(
        @RequestParam String keyword
    ) {
        return ResponseEntity.ok(tagService.searchByName(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(
        @PathVariable String id,
        @Valid @RequestBody TagRequest request
    ) {
        return ResponseEntity.ok(tagService.updateTag(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
