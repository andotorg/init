package org.andot.share.linedot.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author andot
 */
@Setter
@Getter
public class PositionMessageContent extends LineDotMessageBody {
    private Double lang;
    private Double lat;
}
