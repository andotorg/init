package org.andot.share.linedot.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 消息对象
 * @author andot
 */
@Setter
@Getter
public class LineDotMessageBody implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型，用户自定义消息类别
     */
    private long id;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息类容，于action 组合为任何类型消息，content 根据 format 可表示为 text,json ,xml数据格式
     */
    private String content;

    /**
     * content 内容格式
     */
    private String format;

    /**
     * 附加内容 内容
     */
    private String extra;
}
