package com.app.grove.flashcard.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.flashcard.dto.FlashcardRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import com.app.grove.flashcard.infrastructure.FlashcardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FlashcardService {
    private final FlashcardRepository flashcardRepository;
    private final ModelMapper modelMapper;

    public FlashcardResponse create(FlashcardRequest request){
        Flashcard flashcard=modelMapper.map(request, Flashcard.class);
        flashcard.setCreatedAt(LocalDateTime.now());
        Flashcard saved=flashcardRepository.save(flashcard);
        return modelMapper.map(saved, FlashcardResponse.class);
    }

    public Page<FlashcardResponse> getAll(Pageable pageable) {
        return flashcardRepository.findAll(pageable).map(fc -> modelMapper.map(fc, FlashcardResponse.class));
    }

    public FlashcardResponse findById(String id){
        Flashcard flashcard=flashcardRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Flashcard no encontrada con id "+id));
        return modelMapper.map(flashcard,FlashcardResponse.class);
    }

    public Page<FlashcardResponse> findByDifficulty(Integer difficulty, Pageable pageable) {
        return flashcardRepository.findByDifficulty(difficulty, pageable).map(fc -> modelMapper.map(fc, FlashcardResponse.class));
    }

    public void deleteById(String id){
        if(!flashcardRepository.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar: la flashcard no existe"); }
        flashcardRepository.deleteById(id);
    }
}
