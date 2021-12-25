package pl.sggw.server;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseHandler {

    public static String printSingleBook(Book book) {
        return "<p>" + "[" + book.getId() + "] [" + book.getTitle() + "] [" + book.getAuthorName() + "] [" + book.getAuthorSurname() + "]" + "</p>";
    }

    public static String dictToHTML(ConcurrentHashMap<Integer, Book> books) {
        String initialOutput = "[id]" + " [tytuł] " + " [imię autora] " + " [nazwisko autora] ";
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
        String bookTitle, bookAuthorName, bookAuthorSurname;
        for (String entry : entries) {
            keyValuePair = entry.trim().split(",", 4);
            id = Integer.parseInt(keyValuePair[0].substring(7));
            bookTitle = keyValuePair[1].trim().substring(10, keyValuePair[1].length() - 2);
            bookAuthorName = keyValuePair[2].trim().substring(15, keyValuePair[2].length() - 2);
            bookAuthorSurname = keyValuePair[3].trim().substring(18, keyValuePair[3].length() - 3);
            booksDictionary.put(id, new Book(id, bookTitle, bookAuthorName, bookAuthorSurname));
        }
        return booksDictionary;
    }

    public static void removeAllBooks() {
        Main.books = new ConcurrentHashMap<>();
    }
}
