package com.project.demo.Database;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.Utils.Constants;
import com.project.demo.Zoo.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class ZooDatabaseManager {
    private static ResultSet query(String sqlQuery) throws SQLException {
        try (Connection connection = DriverManager.getConnection(Constants.CONNECTION_URL, "root", "admin")) {
            PreparedStatement queryStatement = connection.prepareStatement(sqlQuery);

            return queryStatement.executeQuery();
        }
    }

    public static boolean clearTableRows(String tableName) {
        try {
            query("TRUNCATE TABLE " + tableName);
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean clearAllTables() {
        return clearTableRows("zookeepers") && clearTableRows("enclosures") && clearTableRows("animals");
    }

    public static boolean loadAnimals(Zoo zoo) {
        try (ResultSet animalsResultSet = query("SELECT * FROM animals")) {
            while (animalsResultSet.next()) {
                String name = animalsResultSet.getString("name");
                String species = animalsResultSet.getString("species");
                int age = animalsResultSet.getInt("age");
                Sex sex = animalsResultSet.getInt("sex") == 0 ? Sex.male : Sex.female;
                boolean healthy = animalsResultSet.getBoolean("healthy");

                zoo.addAnimal(name, species, sex, age, healthy);
            }

            return true;
        } catch (SQLException | EnclosureCapacityExceededException | MissingEnclosureException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean loadEnclosures(Zoo zoo) {
        try (ResultSet enclosuresResultSet = query("SELECT * FROM enclosures")) {
            while (enclosuresResultSet.next()) {
                int id = enclosuresResultSet.getInt("id");
                String enclosureSpecies = enclosuresResultSet.getString("species");
                int capacity = enclosuresResultSet.getInt("capacity");
                float width = enclosuresResultSet.getFloat("width");
                float height = enclosuresResultSet.getFloat("height");
                float length = enclosuresResultSet.getFloat("length");

                zoo.addEnclosure(String.valueOf(id), enclosureSpecies, capacity, width, height, length);
            }

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean loadZookeepers(Zoo zoo) {
        try (ResultSet zookeepersResultSet = query("SELECT * FROM zookeepers")) {
            while (zookeepersResultSet.next()) {
                int id = zookeepersResultSet.getInt("id");
                String name = zookeepersResultSet.getString("name");
                Sex sex = zookeepersResultSet.getInt("sex") == 0 ? Sex.male : Sex.female;
                int salary = zookeepersResultSet.getInt("salary");
                int workedMonths = zookeepersResultSet.getInt("worked_months");
                String job = zookeepersResultSet.getString("job");
                if (job.equals("null")) job = "";

                String password = zookeepersResultSet.getString("password");

                zoo.addZookeeper(String.valueOf(id), name, job, sex, salary, workedMonths, password);
            }
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            zoo.zookeepers.clear();
            return false;
        }
    }

    public static boolean tryAddAdmin(String fullName, Sex sex, int yearlySalary, int workedMonths, String hashedPassword) {
//        try (Connection connection = DriverManager.getConnection(Constants.CONNECTION_URL, "root", "admin")) {
//            PreparedStatement queryStatement = connection.prepareStatement("INSERT INTO employees (id,) VALUES ");
//
//            return queryStatement.executeQuery();
//        }
        // TODO: this
        return true;
    }


    public static boolean tryInsertBatchZookeepers(Zoo zoo) {
        try (Connection connection = DriverManager.getConnection(Constants.CONNECTION_URL, "root", "admin")) {
            String zookeeperInsertionQuery = "INSERT INTO zookeepers (id, name, job, sex, salary, worked_months, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement zookeepersStatement = connection.prepareStatement(zookeeperInsertionQuery);

            for (Zookeeper zookeeper : zoo.zookeepers) {
                setPreparedParameters(zookeepersStatement,
                        zookeeper.getId(), zookeeper.name, zookeeper.getJob(),
                        zookeeper.sex.ordinal(), zookeeper.getSalary(), zookeeper.getWorkedMonths(),
                        zookeeper.getPassword()
                );

                zookeepersStatement.addBatch();
            }
            zookeepersStatement.executeBatch();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean tryInsertBatchEnclosuresAndAnimals(Zoo zoo) {
        try (Connection connection = DriverManager.getConnection(Constants.CONNECTION_URL, "root", "admin")) {
            String enclosuresInsertionQuery = "INSERT INTO enclosures (id, species, capacity, width, height, length) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement enclosuresStatement = connection.prepareStatement(enclosuresInsertionQuery);

            String animalsInsertionQuery = "INSERT INTO animals (name, species, sex, age, healthy) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement animalsStatement = connection.prepareStatement(animalsInsertionQuery);
            for (Enclosure enclosure : zoo.enclosures) {
                setPreparedParameters(enclosuresStatement,
                        enclosure.getId(), enclosure.speciesHoused, enclosure.capacity + enclosure.animals.size(),
                        enclosure.getWidth(), enclosure.getHeight(), enclosure.getLength()
                );
                enclosuresStatement.addBatch();

                for (Animal animal : enclosure.animals) {
                    setPreparedParameters(
                            animalsStatement,
                            animal.name, animal.species, animal.sex.ordinal(), animal.age, animal.healthy
                    );
                    animalsStatement.addBatch();
                }
            }

            enclosuresStatement.executeBatch();
            animalsStatement.executeBatch();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static void setPreparedParameters(PreparedStatement queryStatement, Object... data) throws SQLException {
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof String)
                queryStatement.setString(i + 1, (String) data[i]);
            else if (data[i] instanceof Integer)
                queryStatement.setInt(i + 1, (Integer) data[i]);
            else if (data[i] instanceof Float)
                queryStatement.setFloat(i + 1, (Float) data[i]);
            else
                queryStatement.setObject(i + 1, data[i]); // TODO: check
        }
    }

    public static boolean tryInsertPrepared(String query, Object... data) {
        // TODO: debug
        try (Connection connection = DriverManager.getConnection(Constants.CONNECTION_URL, "root", "admin")) {
            PreparedStatement queryStatement = connection.prepareStatement(query);
            setPreparedParameters(queryStatement, data);
            queryStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /*
    C:\Users\matei\.jdks\openjdk-21\bin\java.exe
    "-javaagent:F:\IntelliJ IDEA 2023.2.2\lib\idea_rt.jar=60150:F:\IntelliJ IDEA 2023.2.2\bin"
    -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8
    -classpath C:\Users\matei\.m2\repository\org\openjfx\javafx-controls\21-ea+24\javafx-controls-21-ea+24.jar;
    C:\Users\matei\.m2\repository\org\openjfx\javafx-graphics\21-ea+24\javafx-graphics-21-ea+24.jar;
    C:\Users\matei\.m2\repository\org\openjfx\javafx-base\21-ea+24\javafx-base-21-ea+24.jar;
    C:\Users\matei\.m2\repository\org\openjfx\javafx-fxml\21-ea+24\javafx-fxml-21-ea+24.jar;
    C:\Users\matei\.m2\repository\org\springframework\spring-aop\6.1.2\spring-aop-6.1.2.jar;
    C:\Users\matei\.m2\repository\org\springframework\spring-beans\6.1.2\spring-beans-6.1.2.jar;C:\Users\matei\.m2\repository\org\springframework\spring-context\6.1.2\spring-context-6.1.2.jar;C:\Users\matei\.m2\repository\org\springframework\spring-core\6.1.2\spring-core-6.1.2.jar;C:\Users\matei\.m2\repository\org\springframework\spring-jcl\6.1.2\spring-jcl-6.1.2.jar;C:\Users\matei\.m2\repository\org\springframework\spring-expression\6.1.2\spring-expression-6.1.2.jar;C:\Users\matei\.m2\repository\io\micrometer\micrometer-observation\1.12.1\micrometer-observation-1.12.1.jar;C:\Users\matei\.m2\repository\io\micrometer\micrometer-commons\1.12.1\micrometer-commons-1.12.1.jar;C:\Users\matei\.m2\repository\org\slf4j\jcl-over-slf4j\2.0.7\jcl-over-slf4j-2.0.7.jar;C:\Users\matei\.m2\repository\com\github\ben-manes\caffeine\caffeine\2.9.3\caffeine-2.9.3.jar;C:\Users\matei\.m2\repository\com\google\errorprone\error_prone_annotations\2.10.0\error_prone_annotations-2.10.0.jar;C:\Users\matei\.m2\repository\org\checkerframework\checker-qual\3.32.0\checker-qual-3.32.0.jar -p C:\Users\matei\.m2\repository\com\github\waffle\waffle-jna\3.3.0\waffle-jna-3.3.0.jar;C:\Users\matei\.m2\repository\org\kordamp\ikonli\ikonli-core\12.3.1\ikonli-core-12.3.1.jar;C:\Users\matei\.m2\repository\org\mariadb\jdbc\mariadb-java-client\3.3.2\mariadb-java-client-3.3.2.jar;C:\Users\matei\.m2\repository\org\openjfx\javafx-graphics\21-ea+24\javafx-graphics-21-ea+24-win.jar;C:\Users\matei\.m2\repository\org\openjfx\javafx-controls\21-ea+24\javafx-controls-21-ea+24-win.jar;C:\Users\matei\.m2\repository\org\kordamp\ikonli\ikonli-fontawesome5-pack\12.3.1\ikonli-fontawesome5-pack-12.3.1.jar;C:\Users\matei\.m2\repository\net\java\dev\jna\jna\5.13.0\jna-5.13.0.jar;C:\Users\matei\.m2\repository\net\java\dev\jna\jna-platform\5.13.0\jna-platform-5.13.0.jar;C:\Users\matei\.m2\repository\org\openjfx\javafx-base\21-ea+24\javafx-base-21-ea+24-win.jar;C:\Users\matei\.m2\repository\org\springframework\security\spring-security-core\6.2.1\spring-security-core-6.2.1.jar;F:\P3_Project_JavaFX\demo\target\classes;C:\Users\matei\.m2\repository\org\kordamp\ikonli\ikonli-javafx\12.3.1\ikonli-javafx-12.3.1.jar;C:\Users\matei\.m2\repository\net\synedra\validatorfx\0.4.0\validatorfx-0.4.0.jar;C:\Users\matei\.m2\repository\org\openjfx\javafx-fxml\21-ea+24\javafx-fxml-21-ea+24-win.jar;C:\Users\matei\.m2\repository\org\slf4j\slf4j-api\2.0.7\slf4j-api-2.0.7.jar;C:\Users\matei\.m2\repository\org\springframework\security\spring-security-crypto\6.2.1\spring-security-crypto-6.2.1.jar -m com.project.demo/com.project.demo.ZooApplication
     */

    private static Zoo loadConfig() {
        // FIXME: path error. obviously >_>
        try (Scanner scanner = new Scanner(new FileInputStream("config/config.ini"))) {
            if (!scanner.hasNextLine()) return null;
            String[] zooNameRaw = scanner.nextLine().split("=");
            // name=the zoo name -> [name, the zoo name]
            if (zooNameRaw.length != 2) return null;
            String zooName = zooNameRaw[1];

            if (!scanner.hasNextLine()) return null;
            String[] adminNameRaw = scanner.nextLine().split("=");
            if (adminNameRaw.length != 2) return null;
            String adminName = adminNameRaw[1];

            if (!scanner.hasNextLine()) return null;
            String[] adminSexRaw = scanner.nextLine().split("=");
            if (adminSexRaw.length != 2) return null;
            if (!adminSexRaw[1].equals("0") && !adminSexRaw[1].equals("1")) return null;
            Sex adminSex = adminSexRaw[1].equals("0") ? Sex.male : Sex.female;

            if (!scanner.hasNextLine()) return null;
            String[] adminPasswordRaw = scanner.nextLine().split("=");
            if (adminPasswordRaw.length != 2) return null;
            String adminPassword = adminPasswordRaw[1];


            if (!scanner.hasNextLine()) return null;
            String[] adminSalaryRaw = scanner.nextLine().split("=");
            if (adminSalaryRaw.length != 2) return null;
            int adminSalary;
            try {
                adminSalary = Integer.parseInt(adminSalaryRaw[1]);
            } catch (NumberFormatException ignored) {
                return null;
            }

            if (!scanner.hasNextLine()) return null;
            String[] adminWorkedMonthsRaw = scanner.nextLine().split("=");
            if (adminWorkedMonthsRaw.length != 2) return null;
            int adminWorkedMonths;
            try {
                adminWorkedMonths = Integer.parseInt(adminWorkedMonthsRaw[1]);
            } catch (NumberFormatException ignored) {
                return null;
            }

            Zoo zoo = new Zoo(zooName);
            zoo.setAdmin(adminName, adminSex, adminSalary, adminWorkedMonths, adminPassword);
            return zoo;
        } catch (IOException error) {
            System.err.println(error.getMessage());
            return null;
        }
    }

    public static Zoo load() {
        Zoo zoo = loadConfig();
        if (zoo == null) return null;

        boolean loadResult = ZooDatabaseManager.loadZookeepers(zoo) && ZooDatabaseManager.loadEnclosures(zoo) && ZooDatabaseManager.loadAnimals(zoo);
        if (!loadResult) return null;

        return zoo;
    }
}