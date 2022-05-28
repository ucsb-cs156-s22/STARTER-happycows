package edu.ucsb.cs156.happiercows.services.jobs;

import edu.ucsb.cs156.happiercows.entities.User;
import edu.ucsb.cs156.happiercows.entities.jobs.Job;
import edu.ucsb.cs156.happiercows.repositories.jobs.JobsRepository;
import edu.ucsb.cs156.happiercows.services.CurrentUserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobService {
  @Autowired
  private JobsRepository jobsRepository;

  @Autowired
  private CurrentUserService currentUserService;

  @Lazy
  @Autowired
  private JobService self;


  public Job runAsJob(JobContextConsumer jobFunction) {

    User currentUser = currentUserService.getUser();
    log.info("currentUser={}",currentUser);
    Job job = Job.builder()
      .createdBy(currentUser)
      .status("running")
      .build();

    jobsRepository.save(job);
    self.runJobAsync(job, jobFunction, SecurityContextHolder.getContext());

    return job;
  }

  

  @Async
  public void runJobAsync(Job job, JobContextConsumer jobFunction, SecurityContext securityContext) {
    JobContext context = new JobContext(jobsRepository, job);

    SecurityContextHolder.setContext(securityContext);

    try {
      jobFunction.accept(context);
    } catch (Exception e) {
      e.printStackTrace();
      job.setStatus("error");
      context.log(e.getMessage());
      return;
    }

    job.setStatus("complete");
    jobsRepository.save(job);
  }
}
