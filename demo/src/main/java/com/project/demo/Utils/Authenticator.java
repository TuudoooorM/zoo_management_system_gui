package com.project.demo.Utils;

import com.project.demo.Exceptions.AuthenticationException;
import com.project.demo.Zoo.IEmployed;
import com.project.demo.Zoo.Privileges;
import com.project.demo.Zoo.Zookeeper;
import com.project.demo.Zoo.Zoo;

public abstract class Authenticator {
    public static Privileges privilege = Privileges.GUEST;
    public static IEmployed employee = null;

    private static boolean authenticateZookeeper(Zoo zoo) throws AuthenticationException {
        String zookeeperID = InputReader.readZookeeperIDFromInput();
        if (zookeeperID == null) return false;

        Zookeeper authenticatedZookeeper = zoo.zookeepers
                .stream()
                .filter(zookeeper -> zookeeper.getId().equals(zookeeperID))
                .findFirst()
                .orElse(null);

        if (authenticatedZookeeper == null)
            throw new AuthenticationException("There's no zookeeper with this id.");

        String password = InputReader.readPasswordFromInput();
        if (password == null) return false;

        if (authenticatedZookeeper.getPassword().equals(password)) {
            Authenticator.privilege = Privileges.ZOOKEEPER;
            Authenticator.employee = authenticatedZookeeper;
            return true;
        }

        throw new AuthenticationException("The password for this zookeeper is incorrect.");
    }

    private static boolean authenticateAdmin(Zoo zoo) throws AuthenticationException {
        String password = InputReader.readPasswordFromInput();
        if (password == null) return false;

        if (zoo.admin.password.equals(password)) {
            Authenticator.privilege = Privileges.ADMIN;
            Authenticator.employee = zoo.admin;
            return true;
        }

        throw new AuthenticationException("The password for the admin is incorrect.");
    }

    public static boolean authenticate(Zoo zoo, String rawPrivilegeMode) throws AuthenticationException {
        Privileges privilege = InputReader.getPrivilegeFromInput(rawPrivilegeMode);

        if (privilege == Privileges.ZOOKEEPER)
            return authenticateZookeeper(zoo);

        if (privilege == Privileges.ADMIN)
            return authenticateAdmin(zoo);

        Authenticator.privilege = privilege;
        return true;
    }
}
