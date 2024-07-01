package com.kevin.batch.loans;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch/loans")
public class LoanController {
    private static final Logger logger = LogManager.getLogger(LoanController.class);

    private JobLauncher jobLauncher;
    private Job loanProcessingJob;

    @PostMapping("/start")
    public ResponseEntity<String> start() {
        try {
            logger.info("LoanController start: starting batch job");
            jobLauncher.run(loanProcessingJob, new JobParametersBuilder().toJobParameters());
            return ResponseEntity.ok("Batch job started successfully");
        } catch (Exception e) {
            logger.error("LoanController start: failed with {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to start batch job");
        }
    }
}
