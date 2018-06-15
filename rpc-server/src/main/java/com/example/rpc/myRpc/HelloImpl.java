package com.example.rpc.myRpc;

import com.example.rpc.myRpc.anno.RpcAnnotation;

/**
 * @author lhf
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/6/510:02
 */
@RpcAnnotation(value = IHello.class,version = "1.0")
public class HelloImpl implements IHello {
    @Override
    public String sayHello(String message) {
        return "HELLO "+message;
    }
}
