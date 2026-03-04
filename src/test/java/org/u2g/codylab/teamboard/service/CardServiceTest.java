package org.u2g.codylab.teamboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.u2g.codylab.teamboard.dto.CardApiDTO;
import org.u2g.codylab.teamboard.dto.CreateCardRequestApiDTO;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    // ─── CREATE ──────────────────────────────────────────────

    @Test
    void shouldCreateCardSuccessfully() {
        // Arrange
        CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                .title("My Card")
                .columnId(1L)
                .assignedToId(1L);

        Column column = new Column().setId(1L).setTitle("TO DO");
        User user = new User().setId(1L).setUsername("joel");
        Card card = new Card();
        CardApiDTO responseDto = new CardApiDTO().id(1L).title("My Card");

        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardMapper.toEntity(any(CreateCardRequestApiDTO.class))).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(any(Card.class))).thenReturn(responseDto);

        // Act
        CardApiDTO result = cardService.createCard(dto);

        // Assert
        assertNotNull(result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowWhenTitleIsBlankOnCreate() {
        // Arrange
        CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                .title("")
                .columnId(1L);

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> cardService.createCard(dto));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenColumnNotFoundOnCreate() {
        // Arrange
        CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                .title("My Card")
                .columnId(99L);

        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.createCard(dto));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        // Arrange
        CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO()
                .title("My Card")
                .columnId(1L)
                .assignedToId(99L);

        when(columnRepository.findById(1L)).thenReturn(Optional.of(new Column()));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.createCard(dto));
        verify(cardRepository, never()).save(any());
    }

    // ─── GET BY ID ───────────────────────────────────────────

    @Test
    void shouldGetCardByIdSuccessfully() {
        // Arrange
        Card card = new Card();
        CardApiDTO dto = new CardApiDTO().id(1L).title("My Card");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toResponse(card)).thenReturn(dto);

        // Act
        CardApiDTO result = cardService.getCardById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenCardNotFound() {
        // Arrange
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.getCardById(99L));
    }

    // ─── UPDATE / MOVE CARD ──────────────────────────────────

    @Test
    void shouldMoveCardSuccessfully() {
        // Arrange
        Column newColumn = new Column().setId(2L).setTitle("IN PROGRESS");
        Card card = new Card();

        UpdateCardRequestApiDTO dto = new UpdateCardRequestApiDTO()
                .title("Updated Title")
                .columnId(2L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(columnRepository.findById(2L)).thenReturn(Optional.of(newColumn));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(any(Card.class))).thenReturn(new CardApiDTO());

        // Act
        cardService.updateCardById(1L, dto);

        // Assert
        assertEquals(newColumn, card.getColumn());
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowWhenCardNotFoundOnMove() {
        // Arrange
        UpdateCardRequestApiDTO dto = new UpdateCardRequestApiDTO()
                .title("Title")
                .columnId(2L);

        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.updateCardById(99L, dto));
    }

    @Test
    void shouldThrowWhenTargetColumnNotFoundOnMove() {
        // Arrange
        Card card = new Card();
        UpdateCardRequestApiDTO dto = new UpdateCardRequestApiDTO()
                .title("Title")
                .columnId(99L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.updateCardById(1L, dto));
    }

    @Test
    void shouldThrowWhenTitleIsBlankOnUpdate() {
        // Arrange
        Card card = new Card();
        UpdateCardRequestApiDTO dto = new UpdateCardRequestApiDTO()
                .title("");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> cardService.updateCardById(1L, dto));
        verify(cardRepository, never()).save(any());
    }

    // ─── DELETE ──────────────────────────────────────────────

    @Test
    void shouldDeleteCardSuccessfully() {
        // Arrange
        when(cardRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cardRepository).deleteById(1L);

        // Act & Assert
        assertDoesNotThrow(() -> cardService.deleteCardById(1L));
        verify(cardRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenCardToDeleteNotFound() {
        // Arrange
        when(cardRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.deleteCardById(99L));
        verify(cardRepository, never()).deleteById(any());
    }
}