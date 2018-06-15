package com.example.rpc.myRpc.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author lhf
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/6/1415:28
 */
public class RegisterCenterImpl implements IRegisterCenter {

    private CuratorFramework curatorFramework;
    {
        curatorFramework= CuratorFrameworkFactory.builder()
                        .connectString(ZkConfig.CONNNECTION_STR)
                        .sessionTimeoutMs(4000)
                        .retryPolicy(new ExponentialBackoffRetry(1000,3))
                        .build();
        curatorFramework.start();
    }


    @Override
    public void register(String serviceName, String serviceAddress) {
        String servicePath=ZkConfig.ZK_REGISTER_PATH+"/"+serviceName;

        try {
            if(curatorFramework.checkExists().forPath(servicePath)==null){
                curatorFramework.create().creatingParentsIfNeeded()
                                .withMode(CreateMode.PERSISTENT)
                                .forPath(servicePath);
            }

            String addressPath=servicePath+"/"+serviceAddress;
            String rsNode=curatorFramework.create()
                            .withMode(CreateMode.EPHEMERAL)
                            .forPath(addressPath,"0".getBytes());

            System.out.println("注册成功："+rsNode);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }



    }
}
