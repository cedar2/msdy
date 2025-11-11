package com.platform.common.core.experimental.util;

import java.util.List;
import java.util.function.Consumer;

/**
 * 多任务并发工具类
 *
 * @author Straw
 */
public interface MultiTaskLauncher {

    /**
     * MultiTaskLauncher 的默认实现 - 使用线程池实现
     *
     * @return MultiThreadLauncher，默认使用cpu核心数的线程池
     */
    static MultiTaskLauncher ofDefault() {
        return new MultiThreadLauncher(Runtime.getRuntime().availableProcessors());
    }

    /**
     * 往 launcher 添加任务
     *
     * @param task 任务对象
     * @throws RuntimeException 如果添加任务失败
     */
    void addTask(Runnable task) throws RuntimeException;

    /**
     * 该方法会阻塞调用线程，直到 launcher 的所有任务都被执行完毕。
     */
    void waitFinish();

    /**
     * 尝试往 launcher 添加任务，如果失败，不会抛出异常。
     *
     * @param task 任务对象
     * @return 是否添加成功，如果返回true，表示该任务被接受并已开始执行。
     * 如果返回false，表示该任务没有被接收也没有被执行。
     */
    default boolean tryAddTask(Runnable task) {
        try {
            addTask(task);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    default <T> void autoGenerateTask(List<T> iterableObj,
                                      Consumer<T> consumeEachObj,
                                      boolean waitFinish) {
        iterableObj.forEach(obj -> addTask(() -> consumeEachObj.accept(obj)));
        if (waitFinish) {
            waitFinish();
        }
    }

}
