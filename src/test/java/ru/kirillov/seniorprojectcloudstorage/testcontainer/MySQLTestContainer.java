package ru.kirillov.seniorprojectcloudstorage.testcontainer;

import org.testcontainers.containers.MySQLContainer;

public class MySQLTestContainer extends MySQLContainer<MySQLTestContainer> {

    public static final String IMAGE_VERSION = "mysql";
    public static final String DATABASE_NAME = "mysqlDataBase";
    public static MySQLContainer mySQLContainer;

    public MySQLTestContainer() {
        super(IMAGE_VERSION);
    }

    public static MySQLContainer getInstance() {
        if (mySQLContainer == null) {
            mySQLContainer = new MySQLTestContainer().withDatabaseName(DATABASE_NAME);
        }
        return mySQLContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("jdbc:mysql://localhost:3306/mysqlDataBase", mySQLContainer.getJdbcUrl());
        System.setProperty("root", mySQLContainer.getUsername());
        System.setProperty("root", mySQLContainer.getPassword());
    }

    @Override
    public void stop() {
    }
}
