package com.nazarenko.parallelcomputing.coursework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Data {
    List<File> fileList = new ArrayList<>();

    File[] pathsToFiles = {
            new File("data/neg"),
            new File("data/neg-test"),
            new File("data/pos"),
            new File("data/pos-test"),
            new File("data/unsup")
    };

    public List<File> returnFiles() {
        for (File folder : pathsToFiles) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                fileList.add(file.getAbsoluteFile());
            }
        }
        return fileList;
    }
}
