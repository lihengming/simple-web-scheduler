package com.github.lihengming;

import com.github.kevinsawicki.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by 李恒名 on 2017/4/17.
 */
@RestController
public class TaskController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Integer, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();
    @Autowired
    private TaskRepository repository;
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @PostConstruct
    void init() {
        Iterable<Task> tasks = repository.findByStatus(1);
        logger.info("Initializing Tasks");
        for (final Task task : tasks) {
            ScheduledFuture<?> future = schedule(task);
            Integer id = task.getId();
            futureMap.put(id, future);
            logger.info("Initializing Task,Id：{}", id);
        }
        logger.info("Initialized");
    }


    @PostMapping("/add")
    Task add(Task task) {
        task.setCreateDate(new Date());
        task.setStatus(2);
        return repository.save(task);
    }

    @PostMapping("/start")
    boolean start(Integer id) {
        try {
            Task task = repository.findOne(id);
            ScheduledFuture<?> future = schedule(task);
            task.setCreateDate(new Date());
            task.setStatus(1);
            repository.save(task);
            futureMap.put(id, future);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @PostMapping("/stop")
    boolean stop(Integer id) {
        try {
            ScheduledFuture<?> future = futureMap.remove(id);
            if (!future.isCancelled()) {
                future.cancel(true);
            }
            Task task = repository.findOne(id);
            task.setStatus(2);
            repository.save(task);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @PostMapping("/list")
    Iterable<Task> list() {
        return repository.findAll();
    }

    @PostMapping("/delete")
    boolean delete(Integer id) {
        try {
            repository.delete(id);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    private ScheduledFuture<?> schedule(final Task task) {
        Trigger trigger;
        if (Task.TYPE_CRON == task.getType()) {
            trigger = new CronTrigger(task.getExpression());
        } else {
            Long period = Long.valueOf(task.getExpression());
            PeriodicTrigger periodicTrigger = new PeriodicTrigger(period, TimeUnit.SECONDS);
            periodicTrigger.setFixedRate(true);
            trigger = periodicTrigger;
        }
        return threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                String url = task.getApi();
                try {
                    HttpRequest httpRequest = HttpRequest.post(url);
                    logger.info("task api:{} invoke finish , api result:{}", task.getApi(), httpRequest.body());
                } catch (Exception e) {
                    logger.error("task api invoke fail , detail:{}", task);
                }
            }
        }, trigger);
    }
}
