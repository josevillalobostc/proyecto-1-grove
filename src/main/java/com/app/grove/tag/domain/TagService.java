package com.app.grove.tag.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.tag.dto.TagRequest;
import com.app.grove.tag.dto.TagResponse;
import com.app.grove.tag.infrastructure.TagRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public TagResponse createTag(TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
        tag.setColor(request.getColor());
        tag = tagRepository.save(tag);
        return modelMapper.map(tag, TagResponse.class);
    }

    public TagResponse getTagById(String id) {
        Tag tag = tagRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Tag no encontrado: " + id)
            );
        return modelMapper.map(tag, TagResponse.class);
    }

    public List<TagResponse> getAllTags() {
        return tagRepository
            .findAll()
            .stream()
            .map(tag -> modelMapper.map(tag, TagResponse.class))
            .collect(Collectors.toList());
    }

    @Transactional
    public TagResponse updateTag(String id, TagRequest request) {
        Tag tag = tagRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Tag no encontrado: " + id)
            );
        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
        tag.setColor(request.getColor());
        tag = tagRepository.save(tag);
        return modelMapper.map(tag, TagResponse.class);
    }

    @Transactional
    public void deleteTag(String id) {
        Tag tag = tagRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Tag no encontrado: " + id)
            );
        tagRepository.delete(tag);
    }

    public List<TagResponse> searchByName(String keyword) {
        return tagRepository
            .searchByName(keyword)
            .stream()
            .map(tag -> modelMapper.map(tag, TagResponse.class))
            .collect(Collectors.toList());
    }
}
