package com.rwb.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 代码经测试有问题！！
 */
public class ZKLock {
    private static final Logger log = LoggerFactory.getLogger(ZKLock.class);

    private ZooKeeper zk;

    private String basePath;

    private String lockPath;

    private static final byte[] LOCK_DATA = "".getBytes();

    public ZKLock(ZooKeeper zk, String basePath) {
        if (basePath.endsWith("/") || !basePath.startsWith("/")) {
            throw new IllegalArgumentException("base path must start with '/', and must not end with '/'");
        }
        this.zk = zk;
        this.basePath = basePath;
    }

    private void ensureBasePath() throws KeeperException, InterruptedException {
        if (zk.exists(basePath, false) == null) {
            List<String> pathParts = new ArrayList<>(Arrays.asList(basePath.split("/")));
            pathParts.remove(0);

            int last = 0;
            for (int i = pathParts.size() - 1; i > 0; i--) {
                String path = "/" + StringUtils.join(pathParts.subList(0, i), "/");
                if (zk.exists(path, false) != null) {
                    last = i;
                    break;
                }
            }

            for (int i = last; i < pathParts.size(); i++) {
                String path = "/" + StringUtils.join(pathParts.subList(0, i + 1), "/");
                zk.create(path, LOCK_DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }

    public void lock() throws KeeperException, InterruptedException {
        ensureBasePath();
        String lockPath = zk.create(basePath + "/lock_", LOCK_DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        //log.info(Thread.currentThread().getName() + " create : " + lockPath);

        while (true) {
            //返回给定路径的节点的子节点
            List<String> children = zk.getChildren(basePath, false);
            Collections.sort(children);
            //System.out.printf("当前线程为 %s ,当前的所有子节点为 ： %s \n", Thread.currentThread().getName(), Arrays.toString(children.toArray()));
            String minNode = children.get(0);
            //System.out.printf("当前线程为 %s,当前的节点是 %s \n", Thread.currentThread().getName(), minNode);

            if (StringUtils.isNotBlank(lockPath) && StringUtils.isNotBlank(minNode)
                    && StringUtils.equals(lockPath, basePath + "/" + minNode)) {
                this.lockPath = lockPath;
                return;
            }

            String watchNode = null;

            String node = lockPath.substring(lockPath.lastIndexOf("/") + 1);
            for (int i = children.size() - 1; i >= 0; i--) {
                String child = children.get(i);
                if (child.compareTo(node) < 0) {
                    watchNode = child;
                    break;
                }
            }

            if (watchNode != null) {
                //log.info(Thread.currentThread().getName() + " watch : " + watchNode);

                String watchPath = basePath + "/" + watchNode;

                try {
                    zk.getData(watchPath, event -> {
                        if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
                            synchronized (this) {
                                notifyAll();
                            }
                        }
                    }, null);

                } catch (KeeperException.NoNodeException e) {
                    continue;
                }

                synchronized (this) {
                    wait();
                   // log.info(Thread.currentThread().getName() + " notified");
                }
            }

        }
    }

    public void unlock() throws KeeperException, InterruptedException {
        if (StringUtils.isNotBlank(lockPath)) {
            zk.delete(lockPath, -1);
        } else {
            throw new IllegalStateException("don't has lock");
        }
    }

    /*public static void main(String[] args) {
        int concurrent = 10;
        ExecutorService service = Executors.newFixedThreadPool(concurrent);
        for (int i = 0; i < concurrent; i++) {
            service.execute(() -> {
                ZooKeeper zk;

                try {
                    zk = new ZooKeeper("47.99.242.112:2181", 6000, watchedEvent -> {
                        if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                            System.out.println("connection is established..");
                        }
                    });

                    ZKLock lock = new ZKLock(zk, "/test/node1");

                    lock.lock();
                    System.out.println(Thread.currentThread().getName() + " acquire success");
                    Thread.sleep(1000);

                    lock.unlock();
                    System.out.println(Thread.currentThread().getName() + " release success");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        service.shutdown();
    }*/


}
