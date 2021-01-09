package com.nazarenko.parallelcomputing.coursework;

import java.io.File;
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

    }

}
