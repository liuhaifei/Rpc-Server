package com.example.rpc.myRpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

/**
 * @author lhf
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/6/510:17
 */
public class ProcessorHandler implements Runnable {

//    private Object service;//发布的服务
    private Socket socket;//请求的socket
    private Map<String,Object> handlerMap;

    public ProcessorHandler(Map<String,Object> handlerMap, Socket socket) {
        this.handlerMap=handlerMap;
        this.socket = socket;
    }

    @Override
    public void run() {
        //请求处理流
        ObjectInputStream objectInputStream=null;
        try {
            objectInputStream=new ObjectInputStream(socket.getInputStream());
            //通过反序列化获取远程对象
            RpcRequest rpcRequest=(RpcRequest)objectInputStream.readObject();
            //通过反射获取结果Object
            Object object=invoke(rpcRequest);

            //通过输出流把结果输出
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectInputStream.close();
            objectOutputStream.close();

        }catch (Exception e){
          throw  new RuntimeException(e);
        }finally {
            if(objectInputStream!=null){
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //通过反射调用服务
    public Object invoke(RpcRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object[] args=request.getParameters();

        String serviceName=request.getClassName();
        Class<?>[] types=new Class[args.length];
        for (int i=0;i<args.length;i++){
            types[i]=args[i].getClass();
        }
        Object service=handlerMap.get(serviceName);//从map中获取对象
        Method method=service.getClass().getMethod(request.getMethodName(),types);
        return method.invoke(service,args);
    }
}
