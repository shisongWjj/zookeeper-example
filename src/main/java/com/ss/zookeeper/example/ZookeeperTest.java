package com.ss.zookeeper.example;

import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ZookeeperTest
 *
 * @author shisong
 * @date 2020/9/7
 */
public class ZookeeperTest {

    public static void main(String[] args) throws Exception{
        List<AuthInfo> authInfos = new ArrayList<>();
        AuthInfo authInfo = new AuthInfo("digest","ss123:ss123".getBytes());
        authInfos.add(authInfo);
        //zookeeper的地址，session会话的超时时间，重试策略，连接的超时时间
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("10.0.3.30:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .connectionTimeoutMs(4000)
                .authorization(authInfos)
                .build();
        curatorFramework.start();

        //creat(curatorFramework);
        //update(curatorFramework);
        //get(curatorFramework);
        //operatorWithAsync(curatorFramework);
        authOperation(curatorFramework);
    }

    private static void creat(CuratorFramework curatorFramework) throws Exception{
        curatorFramework
                .create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/test","hello world".getBytes());
    }

    private static void update(CuratorFramework curatorFramework) throws Exception{
        curatorFramework
                .setData()
                .forPath("/test","555555".getBytes());
    }

    private static String get(CuratorFramework curatorFramework) throws Exception{
        String str = new String(curatorFramework.getData().forPath("/test"));
        System.out.println(str);
        return str;
    }

    private static void operatorWithAsync(CuratorFramework curatorFramework) throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String s = curatorFramework.create().creatingParentContainersIfNeeded().inBackground(new BackgroundCallback(){
            @Override
            public void processResult(CuratorFramework client, CuratorEvent event){
                System.out.println(Thread.currentThread().getName() + event.getName());
                countDownLatch.countDown();
            }
        }).forPath("/test1", "helloWorld".getBytes());
        countDownLatch.await();
    }

    private static void authOperation(CuratorFramework curatorFramework) throws Exception{
        List<ACL> acls = new ArrayList<>();
        ACL acl = new ACL(ZooDefs.Perms.ALL,new Id("digest", DigestAuthenticationProvider.generateDigest("ss123:ss123")));
        acls.add(acl);
        curatorFramework
                .create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(acls)
                .forPath("/first_auth","66666".getBytes());
    }

    private void originApiTest() throws Exception{
        ZooKeeper zooKeeper = new ZooKeeper("10.0.3.30:2181",5000,new Watcher(){

            @Override
            public void process(WatchedEvent event) {
                //连接成功以后的事件监听
            }
        });

        zooKeeper.getData("/test",new DataWatchListener(),new Stat());
    }

    ZooKeeper zooKeeper;
    class DataWatchListener implements Watcher{

        @Override
        public void process(WatchedEvent event) {
            String path = event.getPath();
            try {
                zooKeeper.getData(path,this,new Stat());
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
