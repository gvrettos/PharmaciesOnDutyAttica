package com.dstym.pharmaciesondutyattica.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "working-hours")
public class WorkingHour {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "working-hour-text")
    private String workingHourText;

    public WorkingHour() {

    }

    public WorkingHour(int id, String workingHourText) {
        this.id = id;
        this.workingHourText = workingHourText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkingHourText() {
        return workingHourText;
    }

    public void setWorkingHourText(String workingHourText) {
        this.workingHourText = workingHourText;
    }

    @Override
    public String toString() {
        return "entity.WorkingHour{" +
                "id=" + id +
                ", workingHourText='" + workingHourText + '\'' +
                '}';
    }
}
