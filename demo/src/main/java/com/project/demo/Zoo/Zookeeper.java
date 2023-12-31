package com.project.demo.Zoo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Random;
import java.util.UUID;

public class Zookeeper implements IEmployed {
    @PositiveOrZero(message = "Missing salary or worked months of a zookeeper")
    private int salary = 0, workedMonths = 0;

    public String getId() {
        return id;
    }

    @NotNull(message = "Missing the id of a zookeeper")
    private final String id;

    @NotNull(message = "Missing the name of a zookeeper")
    public final String name;

    public String job;

    @NotNull(message = "Missing the sex of a zookeeper")
    public final Sex sex;

    @NotNull(message = "Password for zookeeper is missing")
    private final String password;


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Zookeeper() {
        this.id = null;
        this.name = null;
        this.job = null;
        this.sex = null;
        this.password = null;
    }
    public Zookeeper(String name, String job, Sex sex, String password) {
        Random random = new Random();
        this.id = String.valueOf(random.nextInt(9999 - 1000) + 1000);
        this.name = name;
        this.job = job;
        this.sex = sex;
        this.password = password;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String newJob) {
        job = newJob;
    }

    @Override
    public int getSalary() {
        return salary;
    }

    @Override
    public int getWorkedMonths() {
        return workedMonths;
    }

    @Override
    public void setSalary(int newSalary) {
        salary = newSalary;
    }

    @Override
    public void increaseWorkedMonths(int count) {
        if (workedMonths == -1) workedMonths = 0;
        workedMonths += count;
    }

}
