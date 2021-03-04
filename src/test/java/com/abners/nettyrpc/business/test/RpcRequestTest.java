package com.abners.nettyrpc.business.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.abners.nettyrpc.business.consumer.RequestService;
import com.abners.nettyrpc.main.Boot;
import com.abners.nettyrpc.util.UUIDUtil;

/**
 * 类RpcRequestTest.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年03月02日 15:22:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Boot.class)
public class RpcRequestTest {

    @Autowired
    RequestService requestService;
    Executor executor = new ThreadPoolExecutor(4, 10, 20, TimeUnit.SECONDS, new ArrayBlockingQueue(100),
                                               new ThreadFactory() {

                                                   @Override
                                                   public Thread newThread(Runnable r) {
                                                       Thread thread = new Thread(r);
                                                       thread.setName(System.currentTimeMillis() + "TH:");
                                                       return thread;
                                                   }
                                               });

    @Test
    public void testConsume() throws InterruptedException, ExecutionException {
        FutureTask[] futureTasks = new FutureTask[100];
        for (int i = 0; i < 100; i++) {
            Task task = new Task();
            FutureTask futureTask = new FutureTask(task);
            futureTasks[i] = futureTask;
            executor.execute(futureTask);
        }
        for (int i = 0; i < 100; i++) {
            futureTasks[i].get();
        }

    }

    public class Task implements Callable {

        @Override
        public Object call() throws Exception {
            requestService.sendRequest(UUIDUtil.uuid());
            return UUIDUtil.uuid();
        }
    }
}
