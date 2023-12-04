package org.store.structure.service.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.store.structure.dto.category.CategoryRequestDto;
import org.store.structure.dto.category.CategoryResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.CategoryMapper;
import org.store.structure.model.Category;
import org.store.structure.repository.category.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private Category category;

    @BeforeEach
    void setUp() {
        category = initCategory();
    }

    @Test
    void findAll_WithValidPageable_ReturnsAllCategories() {
        CategoryResponseDto responseDto = initCategoryResponseDto(category);
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        List<CategoryResponseDto> responseDtoList = categoryService.findAll(pageable);

        int expectedSize = 1;
        assertEquals(expectedSize, responseDtoList.size());
        assertEquals(responseDto, responseDtoList.get(0));
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void getById_WithValidCategoryId_ReturnsCategory() {
        CategoryResponseDto expected = initCategoryResponseDto(category);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.getById(category.getId());

        assertEquals(expected, actual);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void getById_WithInvalidCategoryId_ThrowsException() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(category.getId()));

        String expected = "Can't find Category by id: " + category.getId();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void save_WithValidRequestDto_ReturnsResponseDto() {
        CategoryRequestDto requestDto = initCategoryRequestDto(category);
        CategoryResponseDto expected = initCategoryResponseDto(category);

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryResponseDto actual = categoryService.save(requestDto);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void update_WithValidIdAndRequestDto_ReturnsResponseDto() {
        CategoryRequestDto requestDto = initCategoryRequestDto(category);
        CategoryResponseDto expected = initCategoryResponseDto(category);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.update(category.getId(), requestDto);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void update_WithInvalidId_ThrowsException() {
        CategoryRequestDto requestDto = initCategoryRequestDto(category);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(category.getId(), requestDto)
        );

        String expected = "Can't find category by this categoryId: " + category.getId();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(categoryRepository);
    }

    private CategoryRequestDto initCategoryRequestDto(Category category) {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName(category.getName());
        requestDto.setDescription(category.getDescription());
        return requestDto;
    }

    private CategoryResponseDto initCategoryResponseDto(Category category) {
        CategoryResponseDto responseDto = new CategoryResponseDto();
        responseDto.setDescription(category.getDescription());
        responseDto.setId(category.getId());
        responseDto.setName(category.getName());
        return responseDto;
    }

    private Category initCategory() {
        Category category = new Category();
        category.setName("Biography");
        category.setDescription("Personal stories");
        category.setId(1L);
        return category;
    }
}