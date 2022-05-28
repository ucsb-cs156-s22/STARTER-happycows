package edu.ucsb.cs156.happiercows.services.jobs;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import static org.mockito.ArgumentMatchers.any;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import edu.ucsb.cs156.happiercows.entities.jobs.Job;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import edu.ucsb.cs156.happiercows.repositories.jobs.JobsRepository;

import edu.ucsb.cs156.happiercows.testconfig.TestConfig;

@SpringBootTest(classes={JobService.class})
@Import(TestConfig.class)
public class JobServiceTests {
    
    @Autowired
    JobService jobService;

    @MockBean
    JobsRepository jobsRepository;

    @MockBean
    UserRepository userRepository;

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_runAsJob() throws Exception {

        // arrange

        JobContextConsumer jcc = mock(JobContextConsumer.class);

        // act 
        Job job = jobService.runAsJob(jcc);

        // assert
        verify(jobsRepository, times(2)).save(job);
        verify(jcc).accept(any());
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_runAsJob_withException() {

        // arrange
        JobContextConsumer jcc = (ctx)->{ throw new Exception();};

        // act 
        Job job = jobService.runAsJob(jcc);

        // assert
        verify(jobsRepository, times(2)).save(job);

    }

}
