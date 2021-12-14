package pl.sggw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class RouteHandler {

    // Reads HTML file stored in resources folder
    public static String readHTMLFile(String path) throws FileNotFoundException {
        StringBuilder html = new StringBuilder();
        File htmlFile = new File(path);
        Scanner scan = new Scanner(htmlFile);
        while (scan.hasNextLine()) {
            html.append(scan.nextLine());
        }
        scan.close();
        return html.toString();
    }

    // Sets view for a specific route ex. http://localhost:8080/books
    public static String setView(String route, ConcurrentHashMap<Integer, Book> books, HashMap<String, String> requestBody)
            throws IOException {
        String view = readHTMLFile("src/main/resources/home.html");
        if (route.equals("favicon.ico") || route.equals("post.asp") || route.isEmpty()) {
            return view;
        } else {
            route = "src/main/resources/" + route + ".html";
            try {
                view = readHTMLFile(route);
            } catch (Exception e) {
                e.printStackTrace();
                return view;
            }
            if (route.equals("src/main/resources/books.html")) {
                String booksAsHtml = DatabaseHandler.dictToHTML(books);
                view += booksAsHtml;
            }
            if (route.equals("src/main/resources/clearBooksAction.html")) {
                DatabaseHandler.removeAllBooks();
            }
            if (route.equals("src/main/resources/addBookAction.html")) {
                if (books.isEmpty()) {
                    Book book = new Book(0, requestBody.get("title"), requestBody.get("author"));
                    books.put(0, book);
                } else {
                    int lastId = Collections.max(books.keySet());
                    Book book = new Book(lastId + 1, requestBody.get("title"), requestBody.get("author"));
                    books.put(lastId + 1, book);
                }
            }
            if (route.equals("src/main/resources/updateBookAction.html")) {
                books.put(Integer.parseInt(requestBody.get("id")), new Book(
                        Integer.parseInt(requestBody.get("id")),
                        requestBody.get("title"),
                        requestBody.get("author")));
            }
        }
        return view;
    }
}
