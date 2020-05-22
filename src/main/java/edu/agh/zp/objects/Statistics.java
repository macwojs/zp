package edu.agh.zp.objects;

import edu.agh.zp.objects.Chart;
import org.springframework.stereotype.Service;

import java.util.List;

public class Statistics {
    enum type {Sejm, Senat, Everyone};
    public long votesCount;
    public long entitledToVote;
    public float frequency;

    public Chart chart;
    public Statistics(){ ; }

    public Statistics(long votesCount, long entitledToVote,  Chart chart) {
        this.votesCount = votesCount;
        this.entitledToVote = entitledToVote;
        this.frequency = (float)votesCount/entitledToVote *100;
        this.chart = chart;
    }
}
