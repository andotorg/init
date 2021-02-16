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
public class LineDotMessage implements Serializable {
    private LineDotMessageHeader header;
    private LineDotMessageBody body;
    private LineDotMessageFooter footer;
}
