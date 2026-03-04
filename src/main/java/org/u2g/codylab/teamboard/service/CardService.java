package org.u2g.codylab.teamboard.service;

import lombok.extern.slf4j.Slf4j;
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
        if (cardRequestApiDTO.getTitle() == null || cardRequestApiDTO.getTitle().isBlank()) {
            throw new CustomIllegalArgumentException("Title is required");
        }
        Column column = columnRepository.findById(cardRequestApiDTO.getColumnId())
                .orElseThrow(() -> new CustomNotFoundException("Column not found"));
        User user = userRepository.findById(cardRequestApiDTO.getAssignedToId())
                .orElseThrow(() -> new CustomNotFoundException("User not found"));
        Card card = cardMapper.toEntity(cardRequestApiDTO);
        card.setColumn(column);
        card.setAssignedTo(user);
        Card saved = cardRepository.save(card);
        log.info("Card created: {}", saved.getId());
        return cardMapper.toResponse(saved);
    }

    public void deleteCardById(Long id) {
        log.info("Deleting card with id: {}", id);
        if (!cardRepository.existsById(id)) {
            throw new CustomNotFoundException("Card not found");
        }
        cardRepository.deleteById(id);
        log.info("Card deleted: {}", id);
    }

    public CardApiDTO getCardById(Long id) {
        log.info("Getting card with id: {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Card not found"));
        return cardMapper.toResponse(card);
    }

    public CardApiDTO updateCardById(Long id, UpdateCardRequestApiDTO cardRequestApiDTO) {
        log.info("Updating card with id: {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Card not found"));
        if (cardRequestApiDTO.getTitle() == null || cardRequestApiDTO.getTitle().isBlank()) {
            throw new CustomIllegalArgumentException("Title is required");
        }
        card.setTitle(cardRequestApiDTO.getTitle());
        card.setDescription(cardRequestApiDTO.getDescription());
        if (cardRequestApiDTO.getAssignedToId() != null) {
            User user = userRepository.findById(cardRequestApiDTO.getAssignedToId())
                    .orElseThrow(() -> new CustomNotFoundException("User not found"));
            card.setAssignedTo(user);
        }
        if (cardRequestApiDTO.getColumnId() != null) {
            Column column = columnRepository.findById(cardRequestApiDTO.getColumnId())
                    .orElseThrow(() -> new CustomNotFoundException("Column not found"));
            card.setColumn(column);
        }
        Card updated = cardRepository.save(card);
        log.info("Card updated: {}", updated.getId());
        return cardMapper.toResponse(updated);
    }
}
