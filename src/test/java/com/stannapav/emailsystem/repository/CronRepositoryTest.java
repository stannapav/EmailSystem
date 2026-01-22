package com.stannapav.emailsystem.repository;

import com.stannapav.emailsystem.db.entities.CronJob;
import com.stannapav.emailsystem.db.repositories.CronRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class CronRepositoryTest {
    @Autowired
    private CronRepository cronRepository;

    @Test
    public void CronRepository_Save_ReturnSavedCronJob(){
        CronJob cronJob = new CronJob();
        cronJob.setExpression("0 */2 * * * ?");

        CronJob savedCronJob = cronRepository.save(cronJob);

        Assertions.assertThat(savedCronJob).isNotNull();
        Assertions.assertThat(savedCronJob).isEqualTo(cronJob);
    }

    @Test
    public void CronRepository_UpdateCronJob_ReturnUpdatedCronJob(){
        CronJob cronJob = new CronJob();
        cronJob.setExpression("0 */2 * * * ?");

        CronJob savedCronJob = cronRepository.save(cronJob);
        savedCronJob.setExpression("0 2 * * * *");
        CronJob updatedCronJob = cronRepository.save(savedCronJob);

        Assertions.assertThat(updatedCronJob).isNotNull();
        Assertions.assertThat(updatedCronJob).isEqualTo(savedCronJob);
    }

    @Test
    public void CronRepository_FindById_ReturnCronJobWithID(){
        CronJob cronJob = new CronJob();
        cronJob.setExpression("0 */2 * * * ?");

        cronRepository.save(cronJob);
        CronJob foundCronJob = cronRepository.findById(cronJob.getId()).orElse(null);

        Assertions.assertThat(foundCronJob).isNotNull();
        Assertions.assertThat(foundCronJob).isEqualTo(cronJob);
    }

    @Test
    public void CronRepository_FindByExpression_ReturnCronJobWithExpression(){
        String expression = "0 */2 * * * ?";
        CronJob cronJob = new CronJob();
        cronJob.setExpression(expression);

        cronRepository.save(cronJob);
        CronJob foundCronJob = cronRepository.findByExpression(expression).orElse(null);

        Assertions.assertThat(foundCronJob).isNotNull();
        Assertions.assertThat(foundCronJob).isEqualTo(cronJob);
    }

    @Test
    public void CronRepository_FindAll_ReturnAllCronJob(){
        CronJob cronJob1 = new CronJob();
        cronJob1.setExpression("0 */2 * * * ?");
        cronRepository.save(cronJob1);

        CronJob cronJob2 = new CronJob();
        cronJob2.setExpression("0 2 * * * *");
        cronRepository.save(cronJob2);

        Pageable pageable = PageRequest.of(0, 2);
        Page<CronJob> cronJobPage = cronRepository.findAll(pageable);
        Page<CronJob> mokingPage = new PageImpl<>(List.of(cronJob1, cronJob2), pageable, 0);

        Assertions.assertThat(cronJobPage).isNotNull();
        Assertions.assertThat(cronJobPage).isEqualTo(mokingPage);
    }

    @Test
    public void CronRepository_DeleteCronJob_ReturnCronJobIsEmpty(){
        CronJob cronJob = new CronJob();
        cronJob.setExpression("0 */2 * * * ?");

        cronRepository.save(cronJob);
        cronRepository.deleteById(cronJob.getId());
        Optional<CronJob> deletedCronJob = cronRepository.findById(cronJob.getId());

        Assertions.assertThat(deletedCronJob).isEmpty();
    }

    @Test
    public void CronRepository_DeleteAllCronJob_ReturnAllCronJobIsEmpty(){
        CronJob cronJob1 = new CronJob();
        cronJob1.setExpression("0 */2 * * * ?");
        cronRepository.save(cronJob1);

        CronJob cronJob2 = new CronJob();
        cronJob2.setExpression("0 2 * * * *");
        cronRepository.save(cronJob2);

        cronRepository.deleteAll();
        List<CronJob> deletedCronJobs = cronRepository.findAll();

        Assertions.assertThat(deletedCronJobs).isEmpty();
    }
}
