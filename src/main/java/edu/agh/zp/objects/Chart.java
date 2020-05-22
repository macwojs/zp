package edu.agh.zp.objects;

import java.util.List;

public class Chart {
    public String title;
    public List<StatisticRecord> data;

    public Chart(String title, List<StatisticRecord> data) {
        this.title = title;
        this.data = data;
    }
}
