package com.ss.zookeeper.example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;

/**
 * ZookeeperTest
 *
 * @author shisong
 * @date 2020/9/7
 */
public class ZookeeperTest {

    public static void main(String[] args) throws Exception{
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("10.0.3.30:2181")
                .retryPolicy(new RetryUntilElapsed(1000,3))
                .build();

        /*curatorFramework.setData().forPath("/test");*/
    }

}
