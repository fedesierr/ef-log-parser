package com.ef.listener;

import com.ef.service.IpControlService;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author fsierra on 2019-07-29
 */
@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {


    private final ProgressBar progressBar;

    private final IpControlService ipControlService;

    @Autowired
    public JobCompletionNotificationListener(IpControlService ipControlService,
                                             ProgressBar progressBar) {
        this.ipControlService = ipControlService;
        this.progressBar = progressBar;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        try {
            this.progressBar.close();

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                System.out.println("Finished access log parse.");
                ipControlService.checkIps();

            } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
                System.err.println("Something is wrong, check log file for more information.");
                if (!CollectionUtils.isEmpty(jobExecution.getAllFailureExceptions())) {
                    for (Throwable t : jobExecution.getAllFailureExceptions()) {
                        System.err.println(t.getMessage() + ": " + ExceptionUtils.getRootCauseMessage(t));
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            System.err.println("Something is wrong, check log file for more information.");

        }
    }

}