package com.project.demo.Database;

import com.project.demo.Zoo.Zoo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class JsonFileSystemDatabase {
    private final String databaseFilePath;

    public JsonFileSystemDatabase(String databaseFilePath) {
        this.databaseFilePath = databaseFilePath;
    }

    public void open() throws IOException {
        File databaseFile = new File(this.databaseFilePath);
        if(databaseFile.createNewFile())
            throw new FileNotFoundException("The database file is missing.");

    }

    public boolean tryOpen() {
        try {
            this.open();
            return true;
        } catch (FileNotFoundException error) {
            System.err.printf("\nThere is no database at path \"%s\"\n", this.databaseFilePath);
            return false;
        } catch (IOException error)
        {
            System.err.printf("\nThe following error occurred whilst trying to open the database stored in file: \"%s\"\n%s\n", this.databaseFilePath, error);
            return false;
        }
    }

    public Zoo load() throws JsonProcessingException, FileNotFoundException {
        StringBuilder rawFileContents = new StringBuilder();

        try (Scanner scanner = new Scanner(new FileInputStream(databaseFilePath))) {
            while (scanner.hasNextLine()) rawFileContents.append(scanner.nextLine());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(rawFileContents.toString(), Zoo.class);
    }

    private void save(Zoo zoo) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String serializedZooData = objectMapper.writeValueAsString(zoo);
        Files.writeString(Path.of(this.databaseFilePath), serializedZooData);
    }

    public boolean trySave(Zoo zoo) {
       try {
           this.save(zoo);
           return true;
       } catch (JsonProcessingException error) {
           System.err.printf("There has been an error with the serialization of JSON whilst trying to save data to database \"%s\"\n%s\n", this.databaseFilePath, error);
           return false;
       } catch (IOException error) {
           System.err.printf("The following I/O error occurred whilst trying to save the state of the zoo to the database \"%s\"\n%s\n", this.databaseFilePath, error);
           return false;
       }
    }
}
