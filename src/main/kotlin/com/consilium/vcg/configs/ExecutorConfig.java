package com.consilium.vcg.configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfig {

    @Value("${task.recognize.core.size:4}")
    Integer taskCore;


    @Value("${task.report.core.size:4}")
    Integer reportCore;

    @Value("${task.alive.seconds:600}")
    Integer taskKeepAliveSeconds;

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(taskCore);
        taskExecutor.setMaxPoolSize(300);
        taskExecutor.setQueueCapacity(300);
        taskExecutor.setKeepAliveSeconds(taskKeepAliveSeconds);
        taskExecutor.setThreadNamePrefix("taskRecognize-");

        taskExecutor.initialize();

        return taskExecutor;
    }


    @Bean(name = "taskReportExecutor")
    public TaskExecutor taskReportExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(reportCore);
        taskExecutor.setMaxPoolSize(200);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setKeepAliveSeconds(taskKeepAliveSeconds);
        taskExecutor.setThreadNamePrefix("taskReportExecutor-");

        taskExecutor.initialize();

        return taskExecutor;
    }

}
