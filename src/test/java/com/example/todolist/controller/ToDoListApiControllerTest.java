package com.example.todolist.controller;

import com.example.todolist.model.ToDoEntry;
import com.example.todolist.model.ToDoList;
import com.example.todolist.repository.EntryRepository;
import com.example.todolist.repository.ListRepository;
import org.junit.After;
import org.junit.Before;
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
import java.util.List;

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

    @Before
    @After
    public void cleanUp() throws Exception {
        listRepository.deleteAll();
    }

    @Test
    public void getListsShouldReturnListsWithStatus200() throws Exception {
        ToDoList doList = new ToDoList("list");
        doList.addEntry(new ToDoEntry("descr"));
        doList = listRepository.save(doList);

        RequestEntity<Void> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI("/api"));
        ResponseEntity<Collection<ToDoList>> responseEntity = restTemplate
                .exchange(requestEntity, new ParameterizedTypeReference<Collection<ToDoList>>() {
                });

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Collection<ToDoList> body = responseEntity.getBody();
        assertEquals(1, body.size());
        assertEquals(doList, body.iterator().next());
    }

    @Test
    public void getListEntriesShouldReturnEntriesByListId() throws Exception {
        ToDoList doList = new ToDoList("list");
        ToDoEntry entry = new ToDoEntry("descr");
        doList.addEntry(entry);
        doList = listRepository.save(doList);

        RequestEntity<Void> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI("/api/" + doList.getId()));
        ResponseEntity<Collection<ToDoEntry>> responseEntity = restTemplate
                .exchange(requestEntity, new ParameterizedTypeReference<Collection<ToDoEntry>>() {
                });

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Collection<ToDoEntry> body = responseEntity.getBody();
        assertEquals(1, body.size());
        assertEquals(entry, body.iterator().next());
    }

    @Test
    public void getListEntriesByNonexistentIdShouldReturn404StatusCode() throws Exception {
        RequestEntity<Void> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI("/api/2"));

        ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void createListShouldSaveItToRepository() throws Exception {
        ToDoList doList = new ToDoList("list");

        RequestEntity<ToDoList> requestEntity = new RequestEntity<>(doList, HttpMethod.POST, new URI("/api/"));
        ResponseEntity<ToDoList> responseEntity = restTemplate
                .exchange(requestEntity, new ParameterizedTypeReference<ToDoList>() {
                });
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        List<ToDoList> lists = listRepository.findAll();

        assertEquals(1, lists.size());
        assertEquals(doList.getName(), lists.get(0).getName());
    }

    @Test
    public void createNotValidListShouldReturn400StatusCode() throws Exception {
        ToDoList doList = new ToDoList(getNotValidName());

        RequestEntity<ToDoList> requestEntity = new RequestEntity<>(doList, HttpMethod.POST, new URI("/api/"));
        ResponseEntity<ToDoList> responseEntity = restTemplate
                .exchange(requestEntity, new ParameterizedTypeReference<ToDoList>() {
                });
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        List<ToDoList> lists = listRepository.findAll();
        assertEquals(0, lists.size());
    }

    private String getNotValidName() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 257; i++) {
            builder.append(i);
        }
        return builder.toString();
    }


    @Test
    public void createTodoEntryShouldReturn201StatusCode() throws Exception {
        ToDoList doList = listRepository.save(new ToDoList("list"));
        ToDoEntry entry = new ToDoEntry("descr");

        RequestEntity<ToDoEntry> requestEntity = new RequestEntity<>(entry, HttpMethod.POST, new URI("/api/" + doList.getId()));
        ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ToDoList actualList = listRepository.findOne(doList.getId());
        assertEquals(1, entryRepository.count());
        assertEquals(1, actualList.getEntries().size());
        assertEquals(entry.getDescription(), actualList.getEntries().iterator().next().getDescription());
    }

    @Test
    public void createTodoEntryWithNotValidDescriptionShouldReturn400StatusCode() throws Exception {
        ToDoList doList = listRepository.save(new ToDoList("list"));
        ToDoEntry entry = new ToDoEntry(getNotValidDescription());

        RequestEntity<ToDoEntry> requestEntity = new RequestEntity<>(entry, HttpMethod.POST, new URI("/api/" + doList.getId()));
        ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ToDoList actualList = listRepository.findOne(doList.getId());
        assertEquals(0, actualList.getEntries().size());
        assertEquals(0, entryRepository.count());
    }

    private String getNotValidDescription() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16000; i++) {
            builder.append(i);
        }
        return builder.toString();

    }

    @Test
    public void createTodoEntryByNonexistentListIdShouldReturn404StatusCode() throws Exception {
        ToDoEntry entry = new ToDoEntry("descr");

        RequestEntity<ToDoEntry> requestEntity = new RequestEntity<>(entry, HttpMethod.POST, new URI("/api/100"));
        ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(0, listRepository.count());
        assertEquals(0, entryRepository.count());
    }

    @Test
    public void deleteListByIdShouldReturnOK() throws Exception {
        ToDoList doList = listRepository.save(new ToDoList("list"));

        RequestEntity<Void> requestEntity = new RequestEntity<>(HttpMethod.DELETE, new URI("/api/" + doList.getId()));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        System.out.println(requestEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertEquals(0, listRepository.count());
    }

    @Test
    public void deleteListByNonexistentIdShouldReturn404StatusCode() throws Exception {
        RequestEntity<Void> requestEntity = new RequestEntity<>(HttpMethod.DELETE, new URI("/api/100"));
        ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

}