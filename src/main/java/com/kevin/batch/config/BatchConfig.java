package com.kevin.batch.config;

import com.kevin.batch.loans.Loan;
import com.kevin.batch.loans.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private static final Logger logger = LogManager.getLogger(BatchConfig.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final LoanRepository repository;

    @Bean
    public FlatFileItemReader<Loan> reader() {
        FlatFileItemReader<Loan> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/data/loans.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        logger.info("BatchConfig reader: Reader created");
        return itemReader;
    }

    @Bean
    public LoanProcessor processor() {
        return new LoanProcessor();
    }

    @Bean
    public RepositoryItemWriter<Loan> writer() {
        RepositoryItemWriter<Loan> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step importStep() {
        logger.info("BatchConfig importStep: creating Step");
        return new StepBuilder("csvImport", jobRepository)
                .<Loan, Loan>chunk(10, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("importLoans", jobRepository)
                .start(importStep())
                .build();
    }

    private LineMapper<Loan> lineMapper() {
        // Set limits and fields for csv file
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("loanId", "userId", "total", "interest", "monthlyPayment", "balance", "createdDate", "startDate", "updatedDate");

        // Allow transform of csv file lines to Loan
        BeanWrapperFieldSetMapper<Loan> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Loan.class);

        DefaultLineMapper<Loan> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        logger.info("BatchConfig lineMapper: DefaultLineMapper created");
        return lineMapper;
    }
}
