package com.nazarenko.parallelcomputing.coursework;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class InvertedIndex implements Runnable {
    public List<File> filesList;
    public Map<String, Set<File>> index = new HashMap<>();

    public InvertedIndex(List<File> files) {
        this.filesList = files;
    }

    public Map<String, Set<File>> getIndex() {
        return this.index;
    }

    @Override
    public void run() {
        for (File currentFile : filesList) {
            try {
                BufferedReader file = new BufferedReader(new FileReader(currentFile));
                String fileData = file.readLine().replaceAll("[^a-zA-Z ]", "").toLowerCase().trim();
                List<String> fileWords = new ArrayList<>(Arrays.asList(fileData.split("\\s+")));

                for (String currentWord : fileWords) {
                    if ( ! index.containsKey(currentWord)) {
                        index.put(currentWord, new HashSet<>());
                    }
                    index.get(currentWord).add(currentFile.getAbsoluteFile());
                }
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
