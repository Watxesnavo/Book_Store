package org.store.structure.service;

import java.util.List;
import org.store.structure.model.Book;

public interface BookService {
    Book save(Book book);

    List findAll();
}
