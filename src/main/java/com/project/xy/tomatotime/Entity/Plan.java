package com.project.xy.tomatotime.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Plan {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String PlanTitle;
    private String PlanLevel;
    private String EndTime;
    private String PlanTomatoNum;
    private String Remarks;
    @Generated(hash = 473628056)
    public Plan(Long id, String PlanTitle, String PlanLevel, String EndTime,
            String PlanTomatoNum, String Remarks) {
        this.id = id;
        this.PlanTitle = PlanTitle;
        this.PlanLevel = PlanLevel;
        this.EndTime = EndTime;
        this.PlanTomatoNum = PlanTomatoNum;
        this.Remarks = Remarks;
    }
    @Generated(hash = 592612124)
    public Plan() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPlanTitle() {
        return this.PlanTitle;
    }
    public void setPlanTitle(String PlanTitle) {
        this.PlanTitle = PlanTitle;
    }
    public String getPlanLevel() {
        return this.PlanLevel;
    }
    public void setPlanLevel(String PlanLevel) {
        this.PlanLevel = PlanLevel;
    }
    public String getEndTime() {
        return this.EndTime;
    }
    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }
    public String getPlanTomatoNum() {
        return this.PlanTomatoNum;
    }
    public void setPlanTomatoNum(String PlanTomatoNum) {
        this.PlanTomatoNum = PlanTomatoNum;
    }
    public String getRemarks() {
        return this.Remarks;
    }
    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }
}
