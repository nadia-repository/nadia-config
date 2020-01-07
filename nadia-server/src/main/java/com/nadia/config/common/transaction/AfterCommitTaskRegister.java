package com.nadia.config.common.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class AfterCommitTaskRegister extends TransactionSynchronizationAdapter {
    private static final ThreadLocal<List<Runnable>> TASKS = new ThreadLocal<>();

    public static void registerTask(Runnable task) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            log.info("Transaction synchronization is NOT ACTIVE, Execute task={} now", task);
            task.run();
            return;
        }

        List<Runnable> tasks = TASKS.get();
        if (tasks == null) {
            tasks = new LinkedList<>();
            TASKS.set(tasks);

            TransactionSynchronizationManager.registerSynchronization(new AfterCommitTaskRegister());
        }
        tasks.add(task);
    }

    @Override
    public void afterCommit() {
        List<Runnable> tasks = TASKS.get();
        log.info("Transaction successfully committed, start to execute after commit tasks, size={}", tasks.size());

        for (Runnable task : tasks) {
            try {
                task.run();
            } catch (Throwable t) {
                log.error("Fail to run task={}", task, t);
            }
        }
    }

    @Override
    public void afterCompletion(int status) {
        log.info("Transaction completed and clean up all the committed tasks");
        TASKS.remove();
    }
}
