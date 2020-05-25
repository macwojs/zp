package edu.agh.zp.objects;

import java.util.ArrayList;
import java.util.List;

public class Chart {
    public String title;
    public List<StatisticRecord> data;

    public Chart(String title){
        this.title = title;
        this.data = new ArrayList<>();
    }

    public Chart(String title, List<StatisticRecord> data) {
        this.title = title;
        this.data = data;
    }
}
