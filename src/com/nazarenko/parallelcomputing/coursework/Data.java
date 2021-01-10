package com.nazarenko.parallelcomputing.coursework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Data {
    List<File> filesList = new ArrayList<>();

    File[] pathsToData = {
            new File("data/1"), // change pathname to run project
            new File("data/2"), // change pathname to run project
            };

    public List<File> returnFiles() {
        for (File folder : pathsToData) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                filesList.add(file.getAbsoluteFile());
            }
        }
        return filesList;
    }
}
