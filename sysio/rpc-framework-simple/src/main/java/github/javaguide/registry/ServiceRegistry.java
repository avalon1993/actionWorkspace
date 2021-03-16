package github.javaguide.registry;

import io.netty.resolver.InetSocketAddressResolver;

public interface ServiceRegistry {

    void registerService(String rpcServiceName, InetSocketAddressResolver inetSocketAddressResolver);

}
