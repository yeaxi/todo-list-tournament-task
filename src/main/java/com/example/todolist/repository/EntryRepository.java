package com.example.todolist.repository;

import com.example.todolist.model.ToDoEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface EntryRepository extends JpaRepository<ToDoEntry, Long> {

    Collection<ToDoEntry> findAllByListId(Long listId);
}
