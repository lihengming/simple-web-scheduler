package com.github.lihengming;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by 李恒名 on 2017/4/5.
 */
interface  TaskRepository extends CrudRepository<Task, Integer> {
    List<Task> findByStatus(int status);
}
