package com.ef.service;

import com.ef.Constants;
import com.ef.model.DetectedIp;
import com.ef.model.Duration;
import com.ef.util.ASCIITable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * @author fsierra on 2019-07-30
 */
@Slf4j
@Service
public class IpControlServiceImpl implements IpControlService{

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private Date startDate;

    @Value("${threshold}")
    private Integer threshold;

    @Value("${duration}")
    private Duration duration;

    @Autowired
    public IpControlServiceImpl(@Value("${startDate}") String startDateStr,
                                NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws ParseException {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        try {
            this.startDate = DateUtils.parseDate(startDateStr, Constants.PARAM_DATE_FORMAT);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public void checkIps() {
        Date endDate = endDate();
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("threshold", threshold);

        System.out.println("Checking ip exceeding threshold.");
        System.out.println("Start date:\t" + startDate);
        System.out.println("End date:\t" + endDate);
        System.out.println("Threshold:\t" + threshold);

        //check ips in range
        List<DetectedIp> ips = namedParameterJdbcTemplate.query(Constants.CHECK_IP_QUERY, params,
                (rs, row) -> new DetectedIp(
                        rs.getString(1),
                        rs.getInt(2),
                        String.format(Constants.REASON_MESSAGE,
                                rs.getString(1),
                                rs.getInt(2),
                                threshold))
        );

        //insert ips found
        SqlParameterSource[] parameterSource = SqlParameterSourceUtils.createBatch(ips.toArray());
        this.namedParameterJdbcTemplate.batchUpdate(Constants.INSERT_DETECTED_IP_QUERY, parameterSource);

        this.printIps(ips);

    }

    private Date endDate(){
        if(Duration.hourly.equals(duration)){
            return DateUtils.addHours(this.startDate, 1);
        }else{
            return DateUtils.addDays(DateUtils.truncate(this.startDate, Calendar.DAY_OF_MONTH), 1);
        }
    }

    private void printIps(List<DetectedIp> ips){

        String[] headers = { "IP", "Reason" };
        String[][] data = ips.stream()
                .map(ip -> new String[]{ip.getIp(), ip.getReason()})
                .toArray(String[][]::new);
        System.out.println(ASCIITable.of(headers, data));
    }
}
