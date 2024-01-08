package com.project.demo.Utils;

import com.project.demo.Exceptions.AuthorisationException;
import com.project.demo.Zoo.Admin;
import com.project.demo.Zoo.IEmployed;
import com.project.demo.Zoo.Privileges;

public abstract class Authenticator {

    public static Privileges getPrivilege() {
        return privilege;
    }

    public static IEmployed getEmployee() {
        return employee;
    }

    private static Privileges privilege = Privileges.GUEST;
    private static IEmployed employee = null;

    public static void authenticate(Privileges receivedPrivilege, IEmployed receivedEmployee) {
        privilege = receivedPrivilege;
        employee = receivedEmployee;
    }

    public static void authorise(Privileges receivedPrivilege) throws AuthorisationException {
        if (receivedPrivilege == Privileges.ZOOKEEPERS_AND_ABOVE && employee == null)
            throw new AuthorisationException("Unauthorised operation. Only zookeepers and above allowed.");

        if (receivedPrivilege == Privileges.ADMIN && !(employee instanceof Admin))
            throw new AuthorisationException("Unauthorised operation. Only admin is allowed.");

    }
}
