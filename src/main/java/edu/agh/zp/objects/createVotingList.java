package edu.agh.zp.objects;

import edu.agh.zp.repositories.VotingRepository;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class createVotingList {

    public static void run(ModelAndView modelAndView, VotingEntity.TypeOfVoting type, VotingRepository votingRepository)
    {
        LocalDate now = LocalDate.now();
        Date nowSql = java.sql.Date.valueOf(now);
        List<VotingEntity> voting = votingRepository.findByVotingDateBetweenAndVotingTypeOrderByVotingDateAscOpenVotingAsc(nowSql, java.sql.Date.valueOf(now.plusDays(1)), type);
        boolean[] nums = new boolean[voting.size()];
        Time time = java.sql.Time.valueOf(LocalTime.now());
        if (voting.isEmpty()) modelAndView.addObject("Sign", null);
        else {
            for (int i = 0; i < voting.size(); i++) {
                VotingEntity vote = voting.get(i);
                if (vote.getVotingDate().equals(nowSql) && vote.getOpenVoting().before(time) && vote.getCloseVoting().after(time))
                    nums[i] = true;
            }
            modelAndView.addObject("Sign", nums);
        }
        modelAndView.addObject("VotingList", voting);
    }
}
