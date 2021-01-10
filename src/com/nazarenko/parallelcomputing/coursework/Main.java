package com.nazarenko.parallelcomputing.coursework;

import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    private static final List<File> FILES = new Data().returnFiles();

    public static void main(String[] args) {
        final int threadsAmount;
        Scanner scanner = new Scanner(System.in);
        HashMap<String, HashSet<File>> finalIndexSet = new HashMap<>();

        threadsAmount = setThreadsAmount(scanner);

        InvertedIndex[] invertedIndexThreads = new InvertedIndex[threadsAmount];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadsAmount; i++) {
            List<File> partOfFiles = FILES.subList(FILES.size() / threadsAmount * i,
                    i == (threadsAmount - 1) ? FILES.size() : FILES.size() / threadsAmount * (i + 1));
            invertedIndexThreads[i] = new InvertedIndex(partOfFiles);
            invertedIndexThreads[i].start();
            System.out.println(invertedIndexThreads[i].getName() + " starts execution");
        }

        for (int i = 0; i < threadsAmount; i++) {
            try {
                invertedIndexThreads[i].join();
                System.out.println(invertedIndexThreads[i].getName() + " ends execution");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long threadsTime = System.currentTimeMillis();

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

        long finishTime = System.currentTimeMillis();

        long totalThreadsTime = threadsTime - startTime;
        long totalTime = finishTime - startTime;
        long totalMinutes = totalTime / 1000 / 60;
        long totalSeconds = totalTime / 1000 - totalMinutes * 60;
        long totalMilliseconds = totalTime - totalSeconds * 1000 - totalMinutes * 1000 * 60;

        String info = getExecutionInfo(totalMinutes, totalSeconds, totalMilliseconds);
        System.out.println("Working time: " +
                "threads = " + totalThreadsTime + " ms, " +
                "program = " + totalTime + " ms");
        System.out.println(info);
        writeIndexToFile(finalIndexSet, info);
        searchWord(finalIndexSet, scanner);

    }

    private static int setThreadsAmount(Scanner scanner) {
        int threadsAmount;
        do {
            System.out.print("Write amount of threads:\n-> ");
            threadsAmount = scanner.nextInt();
        } while (!(threadsAmount > 0));
        return threadsAmount;
    }

    private static String getExecutionInfo(long totalMinutes, long totalSeconds, long totalMilliseconds) {
        return "Inverted index has been build in [" +
                totalMinutes + " m " +
                totalSeconds + " s " +
                totalMilliseconds + " ms]";
    }

    private static boolean searchWord(HashMap<String, HashSet<File>> finalIndexSet, Scanner scanner) {
        System.out.print("Write word to search:\n-> ");
        if (scanner.hasNext()) {
            String searchWord = scanner.next();
            System.out.println(finalIndexSet.get(searchWord));
            return true;
        }
        return false;
    }

    private static boolean writeIndexToFile(HashMap<String, HashSet<File>> finalIndexSet, String info) {
        try (FileWriter outputWriter = new FileWriter("inverted-index.txt")) {
            outputWriter.write(info + "\n\n");
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
