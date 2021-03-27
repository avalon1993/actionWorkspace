package rpcdemo.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import rpcdemo.rpc.ResponseMappingCallback;
import rpcdemo.util.Packmsg;

/**
 * @author: 马士兵教育
 * @create: 2020-08-16 21:15
 */
public class ClientResponses  extends ChannelInboundHandlerAdapter {

    //consumer.....
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packmsg responsepkg = (Packmsg) msg;

        //曾经我们没考虑返回的事情
        ResponseMappingCallback.runCallBack(responsepkg);

    }
}

