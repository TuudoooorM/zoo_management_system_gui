package com.project.demo;
import com.project.demo.Zoo.Sex;
import com.project.demo.Zoo.Zookeeper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ZooInputDataStore {
    private static final HashMap<String, Object> zooInputData = new HashMap<>();
    private static ArrayList<Zookeeper> zookeepers;

    public static void setAdminInput(String firstName, String lastName, Sex sex, int yearlySalary, int workedMonths, String password) {
        zooInputData.put("firstName", firstName);
        zooInputData.put("lastName", lastName);
        zooInputData.put("sex", sex);
        zooInputData.put("yearlySalary", yearlySalary);
        zooInputData.put("workedMonths", workedMonths);
        zooInputData.put("password", password);
    }



    public static HashMap<String, Object> getData() {
        return zooInputData;
    }

    public static void setZookeeperInput(String firstName, String lastName, Sex sex, int yearlySalary, int workedMonths, String job, String password) {
        if (!zooInputData.containsKey("zookeepers"))
            zooInputData.put("zookeepers", zookeepers);

        Zookeeper zookeeper = new Zookeeper(firstName + " " + lastName, job, sex, password);
        zookeeper.setSalary(yearlySalary);
        zookeeper.increaseWorkedMonths(workedMonths);
        zookeepers.add(zookeeper);
    }
}
