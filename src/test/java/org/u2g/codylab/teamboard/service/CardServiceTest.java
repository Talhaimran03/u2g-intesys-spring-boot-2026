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

    @Test
    void shouldCreateCardSuccessfully() {

        // Arrange
        CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO();
        dto.setTitle("Card");
        dto.setColumnId(1L);
        dto.setAssignedToId(2L);
        Column column = new Column();
        User user = new User();
        Card card = new Card();
        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(cardMapper.toEntity(any())).thenReturn(card);
        when(cardRepository.save(any())).thenReturn(card);
        when(cardMapper.toResponse(any())).thenReturn(new CardApiDTO());

        // Act
        CardApiDTO result = cardService.createCard(dto);

        // Assert
        assertNotNull(result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowWhenColumnNotFound() {

        // Arrange
        CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO();
        dto.setTitle("Card");
        dto.setColumnId(1L);
        dto.setAssignedToId(2L);
        when(columnRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.createCard(dto));
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        // Arrange
        CreateCardRequestApiDTO dto = new CreateCardRequestApiDTO();
        dto.setTitle("Card");
        dto.setColumnId(1L);
        dto.setAssignedToId(2L);
        when(columnRepository.findById(1L)).thenReturn(Optional.of(new Column()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.createCard(dto));
    }

    @Test
    void shouldMoveCardSuccessfully() {

        // Arrange
        Card card = new Card();
        card.setId(1L);
        card.setTitle("Title");
        Column target = new Column();
        target.setId(2L);
        target.setTitle("Done");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(columnRepository.findById(2L)).thenReturn(Optional.of(target));
        when(cardRepository.save(any())).thenReturn(card);

        // Act
        UpdateCardRequestApiDTO updateDto = new UpdateCardRequestApiDTO().columnId(2L).title("Title");
        cardService.updateCardById(1L, updateDto);

        // Assert
        assertEquals(target, card.getColumn());
    }

    @Test
    void shouldThrowWhenCardNotFound() {
        // Arrange
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        UpdateCardRequestApiDTO updateDto = new UpdateCardRequestApiDTO().columnId(2L).title("Title");
        assertThrows(CustomNotFoundException.class, () -> cardService.updateCardById(1L, updateDto));
    }

    @Test
    void shouldGetCardByIdSuccessfully() {

        // Arrange
        Card card = new Card();
        card.setId(1L);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toResponse(card)).thenReturn(new CardApiDTO());

        // Act
        CardApiDTO result = cardService.getCardById(1L);

        // Assert
        assertNotNull(result);
        verify(cardRepository).findById(1L);
    }

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
    void shouldThrowWhenDeletingNonExistingCard() {

        // Arrange
        when(cardRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.deleteCardById(1L));
    }
}
