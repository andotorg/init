package org.andot.share.linedot;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * 内存申请工具类
 * @author lucas
 */
public class ByteBufferAllocatorUtil {
    private ByteBufferAllocatorUtil () {}

    /**
     * 申请内存
     * @return
     */
    public static ByteBuf alloc () {
        ByteBufAllocator byteBufAllocator = new PooledByteBufAllocator();
        return byteBufAllocator.ioBuffer();
    }

    /**
     * 指定长度申请内存
     * @param len 申请内存的长度
     * @return
     */
    public static ByteBuf alloc (int len) {
        ByteBufAllocator byteBufAllocator = new PooledByteBufAllocator();
        return byteBufAllocator.ioBuffer(len);
    }
}
