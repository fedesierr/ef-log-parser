package com.ef.listener;

import com.ef.Constants;
import com.ef.model.DetectedIp;
import com.ef.model.Duration;
import com.ef.util.ASCIITable;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * @author fsierra on 2019-07-29
 */
@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {


    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final ProgressBar progressBar;

    private Date startDate;

    @Value("${threshold}")
    private Integer threshold;

    @Value("${duration}")
    private Duration duration;

    @Autowired
    public JobCompletionNotificationListener(@Value("${startDate}") String startDateStr,
                                             NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                             ProgressBar progressBar) throws ParseException {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.progressBar = progressBar;

        this.startDate = DateUtils.parseDate(startDateStr, "yyyy-MM-dd.HH:mm:ss");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        this.progressBar.close();

        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {

            System.out.println("Finished access log parse.");

            Date endDate = endDate();
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("threshold", threshold);

            System.out.println("Checking ip exceeding threshold.");
            System.out.println("Start date:\t" + startDate);
            System.out.println("End date:\t" + endDate);
            System.out.println("Threshold:\t" + threshold);



            List<DetectedIp> ips = namedParameterJdbcTemplate.query(Constants.CHECK_IP_QUERY, params,
                    (rs, row) -> new DetectedIp(
                            rs.getString(1),
                            rs.getString(2))
            );

            String[] headers = { "IP", "Threshold" };
            String[][] data = ips.stream()
                    .map(ip -> new String[]{ip.getIp(), ip.getReason()})
                    .toArray(String[][]::new);

            System.out.println(ASCIITable.of(headers, data));
        }
    }

    private Date endDate(){
        if(Duration.hourly.equals(duration)){
            return DateUtils.addHours(this.startDate, 1);
        }else{
            return DateUtils.addDays(DateUtils.truncate(this.startDate, Calendar.DAY_OF_MONTH), 1);
        }
    }


}