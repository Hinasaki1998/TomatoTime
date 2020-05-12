package com.project.xy.tomatotime.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Occupied {
    @Id(autoincrement = true)
    private Long id;

    private int Time;
    private boolean isoccupied;
    @Generated(hash = 1367340429)
    public Occupied(Long id, int Time, boolean isoccupied) {
        this.id = id;
        this.Time = Time;
        this.isoccupied = isoccupied;
    }
    @Generated(hash = 1456290022)
    public Occupied() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getTime() {
        return this.Time;
    }
    public void setTime(int Time) {
        this.Time = Time;
    }
    public boolean getIsoccupied() {
        return this.isoccupied;
    }
    public void setIsoccupied(boolean isoccupied) {
        this.isoccupied = isoccupied;
    }
}
