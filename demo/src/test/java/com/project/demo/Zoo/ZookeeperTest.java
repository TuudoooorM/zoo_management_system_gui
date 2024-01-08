package com.project.demo.Zoo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZookeeperTest {
    @Test
    void testZookeeperConstructor() {
        Zookeeper zookeeper1 = new Zookeeper("1234", "test", "job", Sex.female, "password");
        zookeeper1.setSalary(1000);
        zookeeper1.increaseWorkedMonths(10);

        assertEquals("1234", zookeeper1.getId());
        assertEquals("test", zookeeper1.getName());
        assertEquals("job", zookeeper1.getJob());
        assertEquals(Sex.female, zookeeper1.sex);
        assertEquals("password", zookeeper1.getPassword());

        Zookeeper zookeeper2 = new Zookeeper("test", "job", Sex.female, "password");
        assertEquals(4, zookeeper2.getId().length());
        assertEquals("test", zookeeper2.getName());
        assertEquals("job", zookeeper2.getJob());
        assertEquals(Sex.female, zookeeper2.sex);
        assertEquals("password", zookeeper2.getPassword());
    }
}