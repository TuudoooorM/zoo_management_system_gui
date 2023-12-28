package com.project.demo.Utils;
import com.project.demo.Zoo.Privileges;

import java.util.Scanner;

public class InputReader {

    public static Privileges getPrivilegeFromInput(String rawPrivilegeInput) {
        return rawPrivilegeInput.equalsIgnoreCase("admin") ?
                Privileges.ADMIN :
                rawPrivilegeInput.equalsIgnoreCase("zookeeper") ?
                        Privileges.ZOOKEEPER : Privileges.GUEST;
    }

    public static int getSexFromInput(String sexRawInput) {
        return sexRawInput.equalsIgnoreCase("m") ? 0 : sexRawInput.equalsIgnoreCase("f") ? 1 : -1;
    }

    public static String readPasswordFromInput() {
        String password = null,
                userAnswerForWantingToInputPassword = "y";

        while (password == null && userAnswerForWantingToInputPassword.equals("y")) {
            password = InputReader.tryReadLineFromConsole("Please input the password: ");
            if (password == null) {
                userAnswerForWantingToInputPassword = InputReader.tryReadLineFromConsole("Do you want to try again to input the password? (y/n)");
                if (userAnswerForWantingToInputPassword == null) return null;
            }
        }

        return password;
    }
    public static String readZookeeperIDFromInput() {
        String zookeeperID = null,
                userAnswerForWantingToInputID = "y";

        while (zookeeperID == null && userAnswerForWantingToInputID.equals("y")) {
            zookeeperID = InputReader.tryReadLineFromConsole("Please input your zookeeper id: ");
            if (zookeeperID == null) {
                userAnswerForWantingToInputID = InputReader.tryReadLineFromConsole("Do you want to try again to input your zookeeper id? (y/n)");
                if (userAnswerForWantingToInputID == null) return null;
            }
        }

        return zookeeperID;
    }

    public static String tryReadLineFromConsole(String leadingMessage) {
        System.out.print(leadingMessage);

        String input;
        try {
            Scanner scanner = new Scanner(System.in);
            input = scanner.nextLine();
        } catch (RuntimeException error) {
            System.err.printf("\nThere has been an error reading input. Cause:\n%s\n", error);
            return null;
        }

        return input;
    }
}
