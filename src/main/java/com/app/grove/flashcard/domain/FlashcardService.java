package com.app.grove.flashcard.domain;

import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.flashcard.dto.FlashcardRequest;
import com.app.grove.flashcard.dto.FlashcardResponse;
import com.app.grove.flashcard.infrastructure.FlashcardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<FlashcardResponse> getAll(){
        return flashcardRepository.findAll().stream()
                .map(flashcard -> modelMapper.map(this,FlashcardResponse.class))
                .collect(Collectors.toList());
    }

    public FlashcardResponse findById(String id){
        Flashcard flashcard=flashcardRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Flashcard no encontrada con id "+id));
        return modelMapper.map(flashcard,FlashcardResponse.class);
    }

    public List<FlashcardResponse> findByDifficulty(Integer difficulty){
        List<Flashcard> flashcards=flashcardRepository.findByDifficulty(difficulty);
        return flashcards.stream()
                .map(flashcard->modelMapper.map(this,FlashcardResponse.class))
                .collect(Collectors.toList());
    }

    public void deleteById(String id){
        if(!flashcardRepository.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar: la flashcard no existe"); }
        flashcardRepository.deleteById(id);
    }

}
