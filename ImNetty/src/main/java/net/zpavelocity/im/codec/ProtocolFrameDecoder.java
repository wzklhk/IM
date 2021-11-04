package net.zpavelocity.im.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder() {
        this(1024,
                0,
                4,
                0,
                4);
    }

    public ProtocolFrameDecoder(int maxFrameLength,
                                int lengthFieldOffset,
                                int lengthFieldLength,
                                int lengthAdjustment,
                                int initialBytesToStrip) {
        super(maxFrameLength,
                lengthFieldOffset,
                lengthFieldLength,
                lengthAdjustment,
                initialBytesToStrip);
    }
}
