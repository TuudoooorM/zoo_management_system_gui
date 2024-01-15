package com.project.demo.Database;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.Utils.Constants;
import com.project.demo.Zoo.*;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
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
            query("DELETE FROM " + tableName);
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean clearAllTables() {
        return clearTableRows("zookeepers") && clearTableRows("enclosures") && clearTableRows("animals");
    }

    public static boolean clearConfigFile() {
        URL configFilePathURL = ZooApplication.class.getResource("config");
        if (configFilePathURL == null) {
            Platform.exit();
            return false;
        }
        String configFilePath = configFilePathURL.getPath() + "config.ini";
        try (FileWriter ignored = new FileWriter(configFilePath, false)) {
            return true;
        } catch (IOException e) {
            System.err.println("Could not clear config file. Reason: " + e.getMessage());
            return false;
        }
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

    public static Pair<Zookeeper, String> tryFindZookeeper(String zookeeperIDRaw) {
        if (zookeeperIDRaw.length() != Constants.ID_SIZE) return new Pair<>(null, "ID length is incorrect");

        int zookeeperID;
        try {
            zookeeperID = Integer.parseInt(zookeeperIDRaw);
        } catch (NumberFormatException ignored) {
            return new Pair<>(null, "ID format is incorrect");
        }

        try (Connection connection = DriverManager.getConnection(Constants.CONNECTION_URL, "root", "admin")) {
            PreparedStatement zookeeperSearchStatement = connection.prepareStatement(
                    "SELECT * FROM zookeepers WHERE id = ? LIMIT 1"
            );
            setPreparedParameters(zookeeperSearchStatement, zookeeperID);

            ResultSet zookeepersResultSet = zookeeperSearchStatement.executeQuery();
            while(zookeepersResultSet.next()) {
                String name = zookeepersResultSet.getString("name");
                Sex sex = zookeepersResultSet.getInt("sex") == 0 ? Sex.male : Sex.female;
                int salary = zookeepersResultSet.getInt("salary");
                int workedMonths = zookeepersResultSet.getInt("worked_months");
                String job = zookeepersResultSet.getString("job");
                if (job.equals("null")) job = "";

                String password = zookeepersResultSet.getString("password");

                Zookeeper foundZookeeper = new Zookeeper(zookeeperIDRaw, name, job, sex, password);
                foundZookeeper.setSalary(salary);
                foundZookeeper.increaseWorkedMonths(workedMonths);
                return new Pair<>(foundZookeeper, null);
            }

            return new Pair<>(null, "There's no zookeeper with this ID");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new Pair<>(null, "There has been an internal error accessing zookeeper data");
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
                if (job == null) job = "";

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

    public static boolean tryAddAdminAndZooName(String zooName, String fullName, Sex sex, int yearlySalary, int workedMonths, String hashedPassword) {
        URL configFileURL = ZooApplication.class.getResource("config");
        if (configFileURL == null) {
            System.err.println("File path of config file is null.");
            return false;
        }
        String configFilePath = configFileURL.getPath() + "config.ini";

        try (Writer configFileWriter = new FileWriter(configFilePath, false)) {
            configFileWriter.write(
                    String.format("""
                            name=%s
                            admin_name=%s
                            admin_sex=%d
                            admin_password=%s
                            admin_salary=%d
                            admin_worked_months=%d"""
            , zooName, fullName, sex.ordinal(), hashedPassword, yearlySalary, workedMonths));
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }


    public static boolean tryInsertBatchZookeepers(Zoo zoo) {
        try (Connection connection = DriverManager.getConnection(Constants.CONNECTION_URL, "root", "admin")) {
            String zookeeperInsertionQuery = "INSERT INTO zookeepers (id, name, job, sex, salary, worked_months, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement zookeepersStatement = connection.prepareStatement(zookeeperInsertionQuery);

            for (Zookeeper zookeeper : zoo.zookeepers) {
                setPreparedParameters(zookeepersStatement,
                        zookeeper.getId(), zookeeper.name, !zookeeper.getJob().isEmpty() ? zookeeper.getJob() : null,
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

            String animalsInsertionQuery = "INSERT INTO animals (enclosure_id, name, species, sex, age, healthy) VALUES (?, ?, ?, ?, ?, ?)";
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
                            enclosure.getId(), animal.name, animal.species, animal.sex.ordinal(), animal.age, animal.healthy
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
            switch (data[i]) {
                case String stringParameter -> queryStatement.setString(i + 1, stringParameter);
                case Integer integerParameter -> queryStatement.setInt(i + 1, integerParameter);
                case Float floatParameter -> queryStatement.setFloat(i + 1, floatParameter);
                case null, default -> queryStatement.setObject(i + 1, data[i]);
            }
        }
    }

    public static boolean tryQueryPreparedStatement(String query, Object... data) {
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

    private static Pair<Zoo, String> loadConfig() {
        try (Scanner scanner = new Scanner(
                Objects.requireNonNull(
                        ZooApplication.class.getResourceAsStream("config/config.ini")
                )
        )) {
            if (!scanner.hasNextLine()) return new Pair<>(null, "The config file is incomplete");
            String[] zooNameRaw = scanner.nextLine().split("=");
            // name=the zoo name -> [name, the zoo name]
            if (zooNameRaw.length != 2 || !zooNameRaw[0].equals("name"))
                return new Pair<>(null, "Malformed zoo name");
            String zooName = zooNameRaw[1];

            if (!scanner.hasNextLine()) return new Pair<>(null, "The config file is incomplete");
            String[] adminNameRaw = scanner.nextLine().split("=");
            if (adminNameRaw.length != 2 || !adminNameRaw[0].equals("admin_name"))
                return new Pair<>(null, "Malformed admin name");
            String adminName = adminNameRaw[1];

            if (!scanner.hasNextLine()) return new Pair<>(null, "The config file is incomplete");
            String[] adminSexRaw = scanner.nextLine().split("=");
            if (adminSexRaw.length != 2 || !adminSexRaw[0].equals("admin_sex"))
                return new Pair<>(null, "Malformed admin sex");
            if (!adminSexRaw[1].equals("0") && !adminSexRaw[1].equals("1"))
                return new Pair<>(null, "Malformed admin sex value (not a 0 or a 1)");
            Sex adminSex = adminSexRaw[1].equals("0") ? Sex.male : Sex.female;

            if (!scanner.hasNextLine()) return new Pair<>(null, "The config file is incomplete");
            String[] adminPasswordRaw = scanner.nextLine().split("=");
            if (adminPasswordRaw.length != 2 || !adminPasswordRaw[0].equals("admin_password"))
                return new Pair<>(null, "Malformed admin password");
            String adminPassword = adminPasswordRaw[1];

            if (!scanner.hasNextLine()) return new Pair<>(null, "The config file is incomplete");
            String[] adminSalaryRaw = scanner.nextLine().split("=");
            if (adminSalaryRaw.length != 2 || !adminSalaryRaw[0].equals("admin_salary"))
                return new Pair<>(null, "Malformed admin salary");

            int adminSalary;
            try {
                adminSalary = Integer.parseInt(adminSalaryRaw[1]);
            } catch (NumberFormatException ignored) {
                return new Pair<>(null, "Malformed admin salary value (it's not a number)");
            }

            if (!scanner.hasNextLine()) return new Pair<>(null, "The config file is incomplete");
            String[] adminWorkedMonthsRaw = scanner.nextLine().split("=");
            if (adminWorkedMonthsRaw.length != 2 || !adminWorkedMonthsRaw[0].equals("admin_worked_months"))
                return new Pair<>(null, "Malformed admin worked months count");

            int adminWorkedMonths;
            try {
                adminWorkedMonths = Integer.parseInt(adminWorkedMonthsRaw[1]);
            } catch (NumberFormatException ignored) {
                return new Pair<>(null, "Malformed admin worked months count (it's not a number");
            }

            Zoo zoo = new Zoo(zooName);
            zoo.setAdmin(adminName, adminSex, adminSalary, adminWorkedMonths, adminPassword);
            return new Pair<>(zoo, null);
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
            return new Pair<>(null, "There's been an internal error regarding the config file (doesn't exist).");
        }
    }

    public static Pair<Zoo, String> load() {
        Pair<Zoo, String> zooData = loadConfig();
        if (zooData.getValue() != null) return zooData;

        Zoo zoo = zooData.getKey();

        boolean loadResult = ZooDatabaseManager.loadZookeepers(zoo) && ZooDatabaseManager.loadEnclosures(zoo) && ZooDatabaseManager.loadAnimals(zoo);
        if (!loadResult) return new Pair<>(null, "There's been an error loading zoo data.");

        return zooData;
    }
}