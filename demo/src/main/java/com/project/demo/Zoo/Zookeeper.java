package com.project.demo.Zoo;

import com.project.demo.Utils.Randoms;

public class Zookeeper implements IEmployed {
    private int salary = 0, workedMonths = 0;

    private final String id;

    public final String name;

    public String job;

    public final Sex sex;

    private final String password;


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Zookeeper(String id, String name, String job, Sex sex, String password) {
        this.id = id;
        this.name = name;
        this.job = job;
        this.sex = sex;
        this.password = password;
    }

    public Zookeeper(String name, String job, Sex sex, String password) {
        this.id = String.valueOf(Randoms.getRandomNumberBetween(1000, 9999));
        this.name = name;
        this.job = job;
        this.sex = sex;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getJob() {
        return job;
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
