package com.ef.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fsierra on 2019-07-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectedIp {

    private String ip;

    private Integer total;

    private String reason;
}
