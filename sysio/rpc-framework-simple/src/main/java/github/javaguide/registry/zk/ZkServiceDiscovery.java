package github.javaguide.registry.zk;

import github.javaguide.extension.ExtensionLoader;
import github.javaguide.loadbalance.LoadBalance;
import github.javaguide.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = ExtensionLoader
                .getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();

        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {

        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcServiceName);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(";");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);

    }
}
