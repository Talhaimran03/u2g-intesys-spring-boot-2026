package org.u2g.codylab.teamboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.u2g.codylab.teamboard.dto.CreateCardRequestApiDTO;
import org.u2g.codylab.teamboard.dto.CardApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateCardRequestApiDTO;
import org.u2g.codylab.teamboard.entity.Card;
import org.u2g.codylab.teamboard.entity.Column;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.CardMapper;
import org.u2g.codylab.teamboard.repository.CardRepository;
import org.u2g.codylab.teamboard.repository.ColumnRepository;
import org.u2g.codylab.teamboard.repository.UserRepository;

@Slf4j
@Transactional
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public CardService(CardRepository cardRepository, ColumnRepository columnRepository, UserRepository userRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.userRepository = userRepository;
        this.cardMapper = cardMapper;
    }

    public CardApiDTO createCard(CreateCardRequestApiDTO cardRequestApiDTO) {
        log.info("Creating card: {}", cardRequestApiDTO);

        Column column = columnRepository.findById(cardRequestApiDTO.getColumnId())
                .orElseThrow(() -> new CustomNotFoundException(
                        "column not found with id: " + cardRequestApiDTO.getColumnId()));

        // 🔥 récupérer utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new CustomNotFoundException("Authenticated user not found"));

        Card card = cardMapper.toEntity(cardRequestApiDTO);

        card.setColumn(column);
        card.setAssignedTo(user); // jamais null

        Card saved = cardRepository.save(card);

        log.info("Saved card: {}", saved);

        return cardMapper.toResponse(saved);
    }

    public void deleteCardById(Long id) {
        log.info("Deleting card: {}", id);
        if (!cardRepository.existsById(id)) {
            throw new CustomNotFoundException("card not found with id: " + id);
        }
        cardRepository.deleteById(id);
        log.info("Deleted card: {}", id);
    }

    public CardApiDTO getCardById(Long id) {
        log.info("Getting card: {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("card not found with id: " + id));
        log.info("Card found: {}", card);
        return cardMapper.toResponse(card);
    }

    public CardApiDTO updateCardById(Long id, UpdateCardRequestApiDTO cardRequestApiDTO) {
        log.info("Updating card: {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("card not found with id: " + id));

        if (cardRequestApiDTO.getTitle() != null) {
            card.setTitle(cardRequestApiDTO.getTitle());
        }
        if (cardRequestApiDTO.getDescription() != null) {
            card.setDescription(cardRequestApiDTO.getDescription());
        }
        if (cardRequestApiDTO.getAssignedToId() != null) {
            User user = userRepository.findById(cardRequestApiDTO.getAssignedToId())
                    .orElseThrow(() -> new CustomNotFoundException("user not found with id: " + cardRequestApiDTO.getAssignedToId()));
            card.setAssignedTo(user);
        }
        if (cardRequestApiDTO.getColumnId() != null) {
            Column column = columnRepository.findById(cardRequestApiDTO.getColumnId())
                    .orElseThrow(() -> new CustomNotFoundException("column not found with id: " + cardRequestApiDTO.getColumnId()));
            card.setColumn(column);
        }

        Card updated = cardRepository.save(card);
        log.info("Updated card: {}", updated);
        return cardMapper.toResponse(updated);
    }
}