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
        Column column = new Column().setId(1L).setTitle("To Do");
        User user = new User().setId(1L).setUsername("malaika");
        Card card = new Card().setTitle("My Card");

        CreateCardRequestApiDTO request = new CreateCardRequestApiDTO()
                .title("My Card")
                .columnId(1L)
                .assignedToId(1L);

        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardMapper.toEntity(any(CreateCardRequestApiDTO.class))).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(any(Card.class)))
                .thenReturn(new CardApiDTO().id(1L).title("My Card"));

        // Act
        CardApiDTO result = cardService.createCard(request);

        // Assert
        assertNotNull(result);
        assertEquals("My Card", result.getTitle());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowExceptionWhenColumnNotFoundOnCreate() {
        // Arrange
        CreateCardRequestApiDTO request = new CreateCardRequestApiDTO()
                .columnId(99L)
                .assignedToId(1L);

        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.createCard(request));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCreate() {
        // Arrange
        Column column = new Column().setId(1L).setTitle("To Do");
        CreateCardRequestApiDTO request = new CreateCardRequestApiDTO()
                .columnId(1L)
                .assignedToId(99L);

        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.createCard(request));
        verify(cardRepository, never()).save(any());
    }



    @Test
    void shouldGetCardByIdSuccessfully() {
        // Arrange
        Card card = new Card().setId(1L).setTitle("My Card");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toResponse(card))
                .thenReturn(new CardApiDTO().id(1L).title("My Card"));

        // Act
        CardApiDTO result = cardService.getCardById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenCardNotFoundById() {
        // Arrange
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.getCardById(99L));
    }



    @Test
    void shouldDeleteCardSuccessfully() {
        // Arrange
        when(cardRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> cardService.deleteCardById(1L));
        verify(cardRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCardNotFoundOnDelete() {
        // Arrange
        when(cardRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.deleteCardById(99L));
        verify(cardRepository, never()).deleteById(any());
    }



    @Test
    void shouldUpdateCardSuccessfully() {
        // Arrange
        Card card = new Card().setId(1L).setTitle("Old Title");
        User user = new User().setId(1L).setUsername("malaika");
        Column column = new Column().setId(1L).setTitle("To Do");

        UpdateCardRequestApiDTO request = new UpdateCardRequestApiDTO()
                .title("Updated Title")
                .description("Updated Desc")
                .assignedToId(1L)
                .columnId(1L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(any(Card.class)))
                .thenReturn(new CardApiDTO().id(1L).title("Updated Title"));

        // Act
        CardApiDTO result = cardService.updateCardById(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowExceptionWhenCardNotFoundOnUpdate() {
        // Arrange
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class,
                () -> cardService.updateCardById(99L, new UpdateCardRequestApiDTO()));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnUpdate() {
        // Arrange
        Card card = new Card().setId(1L).setTitle("My Card");
        UpdateCardRequestApiDTO request = new UpdateCardRequestApiDTO().assignedToId(99L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.updateCardById(1L, request));
    }

    @Test
    void shouldThrowExceptionWhenColumnNotFoundOnUpdate() {
        // Arrange
        Card card = new Card().setId(1L).setTitle("My Card");
        UpdateCardRequestApiDTO request = new UpdateCardRequestApiDTO().columnId(99L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> cardService.updateCardById(1L, request));
    }
    @Test
    void shouldUpdateOnlyColumnIdWhenTitleIsNull() {
        // Arrange
        Card card = new Card().setId(1L).setTitle("My Card");
        Column column = new Column().setId(2L).setTitle("Done");

        UpdateCardRequestApiDTO request = new UpdateCardRequestApiDTO()
                .columnId(2L); // seulement columnId, pas de title

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(columnRepository.findById(2L)).thenReturn(Optional.of(column));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(any(Card.class)))
                .thenReturn(new CardApiDTO().id(1L).title("My Card"));

        // Act
        CardApiDTO result = cardService.updateCardById(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("My Card", result.getTitle());
        verify(cardRepository).save(any(Card.class));
        verify(userRepository, never()).findById(any());
    }
}
