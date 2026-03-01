package org.u2g.codylab.teamboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.u2g.codylab.teamboard.api.CardApi;
import org.u2g.codylab.teamboard.dto.CardRequestApiDTO;
import org.u2g.codylab.teamboard.dto.CardResponseApiDTO;
import org.u2g.codylab.teamboard.service.CardService;

@RestController
public class CardController implements CardApi {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<CardResponseApiDTO> createCard(CardRequestApiDTO cardRequestApiDTO) {
        return ResponseEntity.ok(cardService.createCard(cardRequestApiDTO));
    }

    @Override
    public ResponseEntity<Void> deleteCardById(Long id) {
        return ResponseEntity.ok(cardService.deleteCardById(id));
    }

    @Override
    public ResponseEntity<CardResponseApiDTO> getCardById(Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @Override
    public ResponseEntity<CardResponseApiDTO> updateCardById(Long id, CardRequestApiDTO cardRequestApiDTO) {
        return ResponseEntity.ok(cardService.updateCardById(id, cardRequestApiDTO));
    }
}