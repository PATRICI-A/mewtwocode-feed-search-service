package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.ports.in.RegisterInteractionPort;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterInteractionService implements RegisterInteractionPort {

    private final FeedInteractionRepositoryPort interactionRepository;

    public RegisterInteractionService(FeedInteractionRepositoryPort interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    @Override
    public void register(UUID userId, UUID patchId, InteractionType action) {
        interactionRepository.save(userId, patchId, action);
    }
}
