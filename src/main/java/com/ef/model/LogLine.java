package com.ef.model;

import lombok.Data;

import java.util.Date;

/**
 * @author fsierra on 2019-07-28
 */
@Data
public class LogLine {

    private Date date;

    private String ip;

    private String method;

    private Integer httpStatus;

    private String userAgent;
}
