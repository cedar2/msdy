package com.platform.common.core.experimental.util;

import com.platform.common.core.experimental.annotation.PlatformExperimental;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多任务并发工具类的默认实现类，使用线程池实现
 *
 * @author Straw
 */
@PlatformExperimental
public class MultiThreadLauncher implements MultiTaskLauncher {

    ThreadPoolExecutor executor;

    public MultiThreadLauncher(int nThreads) {
        executor = ofThreadPoolExecutor(nThreads);
    }

    @Override
    public boolean tryAddTask(Runnable task) {
        // 线程池处在关闭状态，无法接收任务。
        if (executor.isTerminating() || executor.isTerminated()) {
            return false;
        }

        addTask(task);
        return true;
    }

    @Override
    public void addTask(Runnable task) {
        executor.submit(task);
    }

    @Override
    public void waitFinish() {
        // 关闭线程池
        // 该方法调用后，线程池无法接收新任务。此后调用的addTask方法会无效。
        executor.shutdown();

        // 循环等待线程池完成剩余任务。
        while (!executor.isTerminated()) {
            try {
                // 等待 1 秒
                if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException ignored) {}
        }

    }

    protected ThreadPoolExecutor ofThreadPoolExecutor(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }


}
