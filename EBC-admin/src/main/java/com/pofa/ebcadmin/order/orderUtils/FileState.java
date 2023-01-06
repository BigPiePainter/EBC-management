package com.pofa.ebcadmin.order.orderUtils;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class FileState {
    private String fileName;
    private String state;

    private int code;

    private Long size;


    private int realRowNum;

    private int rightRowNum;

    private int wrongRowNum;

    private long socketSendTime = 0;
}
