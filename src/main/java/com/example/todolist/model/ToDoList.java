package com.example.todolist.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * To-Do list with name (up to 255 chars) and a set of entries
 */
@Entity
@Table(name = "lists")
public class ToDoList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(max = 255)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "list")
    @Fetch(value = FetchMode.JOIN)
    private Set<ToDoEntry> entries = new HashSet<>();

    public ToDoList(String name) {
        this.name = name;
    }

    public ToDoList() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ToDoEntry> getEntries() {
        return entries;
    }

    public void addEntry(ToDoEntry entry) {
        entry.setList(this);
        entries.add(entry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDoList toDoList = (ToDoList) o;
        return Objects.equals(id, toDoList.id) &&
                Objects.equals(name, toDoList.name) &&
                Objects.equals(new HashSet<>(entries), new HashSet<>(toDoList.entries));
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, entries);
    }

    @Override
    public String toString() {
        return "ToDoList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", entries=" + entries +
                '}';
    }
}
