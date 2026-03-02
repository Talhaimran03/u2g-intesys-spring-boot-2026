package org.u2g.codylab.teamboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.u2g.codylab.teamboard.api.CardApi;
import org.u2g.codylab.teamboard.dto.CardApiDTO;
import org.u2g.codylab.teamboard.dto.CreateCardRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateCardRequestApiDTO;
import org.u2g.codylab.teamboard.service.CardService;

@RestController
public class CardController implements CardApi {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<CardApiDTO> createCard(CreateCardRequestApiDTO createCardRequestApiDTO) {
        CardApiDTO response = cardService.createCard(createCardRequestApiDTO);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteCardById(Long id) {
        cardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CardApiDTO> getCardById(Long id) {
        CardApiDTO response = cardService.getCardById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CardApiDTO> updateCard(Long id, UpdateCardRequestApiDTO updateCardRequestApiDTO) {
        CardApiDTO response = cardService.updateCardById(id, updateCardRequestApiDTO);
        return ResponseEntity.ok(response);
    }
}