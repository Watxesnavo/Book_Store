package org.store.structure.service.category;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.category.CategoryRequestDto;
import org.store.structure.dto.category.CategoryResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.CategoryMapper;
import org.store.structure.model.Category;
import org.store.structure.repository.category.CategoryRepository;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        return categoryMapper.toDto(
                categoryRepository.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Can't find Category by id: " + id))
        );
    }

    @Override
    public CategoryResponseDto save(CategoryRequestDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponseDto update(Long categoryId, CategoryRequestDto categoryDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find category by this categoryId: " + categoryId
                )
        );
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
