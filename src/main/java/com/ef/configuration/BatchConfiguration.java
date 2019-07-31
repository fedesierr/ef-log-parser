package com.ef.configuration;

import com.ef.Constants;
import com.ef.listener.JobCompletionNotificationListener;
import com.ef.listener.ProgressListener;
import com.ef.model.LogLine;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fsierra on 2019-07-29
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final String accessLog;

    @Autowired
    public BatchConfiguration(@Value("${accesslog}") String accessLog,
                              JobBuilderFactory jobBuilderFactory,
                              StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.accessLog = accessLog;

    }

    @Bean
    public Long lineCount() throws IOException {
        Path path = Paths.get(this.accessLog);
        if(Files.exists(path)) {
            return Files.lines(path).count();
        }

        return 0L;
    }

    @Bean
    public ProgressBar progressBar() throws IOException{
        return new ProgressBarBuilder()
                .setInitialMax(lineCount())
                .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BLOCK)
                .setTaskName("Reading accesslog...")
                .build();
    }

    @Bean
    public Map<?, ? extends PropertyEditor> customEditors(){
        Map<Object, PropertyEditor> customEditors = new HashMap<>();
        customEditors.put("java.util.Date", new CustomDateEditor(new SimpleDateFormat(Constants.DATE_FORMAT), true));
        return customEditors;
    }

    @Bean
    public FlatFileItemReader<LogLine> reader() {
        return new FlatFileItemReaderBuilder<LogLine>()
                .name("personItemReader")
                .resource(new FileSystemResource(accessLog))
                .delimited()
                .delimiter(Constants.DELIMITER)
                .quoteCharacter(Constants.QUOTE)
                .names(Constants.NAMES)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<LogLine>() {{
                    setTargetType(LogLine.class);
                    setCustomEditors(customEditors());
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<LogLine> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<LogLine>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(Constants.INSERT_QUERY)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job loadLogLoneJob(JobCompletionNotificationListener listener, Step loadLogLineStep) {
        return jobBuilderFactory.get("loadLogLoneJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(loadLogLineStep)
                .end()
                .build();
    }

    @Bean
    public Step loadLogLineStep(ProgressListener progressListener, JdbcBatchItemWriter<LogLine> writer) {
        return stepBuilderFactory.get("loadLogLineStep")
                .<LogLine, LogLine>chunk(Constants.CHUNK_SIZE)
                .reader(reader())
                .writer(writer)
                .listener(progressListener)
                .build();
    }
}
