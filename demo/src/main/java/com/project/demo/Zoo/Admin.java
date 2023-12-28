package com.project.demo.Zoo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class Admin implements IEmployed {

    @PositiveOrZero(message = "Missing salary or worked months of the admin")
    private int salary = -1, workedMonths = -1;

    @NotNull(message = "Missing admin name")
    public final String name;

    @NotNull(message = "Unspecified sex for admin")
    public final Sex sex;

    @NotNull(message = "Password for admin is missing")
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
