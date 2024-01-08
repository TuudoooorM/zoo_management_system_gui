package com.project.demo.Utils;

public abstract class Constants {
    public static final String DATABASE_SERVER_HOST = "localhost";
    public static final int DATABASE_SERVER_PORT = 3306;
    public static final String DATABASE_NAME = "zoo";

    public static final String CONNECTION_URL = String.format(
            "jdbc:mariadb://%s:%d/%s",
            DATABASE_SERVER_HOST,
            DATABASE_SERVER_PORT,
            DATABASE_NAME
    );

    public static final int ID_SIZE = 4;

    public static final int THREAD_COUNT = 4;
}
