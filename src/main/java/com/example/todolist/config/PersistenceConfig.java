package com.example.todolist.config;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.example.todolist.repository")
public class PersistenceConfig {

}
