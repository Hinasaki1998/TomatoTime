package com.project.xy.tomatotime.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Schedule {
    @Id(autoincrement = true)
    private Long id;
    private String Memorandum;
    private String StartTime;
    private String EstimatedTime;
    private String Remarks;
    private String type;
    @Generated(hash = 1434848770)
    public Schedule(Long id, String Memorandum, String StartTime,
            String EstimatedTime, String Remarks, String type) {
        this.id = id;
        this.Memorandum = Memorandum;
        this.StartTime = StartTime;
        this.EstimatedTime = EstimatedTime;
        this.Remarks = Remarks;
        this.type = type;
    }
    @Generated(hash = 729319394)
    public Schedule() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMemorandum() {
        return this.Memorandum;
    }
    public void setMemorandum(String Memorandum) {
        this.Memorandum = Memorandum;
    }
    public String getStartTime() {
        return this.StartTime;
    }
    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }
    public String getEstimatedTime() {
        return this.EstimatedTime;
    }
    public void setEstimatedTime(String EstimatedTime) {
        this.EstimatedTime = EstimatedTime;
    }
    public String getRemarks() {
        return this.Remarks;
    }
    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
