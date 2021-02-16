package org.andot.share.linedot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author andot
 */
public class StringToByteBufUtil {
    private StringToByteBufUtil() {
    }

    public static ByteBuf toByteBuf(String str) {
        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
        return byteBuf;
    }

    public static ByteBuf toByteBuf(JSONObject jsonObject) {
        byte[] data = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
        ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
        return byteBuf;
    }

    public static <T> ByteBuf toByteBuf(T t) {
        byte[] data = JSONObject.toJSONString(t).getBytes(StandardCharsets.UTF_8);
        ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
        return byteBuf;
    }
}
