package com.example.todolist.controller;

import com.example.todolist.model.ToDoEntry;
import com.example.todolist.model.ToDoList;
import com.example.todolist.repository.EntryRepository;
import com.example.todolist.repository.ListRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ToDoListApiControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ListRepository listRepository;
    @Autowired
    private EntryRepository entryRepository;

    @Test
    public void getListsShouldReturnListsWithStatus200() throws Exception {
        ToDoList doList = new ToDoList("list");
        doList.addEntry(new ToDoEntry("descr"));
        doList = listRepository.save(doList);

        RequestEntity<Void> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI("/api"));
        ResponseEntity<Collection<ToDoList>> responseEntity = restTemplate
                .exchange(requestEntity, new ParameterizedTypeReference<Collection<ToDoList>>() {
                });

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Collection<ToDoList> body = responseEntity.getBody();
        assertEquals(1, body.size());
        assertEquals(doList, body.iterator().next());
    }


}