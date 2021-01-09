package com.nazarenko.parallelcomputing.coursework;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final List<File> FILES = new Data().returnFiles();

    public static void main(String[] args) {
        final int threadsAmount;
        HashMap<String, HashSet<File>> finalIndexSet = new HashMap<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Write amount of threads:");
        threadsAmount = scanner.nextInt();
        InvertedIndex[] invertedIndexThreads = new InvertedIndex[threadsAmount];

        for (int i = 0; i < threadsAmount; i++) {
            List<File> partOfFiles = FILES.subList(FILES.size() / threadsAmount * i,
                    i == (threadsAmount - 1) ? FILES.size() : FILES.size() / threadsAmount * (i + 1));
            invertedIndexThreads[i] = new InvertedIndex(partOfFiles);
            invertedIndexThreads[i].start();
        }

        for (int i = 0; i < threadsAmount; i++) {
            try {
                invertedIndexThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < threadsAmount; i++) {
            invertedIndexThreads[i].getIndex()
                    .forEach((key, value) -> finalIndexSet
                            .merge(key, value, (associatedValue, newValue) -> {
                                HashSet<File> existedFiles = new HashSet<>(associatedValue);
                                existedFiles.addAll(newValue);
                                return existedFiles;
                            })
                    );
        }

        try (FileWriter outputWriter = new FileWriter("inverted-index.txt")) {
            for (Map.Entry<String, HashSet<File>> wordDoc : finalIndexSet.entrySet()) {
                String word = wordDoc.getKey();
                HashSet<File> docWordCount = wordDoc.getValue();
                outputWriter.write("[" + word + "] appears " + docWordCount.size() + " times in:\n");

                for (File docFrequency : docWordCount) {
                    String document = docFrequency.toString();
                    outputWriter.write("\t" + document + "\n");
                }
                outputWriter.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
