package net.zpavelocity.netty.codec.byte2int;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

public class Byte2IntegerDecoderTest {

    public static void main(String[] args) {
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {

            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new Byte2IntegerDecoder());
                ch.pipeline().addLast(new IntegerProcessHandler());
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(channelInitializer);
        for (int j = 0; j < 100; j++) {
            ByteBuf buf = Unpooled.buffer();
            buf.writeInt(j);
            channel.writeInbound(buf);
        }

    }
}
