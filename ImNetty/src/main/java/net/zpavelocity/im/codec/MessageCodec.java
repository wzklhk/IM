package net.zpavelocity.im.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import net.zpavelocity.im.message.Message;
import net.zpavelocity.im.message.MessageTypeEnum;
import net.zpavelocity.im.util.BytesObjConverter;

import java.util.List;

//@ChannelHandler.Sharable
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. magic number
        out.writeBytes(new byte[]{1, 2});
        // 2. version
        out.writeByte(1);
        // 3. message type code
        out.writeByte(MessageTypeEnum.getCode(msg.getMessageTypeEnum()));

        byte[] bytes = BytesObjConverter.o2b(msg);
        int length = bytes.length;

        // 4. length of message
        out.writeInt(length);
        // 5. message
        out.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. magic number
        in.readBytes(2);
        // 2. version
        byte version = in.readByte();
        // 3. message type
        MessageTypeEnum messageType = MessageTypeEnum.getType(in.readByte());
        // 4. length of message
        int length = in.readInt();

        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 5. message
        Message message = (Message) BytesObjConverter.b2o(bytes);

        out.add(message);
    }
}
