package com.example.todolist.repository;

import com.example.todolist.model.ToDoEntry;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends JpaRepository<ToDoEntry, Long> {

    Collection<ToDoEntry> findAllByListId(Long listId);
}
