package com.ss.zookeeper.example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * MutexDemo
 *
 * @author shisong
 * @date 2020/9/9
 */
public class MutexDemo {

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("10.0.3.30:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .connectionTimeoutMs(4000)
                .build();
        curatorFramework.start();

        //这里的分布式锁，与juc中的锁性质是一样的
        //分布式锁针对的是进程间，juc中的锁针对的是线程间的
        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework,"/locks");
        for(int i = 0 ; i < 10 ; i++){
            new Thread(()->{
                try {
                    //加锁
                    interProcessMutex.acquire();
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        //释放锁
                        interProcessMutex.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            },"线程"+i).start();
        }
    }
}
