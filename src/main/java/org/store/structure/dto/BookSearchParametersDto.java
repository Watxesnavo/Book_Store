package org.store.structure.dto;

public record BookSearchParametersDto(String[] authors,
                                      String[] titles,
                                      String[] prices,
                                      String[] coverImages) {

}
