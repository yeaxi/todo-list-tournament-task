package com.example.todolist.controller;

import com.example.todolist.exceptions.NotFoundException;
import com.example.todolist.model.ToDoEntry;
import com.example.todolist.model.ToDoList;
import com.example.todolist.repository.EntryRepository;
import com.example.todolist.repository.ListRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("api")
public class ToDoListApiController {

    private final ListRepository listRepository;
    private final EntryRepository entryRepository;

    public ToDoListApiController(ListRepository listRepository,
                                 EntryRepository entryRepository) {
        this.listRepository = listRepository;
        this.entryRepository = entryRepository;
    }

    @GetMapping
    public Collection<ToDoList> getLists() {
        return listRepository.findAll();
    }

    @GetMapping("/{listId}")
    public Collection<ToDoEntry> getListEntries(@PathVariable Long listId) {
        Collection<ToDoEntry> entries = entryRepository.findAllByListId(listId);
        if (entries.isEmpty()) {
            throw new NotFoundException();
        }
        return entries;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ToDoList createList(@RequestBody @Valid ToDoList list) {
        return listRepository.save(list);
    }

    @PostMapping("/{listId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEntry(@PathVariable Long listId, @RequestBody @Valid ToDoEntry entry) {
        ToDoList list = ensureExists(listRepository.findOne(listId));
        entry.setList(list);
        entryRepository.save(entry);
    }

    @DeleteMapping("/{listid}")
    public void deleteList(@PathVariable("listid") Long listId) {
        listRepository.delete(listId);
    }

    @DeleteMapping("/{entryId}/{listId}")
    public void deleteEntry(@PathVariable("listId") Long listId, @PathVariable("entryId") Long entryId) {
        ToDoList list = ensureExists(listRepository.findOne(listId));
        ToDoEntry entry = ensureExists(entryRepository.findOne(entryId));
        if (entry.getList() != list) {
            throw new IllegalArgumentException();
        }
        list.removeEntry(entry);
        entryRepository.delete(entry);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFound() {
        // No-op, return empty 404
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void badRequest() {
        // No-op, return empty 400
    }

    private static <T> T ensureExists(T object) {
        if (object == null) {
            throw new NotFoundException();
        }
        return object;
    }
}
