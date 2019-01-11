package com.rwb.service.imp;

import com.rwb.api.ZKLockTest;
import com.rwb.util.ZKLock;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZKLockTestImp implements ZKLockTest {

    private volatile int num = 0;

    private ZooKeeper zk = null;

    InterProcessMutex mutex = null;

    CuratorFramework client = null;
    {

        synchronized (this) {
            if (mutex == null) {
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

                client = CuratorFrameworkFactory.newClient("47.99.242.112:2181", retryPolicy);

                client.start();

                mutex = new InterProcessMutex(client, "/user/node");
            }

            /*synchronized (this) {
                if (zk == null) {
                    try {
                        zk = new ZooKeeper("47.99.242.112:2181", 6000, watchedEvent -> {
                            if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                                System.out.println("connection is established..");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }*/
        }
    }

    @Override
    public void Append() {

        /*RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("47.99.242.112:2181", retryPolicy);
        client.start();

        // 锁节点为 /curator/mutex
        InterProcessMutex mutex = new InterProcessMutex(client, "/curator/mutex");*/

        try {

            /*ZKLock lock = new ZKLock(zk,"/test/node1");

            lock.lock();*/
            mutex.acquire();

            num = num + 1;



            //lock.unlock();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Integer getNum() {
        return num;
    }
}
