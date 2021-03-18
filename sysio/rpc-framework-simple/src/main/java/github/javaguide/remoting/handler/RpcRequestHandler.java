package github.javaguide.remoting.handler;

import github.javaguide.factory.SingletonFactory;
import github.javaguide.provider.ServiceProvider;
import github.javaguide.provider.ServiceProviderImpl;
import github.javaguide.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.toRpcProperties());
        return invokeTargetMethod(rpcRequest, service);
    }


    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result = new Object();

        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

}
