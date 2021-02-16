package org.andot.share.linedot.util;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * get请求地址路径参数获取工具类
 * @author andot
 */
public class HttpRequestParamUtil {
    private HttpRequestParamUtil () {

    }

    /**
     * 获取路径参数
     * @param request
     * @return
     */
    public static Map<String, Object> getRequestParams(FullHttpRequest request) {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
        List<InterfaceHttpData> httpPostData = decoder.getBodyHttpDatas();
        Map<String, Object> params = new HashMap<>();

        for (InterfaceHttpData data : httpPostData) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            }
        }
        return params;
    }

    public static String getLineId (String url) {
        String lineId = null;
        if (!StringUtil.isNullOrEmpty(url) && url.indexOf("lineId") > -1) {
            String params = url.substring(url.indexOf("lineId="));
            String[] str = params.split("=");
            if (str != null && str.length > 1) {
                lineId = str[1];
            }
        }
        return lineId;
    }
}
