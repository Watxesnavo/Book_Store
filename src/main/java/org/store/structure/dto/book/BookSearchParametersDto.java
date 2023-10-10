package org.store.structure.dto.book;

public record BookSearchParametersDto(String[] authors,
                                      String[] titles,
                                      String[] prices,
                                      String[] coverImages) {

}
