package com.ef.listener;

import me.tongfei.progressbar.ProgressBar;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fsierra on 2019-07-29
 */
@Component
public class ProgressListener implements ChunkListener {

    private final ProgressBar progressBar;

    @Autowired
    public ProgressListener(ProgressBar progressBar) {
         this.progressBar = progressBar;
    }

    @Override
    public void beforeChunk(ChunkContext context) {

    }

    @Override
    public void afterChunk(ChunkContext context) {
        int count = context.getStepContext().getStepExecution().getReadCount();
        this.progressBar.stepTo(count);
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
