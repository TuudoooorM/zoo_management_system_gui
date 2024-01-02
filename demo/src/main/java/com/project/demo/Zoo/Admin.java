package com.project.demo.Zoo;

public class Admin implements IEmployed {

    private int salary = -1, workedMonths = -1;

    public final String name;

    public final Sex sex;

    public final String password;

    public Admin() {
        this.name = null;
        this.sex = null;
        this.password = null;
    }

    public Admin(String name, Sex sex, String password) {
        this.name = name;
        this.sex = sex;
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
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
