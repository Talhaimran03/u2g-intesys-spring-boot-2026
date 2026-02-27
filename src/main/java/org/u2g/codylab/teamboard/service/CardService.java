package org.u2g.codylab.teamboard.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.u2g.codylab.teamboard.dto.CardRequestApiDTO;
import org.u2g.codylab.teamboard.dto.CardResponseApiDTO;
import org.u2g.codylab.teamboard.entity.Card;
import org.u2g.codylab.teamboard.entity.Column;
import org.u2g.codylab.teamboard.entity.Project;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.mapper.CardMapper;
import org.u2g.codylab.teamboard.repository.CardRepository;
import org.u2g.codylab.teamboard.repository.ColumnRepository;
import org.u2g.codylab.teamboard.repository.UserRepository;

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

    public CardResponseApiDTO createCard(CardRequestApiDTO cardRequestApiDTO) {
        Column project = columnRepository.findById(cardRequestApiDTO.getColumnId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User user = userRepository.findById(cardRequestApiDTO.getAssignedTo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Card card = cardMapper.toEntity(cardRequestApiDTO);
        card.setColumn(project);
        card.setAssignedTo(user);
        Card saved = cardRepository.save(card);
        return cardMapper.toResponse(saved);
    }

    public Void deleteCardById(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        cardRepository.deleteById(id);
        return null;
    }
}
