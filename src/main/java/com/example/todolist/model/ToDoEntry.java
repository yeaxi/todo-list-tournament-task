package com.example.todolist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * To-Do entry with id, list it is assigned to and up to 16k long description
 */
@Entity
@Table(name = "entries")
public class ToDoEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private ToDoList list;

    @NotNull
    @Size(max = 16000)
    private String description;

    public ToDoEntry(String description) {
        this.description = description;
    }

    public ToDoEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setList(ToDoList list) {
        this.list = list;
    }

    public ToDoList getList() {
        return list;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToDoEntry entry = (ToDoEntry) o;

        if (id != null ? !id.equals(entry.id) : entry.id != null) return false;
        return description != null ? description.equals(entry.description) : entry.description == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToDoEntry{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
