package com.app.grove.flashcard.domain;

import com.app.grove.concept.infrastructure.ConceptRepository;
import com.app.grove.exceptions.ResourceNotFoundException;
import com.app.grove.flashcard.dto.*;
import com.app.grove.flashcard.infrastructure.FlashcardRepository;
import com.app.grove.flashcard.infrastructure.UserFlashcardProgressRepository;
import com.app.grove.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final UserFlashcardProgressRepository progressRepository;
    private final ConceptRepository conceptRepository;
    private final ModelMapper modelMapper;

    // ─── Basic CRUD ───────────────────────────────────────────────────────────

    public FlashcardResponse create(FlashcardRequest request) {
        Flashcard flashcard = modelMapper.map(request, Flashcard.class);
        flashcard.setCreatedAt(LocalDateTime.now());
        Flashcard saved = flashcardRepository.save(flashcard);
        return modelMapper.map(saved, FlashcardResponse.class);
    }

    public Page<FlashcardResponse> getAll(Pageable pageable) {
        return flashcardRepository.findAll(pageable)
                .map(fc -> modelMapper.map(fc, FlashcardResponse.class));
    }

    public FlashcardResponse findById(String id) {
        Flashcard flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard no encontrada con id " + id));
        return modelMapper.map(flashcard, FlashcardResponse.class);
    }

    public Page<FlashcardResponse> findByDifficulty(Integer difficulty, Pageable pageable) {
        return flashcardRepository.findByDifficulty(difficulty, pageable)
                .map(fc -> modelMapper.map(fc, FlashcardResponse.class));
    }

    public void deleteById(String id) {
        if (!flashcardRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: la flashcard no existe");
        }
        flashcardRepository.deleteById(id);
    }

    // ─── Study sessions (SRS) ────────────────────────────────────────────────

    /**
     * Returns flashcards due for review plus unreviewed flashcards (max 20 new per session).
     * Due cards come first ordered by nextReviewAt, then unreviewed ones.
     */
    public StudySessionResponse getStudySession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<UserFlashcardProgress> dueCards = progressRepository
                .findDueFlashcardsByUserId(currentUser.getId());

        List<FlashcardStudyResponse> sessionCards = new ArrayList<>(
                dueCards.stream()
                        .map(p -> toStudyResponse(p.getFlashcard(), p))
                        .collect(Collectors.toList())
        );

        // Include unreviewed flashcards (never studied by this user), up to 20 new cards
        flashcardRepository.findUnreviewedByUserId(currentUser.getId(), 20).stream()
                .map(f -> toStudyResponse(f, null))
                .forEach(sessionCards::add);

        StudySessionResponse session = new StudySessionResponse();
        session.setTotal(sessionCards.size());
        session.setFlashcards(sessionCards);
        return session;
    }

    /**
     * Records a review using the SM-2 spaced repetition algorithm.
     * rating: 1=Again, 2=Hard, 3=Good, 4=Easy
     * Returns the updated flashcard with its new scheduled interval.
     */
    @Transactional
    public FlashcardStudyResponse reviewFlashcard(FlashcardReviewRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Flashcard flashcard = flashcardRepository.findById(request.getFlashcardId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Flashcard no encontrada: " + request.getFlashcardId()));

        UserFlashcardProgress progress = progressRepository
                .findByUserIdAndFlashcardId(currentUser.getId(), request.getFlashcardId())
                .orElseGet(() -> {
                    UserFlashcardProgress p = new UserFlashcardProgress();
                    p.setUser(currentUser);
                    p.setFlashcard(flashcard);
                    p.setEaseFactor(2.5);
                    p.setInterval(0);
                    p.setReviewCount(0);
                    return p;
                });

        applySM2(progress, request.getRating());
        progress.setLastReviewedAt(LocalDateTime.now());
        progress.setReviewCount(progress.getReviewCount() + 1);
        progressRepository.save(progress);

        return toStudyResponse(flashcard, progress);
    }

    /**
     * Returns flashcards for a concept-focused study session.
     * GET /api/v1/flashcards/session/concept/{conceptId}
     */
    public StudySessionResponse getSessionByConcept(String conceptId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<com.app.grove.flashcard.domain.Flashcard> flashcards = flashcardRepository.findByConceptId(conceptId);
        List<UserFlashcardProgress> progressList = progressRepository
                .findByUserIdAndConceptId(currentUser.getId(), conceptId);

        List<FlashcardStudyResponse> cards = flashcards.stream().map(f -> {
            UserFlashcardProgress p = progressList.stream()
                    .filter(prog -> prog.getFlashcard() != null && prog.getFlashcard().getId().equals(f.getId()))
                    .findFirst()
                    .orElse(null);
            return toStudyResponse(f, p);
        }).collect(Collectors.toList());

        StudySessionResponse session = new StudySessionResponse();
        session.setTotal(cards.size());
        session.setFlashcards(cards);
        return session;
    }

    // ─── SM-2 Algorithm ───────────────────────────────────────────────────────

    /**
     * Simplified SM-2 spaced repetition algorithm.
     * rating: 1=Again (<1min), 2=Hard, 3=Good (correct+effort), 4=Easy
     */
    private void applySM2(UserFlashcardProgress p, int rating) {
        if (rating == 1) {
            // Again: reset interval
            p.setInterval(0);
            p.setNextReviewAt(LocalDateTime.now().plusMinutes(1));
            return;
        }

        double ef = p.getEaseFactor();
        int interval = p.getInterval();

        if (rating >= 3) {
            // Correct answer
            if (interval == 0) {
                interval = 1;
            } else if (interval == 1) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * ef);
            }
            // Update ease factor (SM-2 formula)
            ef = ef + (0.1 - (4 - rating) * (0.08 + (4 - rating) * 0.02));
            if (ef < 1.3) ef = 1.3;
        } else {
            // Hard: repeat with shorter interval
            interval = Math.max(1, (int) Math.round(interval * 1.2));
        }

        p.setEaseFactor(ef);
        p.setInterval(interval);
        p.setNextReviewAt(LocalDateTime.now().plusDays(interval));
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private FlashcardStudyResponse toStudyResponse(Flashcard f, UserFlashcardProgress p) {
        FlashcardStudyResponse r = new FlashcardStudyResponse();
        r.setId(f.getId());
        r.setFront(f.getFront());
        r.setBack(f.getBack());
        r.setHint(f.getHint());
        r.setDifficulty(f.getDifficulty());
        r.setCreatedAt(f.getCreatedAt());

        // Populate concept info by traversing HAS_FLASHCARD relationship backwards
        conceptRepository.findByFlashcardId(f.getId()).ifPresent(concept -> {
            r.setConceptId(concept.getId());
            r.setConceptTitle(concept.getTitle());
            if (concept.getTags() != null && !concept.getTags().isEmpty()) {
                r.setConceptTag(concept.getTags().get(0).getName());
            }
        });

        // SRS data
        if (p != null) {
            r.setInterval(p.getInterval());
            r.setEaseFactor(p.getEaseFactor());
            r.setNextReviewAt(p.getNextReviewAt());
            r.setReviewCount(p.getReviewCount());
        }

        return r;
    }
}
