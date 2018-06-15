package com.example.rpc.myRpc;

import com.example.rpc.myRpc.anno.RpcAnnotation;
import com.example.rpc.myRpc.zk.IRegisterCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lhf
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: rpcServer 用于发布远程服务
 * @date 2018/6/510:04
 */
public class RpcServer {

    //创建一个线程池
    private static final ExecutorService EXECUTOR_SERVICE= Executors.newCachedThreadPool();

    //服务发布地址
    private String serviceAddress;

    //注册中心
    private IRegisterCenter registerCenter;


    // 存放服务名称和服务对象之间的关系
    Map<String,Object> handlerMap=new HashMap<>();


    public RpcServer(IRegisterCenter registerCenter,String serviceAddress){
        this.registerCenter=registerCenter;
        this.serviceAddress=serviceAddress;
    }


    public void bind(Object... services){
        for (Object object:services) {
            RpcAnnotation rpcAnnotation=object.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName=rpcAnnotation.value().getName();
            String version=rpcAnnotation.version();

            handlerMap.put(serviceName,object);
        }
    }

    public void publisher(){
        //创建一个服务端socket，且设置传送过来的端口
        ServerSocket serverSocket=null;
        try {
            String[] addrs=serviceAddress.split(":");
            serverSocket=new ServerSocket(Integer.parseInt(addrs[1]));  //启动一个服务监听


            for(String interfaceName:handlerMap.keySet()){
                registerCenter.register(interfaceName,serviceAddress);
                System.out.println("注册服务成功："+interfaceName+"->"+serviceAddress);
            }

            //循环监听
            while (true){
                Socket socket=serverSocket.accept();
                //把监听到的请求交给线程池去处理
                //创建一个映射对象

                EXECUTOR_SERVICE.execute(new ProcessorHandler(handlerMap,socket));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if (serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
