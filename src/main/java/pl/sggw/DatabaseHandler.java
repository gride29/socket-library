package pl.sggw;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseHandler {

    public static String printSingleBook(Book book) {
        return "<p>" + "[" + book.getId() + "] [" + book.getTitle() + "] [" + book.getAuthor() + "]" + "</p>";
    }

    public static String dictToHTML(ConcurrentHashMap<Integer, Book> books) {
        String initialOutput = "[id]" + " [tytuł] " + " [autor] ";
        StringBuilder output = new StringBuilder(initialOutput);
        for (Book book : books.values()) {
            output.append(printSingleBook(book));
        }
        if (output.toString().equals(initialOutput)) {
            return "Brak książek";
        } else {
            return output.toString();
        }
    }

    public static ConcurrentHashMap<Integer, Book> readJsonFile() throws FileNotFoundException {
        File file = new File("src/main/resources/database.json");
        Scanner scan = new Scanner(file);
        StringBuilder json = new StringBuilder();
        while (scan.hasNextLine()) {
            json.append(scan.nextLine());
        }
        scan.close();
        return parseJson(json.toString());
    }

    private static ConcurrentHashMap<Integer, Book> parseJson(String json) {
        ConcurrentHashMap<Integer, Book> booksDictionary = new ConcurrentHashMap<>();
        if (json.length() == 0) {
            return booksDictionary;
        }
        json = json.substring(1, json.length() - 1);
        String[] entries = json.split("},");
        for (int i = 0; i < entries.length - 1; i++) {
            entries[i] += "}";
        }
        int id;
        String[] keyValuePair;
        String bookTitle, bookAuthor;
        for (String entry : entries) {
            keyValuePair = entry.trim().split(",", 3);
            id = Integer.parseInt(keyValuePair[0].substring(7));
            bookTitle = keyValuePair[1].trim().substring(10, keyValuePair[1].length() - 2);
            bookAuthor = keyValuePair[2].trim().substring(11, keyValuePair[2].length() - 3);
            booksDictionary.put(id, new Book(id, bookTitle, bookAuthor));
        }
        return booksDictionary;
    }

    public static void removeAllBooks() {
        Main.books = new ConcurrentHashMap<>();
    }
}
