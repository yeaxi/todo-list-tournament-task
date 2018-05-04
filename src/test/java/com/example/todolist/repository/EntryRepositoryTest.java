package com.example.todolist.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EntryRepositoryTest {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private ListRepository listRepository;

    @Test
    public void findAllByListId() {

    }
}