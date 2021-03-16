package github.javaguide.remoting.transport.netty.codec;

import github.javaguide.enums.CompressTypeEnum;
import github.javaguide.enums.SerializationTypeEnum;
import github.javaguide.remoting.constants.RpcConstants;
import github.javaguide.remoting.dto.RpcMessage;
import github.javaguide.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) throws Exception {


        out.writeBytes(RpcConstants.MAGIC_NUMBER);
        out.writeByte(RpcConstants.VERSION);

        //留4空位给full length
        out.writerIndex(out.writerIndex() + 4);

        byte messageType = rpcMessage.getMessageType();
        out.writeByte(messageType);
        out.writeByte(rpcMessage.getCodec());
        out.writeByte(CompressTypeEnum.GZIP.getCode());
        out.writeInt(ATOMIC_INTEGER.getAndIncrement());

        byte[] bodyBytes = null;
        int fullLength = RpcConstants.HEAD_LENGTH;
        if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec name: [{}] ", codecName);

//            Serializer



        }


    }
}
