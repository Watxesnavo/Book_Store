package org.store.structure.dto.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateBookRequestDto {
    @NotNull
    @Size(min = 1)
    private String title;
    @NotNull
    @NotEmpty
    private String author;
    @NotNull
    @NotEmpty
    private String isbn;
    @NotNull
    private BigDecimal price;
    private String description;
    private String coverImage;
    @NotNull
    @NotEmpty
    private Set<Long> categoryIds;
    private boolean deleted;
}
