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
public class LineDotMessageHeader implements Serializable {
    /**
     * 当前消息发送人id
     */
    private String lineId;
    /**
     * 当前接收消息人id
     */
    private String toLineId;
    /**
     * 消息类型
     */
    private Integer msgType;
}
