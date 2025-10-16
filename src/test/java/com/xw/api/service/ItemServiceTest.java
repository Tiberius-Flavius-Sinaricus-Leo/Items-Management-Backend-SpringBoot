package com.xw.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.xw.api.dto.ItemRequest;
import com.xw.api.dto.ItemResponse;
import com.xw.api.entity.ItemEntity;
import com.xw.api.repository.ItemRepository;


@ExtendWith(MockitoExtension.class)
@DisplayName("Item Service Tests")
public class ItemServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private ItemService itemService;

  private ItemEntity sampleEntity;

  private ItemRequest sampleRequest;

  @BeforeEach
  void setUp() {
    Timestamp now = Timestamp.from(Instant.now());

    sampleEntity = ItemEntity.builder()
      .id(1L)
      .itemId("item-123")
      .name("Sample Item")
      .description("This is a sample item.")
      .price(BigDecimal.valueOf(19.99))
      .createdAt(now)
      .updatedAt(now)
      .build();

    sampleRequest = ItemRequest.builder()
      .name("Sample Item")
      .description("This is a sample item.")
      .price(BigDecimal.valueOf(19.99))
      .build();
  }

  @Nested
  @DisplayName("Create Items Tests")
  class CreateItemsTests {

    @Test
    @DisplayName("Create Item Successfully")
    void createItemSuccessfully() {
      when(itemRepository.save(any(ItemEntity.class))).thenReturn(sampleEntity);

      ItemResponse createdItem = itemService.createItem(sampleRequest);

      assertNotNull(createdItem);
      assertEquals(sampleEntity.getName(), createdItem.getName());
      assertEquals(sampleEntity.getDescription(), createdItem.getDescription());
      assertEquals(sampleEntity.getPrice(), createdItem.getPrice());

      verify(itemRepository).save(any(ItemEntity.class));
    }
  }

  @Nested
  @DisplayName("Get Item By ID Tests")
  class GetItemByIdTests {

    @Test
    @DisplayName("Get Item By ID Successfully")
    void getItemByIdSuccessfully() {
      when(itemRepository.findByItemId("item-123")).thenReturn(Optional.of(sampleEntity));

      Optional<ItemResponse> fetchedItem = itemService.getItemById("item-123");

      assertTrue(fetchedItem.isPresent());
      assertEquals(sampleEntity.getName(), fetchedItem.get().getName());
      assertEquals(sampleEntity.getDescription(), fetchedItem.get().getDescription());
      assertEquals(sampleEntity.getPrice(), fetchedItem.get().getPrice());
      verify(itemRepository).findByItemId("item-123");
    }
  }

  @Nested
  @DisplayName("Get All Items Tests")
  class GetAllItemsTests {

    @Test
    @DisplayName("Should return all items")
    void shouldReturnAllItems() {
      
      ItemEntity anotherEntity = ItemEntity.builder()
        .id(2L)
        .itemId("item-456")
        .name("Another Item")
        .description("This is another item.")
        .price(BigDecimal.valueOf(29.99))
        .createdAt(Timestamp.from(Instant.now()))
        .updatedAt(Timestamp.from(Instant.now()))
        .build();

      List<ItemEntity> items = Arrays.asList(sampleEntity, anotherEntity);

      when(itemRepository.findAll()).thenReturn(items);

      List<ItemResponse> itemResponses = itemService.getAllItems();

      assertNotNull(itemResponses);
      assertEquals(2, itemResponses.size());
      assertEquals(sampleEntity.getName(), itemResponses.get(0).getName());
      assertEquals(anotherEntity.getName(), itemResponses.get(1).getName());
    }
  }
}
