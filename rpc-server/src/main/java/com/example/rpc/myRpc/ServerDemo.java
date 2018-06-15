package com.example.rpc.myRpc;

import com.example.rpc.myRpc.zk.IRegisterCenter;
import com.example.rpc.myRpc.zk.RegisterCenterImpl;

/**
 * @author lhf
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/6/510:50
 */
public class ServerDemo {
    public static void main(String[] args) {
        IHello hello=new HelloImpl();

        IRegisterCenter registerCenter=new RegisterCenterImpl();
        RpcServer rpcServer=new RpcServer(registerCenter,"127.0.0.1:8080");
        rpcServer.bind(hello);
        //发布服务
        rpcServer.publisher();
    }



}
