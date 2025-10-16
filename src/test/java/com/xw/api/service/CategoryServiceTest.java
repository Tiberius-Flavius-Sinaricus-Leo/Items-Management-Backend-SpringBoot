package com.xw.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.xw.api.dto.CategoryRequest;
import com.xw.api.dto.CategoryResponse;
import com.xw.api.entity.CategoryEntity;
import com.xw.api.exception.CategoryNotEmptyException;
import com.xw.api.repository.CategoryRepository;
import com.xw.api.repository.ItemRepository;
import com.xw.api.service.implementation.CategoryServiceImplementation;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service Tests")
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private CategoryServiceImplementation categoryService;

  private CategoryEntity sampleEntity;
  private CategoryRequest sampleRequest;

  @BeforeEach
  void setUp() {
    Timestamp now = Timestamp.from(Instant.now());

    sampleEntity = CategoryEntity.builder()
        .id(1L)
        .categoryId("test-uuid-123")
        .name("Electronics")
        .description("Electronic devices and accessories")
        .bgColor("#FF5722")
        .createdAt(now)
        .updatedAt(now)
        .build();

    sampleRequest = CategoryRequest.builder()
        .name("Electronics")
        .description("Electronic devices and accessories")
        .bgColor("#FF5722")
        .build();
  }

  @Nested
  @DisplayName("Create Category Tests")
  class CreateCategoryTests {

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() {
      // Given
      when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(sampleEntity);
      when(itemRepository.countByCategory(1L)).thenReturn(5);

      // When
      CategoryResponse result = categoryService.createCategory(sampleRequest);

      // Then
      assertNotNull(result);
      assertEquals(sampleEntity.getName(), result.getName());
      assertEquals(sampleEntity.getDescription(), result.getDescription());
      assertEquals(sampleEntity.getBgColor(), result.getBgColor());
      assertEquals(5, result.getItemsCount());

      verify(categoryRepository).save(any(CategoryEntity.class));
      verify(itemRepository).countByCategory(1L);
    }
  }

  @Nested
  @DisplayName("Get All Categories Tests")
  class GetAllCategoriesTests {

    @Test
    @DisplayName("Should return all categories")
    void shouldReturnAllCategories() {
      // Given
      CategoryEntity entity2 = CategoryEntity.builder()
          .id(2L)
          .categoryId("test-uuid-456")
          .name("Clothing")
          .description("Apparel and accessories")
          .bgColor("#2196F3")
          .createdAt(Timestamp.from(Instant.now()))
          .updatedAt(Timestamp.from(Instant.now()))
          .build();

      List<CategoryEntity> entities = Arrays.asList(sampleEntity, entity2);

      when(categoryRepository.findAll()).thenReturn(entities);
      when(itemRepository.countByCategory(1L)).thenReturn(5);
      when(itemRepository.countByCategory(2L)).thenReturn(3);

      // When
      List<CategoryResponse> result = categoryService.getAllCategories();

      // Then
      assertNotNull(result);
      assertEquals(2, result.size());

      CategoryResponse firstCategory = result.get(0);
      assertEquals("Electronics", firstCategory.getName());
      assertEquals(5, firstCategory.getItemsCount());

      CategoryResponse secondCategory = result.get(1);
      assertEquals("Clothing", secondCategory.getName());
      assertEquals(3, secondCategory.getItemsCount());

      verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void shouldReturnEmptyListWhenNoCategoriesExist() {
      // Given
      when(categoryRepository.findAll()).thenReturn(Arrays.asList());

      // When
      List<CategoryResponse> result = categoryService.getAllCategories();

      // Then
      assertNotNull(result);
      assertTrue(result.isEmpty());
      verify(categoryRepository).findAll();
    }
  }

  @Nested
  @DisplayName("Get Category By ID Tests")
  class GetCategoryByIdTests {

    @Test
    @DisplayName("Should return category when found")
    void shouldReturnCategoryWhenFound() {
      // Given
      when(categoryRepository.findByCategoryId("test-uuid-123")).thenReturn(Optional.of(sampleEntity));
      when(itemRepository.countByCategory(1L)).thenReturn(5);

      // When
      Optional<CategoryResponse> result = categoryService.getCategoryById("test-uuid-123");

      // Then
      assertTrue(result.isPresent());
      CategoryResponse category = result.get();
      assertEquals("Electronics", category.getName());
      assertEquals("Electronic devices and accessories", category.getDescription());
      assertEquals("#FF5722", category.getBgColor());
      assertEquals(5, category.getItemsCount());

      verify(categoryRepository).findByCategoryId("test-uuid-123");
      verify(itemRepository).countByCategory(1L);
    }

    @Test
    @DisplayName("Should return empty when category not found")
    void shouldReturnEmptyWhenCategoryNotFound() {
      // Given
      when(categoryRepository.findByCategoryId("non-existent-id")).thenReturn(Optional.empty());

      // When
      Optional<CategoryResponse> result = categoryService.getCategoryById("non-existent-id");

      // Then
      assertFalse(result.isPresent());

      verify(categoryRepository).findByCategoryId("non-existent-id");
      verify(itemRepository, never()).countByCategory(anyLong());
    }
  }

  @Nested
  @DisplayName("Update Category Tests")
  class UpdateCategoryTests {

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategorySuccessfully() {
      // Given
      CategoryRequest updateRequest = CategoryRequest.builder()
          .name("Updated Electronics")
          .description("Updated electronic devices")
          .bgColor("#FF9800")
          .build();

      CategoryEntity updatedEntity = CategoryEntity.builder()
          .id(1L)
          .categoryId("test-uuid-123")
          .name("Updated Electronics")
          .description("Updated electronic devices")
          .bgColor("#FF9800")
          .createdAt(sampleEntity.getCreatedAt())
          .updatedAt(Timestamp.from(Instant.now()))
          .build();

      when(categoryRepository.findByCategoryId("test-uuid-123")).thenReturn(Optional.of(sampleEntity));
      when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(updatedEntity);
      when(itemRepository.countByCategory(1L)).thenReturn(5);

      // When
      CategoryResponse result = categoryService.updateCategory("test-uuid-123", updateRequest);

      // Then
      assertNotNull(result);
      assertEquals("Updated Electronics", result.getName());
      assertEquals("Updated electronic devices", result.getDescription());
      assertEquals("#FF9800", result.getBgColor());
      assertEquals(5, result.getItemsCount());

      verify(categoryRepository).findByCategoryId("test-uuid-123");
      verify(categoryRepository).save(any(CategoryEntity.class));
      verify(itemRepository).countByCategory(1L);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent category")
    void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
      // Given
      CategoryRequest updateRequest = CategoryRequest.builder()
          .name("Updated Name")
          .build();

      when(categoryRepository.findByCategoryId("non-existent-id")).thenReturn(Optional.empty());

      // When & Then
      assertThrows(RuntimeException.class, () -> {
        categoryService.updateCategory("non-existent-id", updateRequest);
      });

      verify(categoryRepository).findByCategoryId("non-existent-id");
      verify(categoryRepository, never()).save(any(CategoryEntity.class));
    }
  }

  @Nested
  @DisplayName("Delete Category Tests")
  class DeleteCategoryTests {

    @Test
    @DisplayName("Should delete category successfully when no items exist")
    void shouldDeleteCategorySuccessfully() {
      // Given
      when(categoryRepository.findByCategoryId("test-uuid-123")).thenReturn(Optional.of(sampleEntity));
      when(itemRepository.countByCategory(1L)).thenReturn(0);

      // When
      categoryService.deleteCategory("test-uuid-123");

      // Then
      verify(categoryRepository).findByCategoryId("test-uuid-123");
      verify(itemRepository).countByCategory(1L);
      verify(categoryRepository).delete(sampleEntity);
    }

    @Test
    @DisplayName("Should throw exception when deleting category with existing items")
    void shouldThrowExceptionWhenDeletingCategoryWithItems() {
      // Given
      when(categoryRepository.findByCategoryId("test-uuid-123")).thenReturn(Optional.of(sampleEntity));
      when(itemRepository.countByCategory(1L)).thenReturn(5);

      // When & Then
      CategoryNotEmptyException exception = assertThrows(CategoryNotEmptyException.class, () -> {
        categoryService.deleteCategory("test-uuid-123");
      });

      // Verify the exception message
      assertEquals("Cannot delete category with existing items. Please remove all items first.", 
                   exception.getMessage());

      // Verify the correct methods were called
      verify(categoryRepository).findByCategoryId("test-uuid-123");
      verify(itemRepository).countByCategory(1L);
      verify(categoryRepository, never()).delete(any(CategoryEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent category")
    void shouldThrowExceptionWhenDeletingNonExistentCategory() {
      // Given
      when(categoryRepository.findByCategoryId("non-existent-id")).thenReturn(Optional.empty());

      // When & Then
      assertThrows(RuntimeException.class, () -> {
        categoryService.deleteCategory("non-existent-id");
      });

      verify(categoryRepository).findByCategoryId("non-existent-id");
      verify(categoryRepository, never()).delete(any(CategoryEntity.class));
    }
  }
}