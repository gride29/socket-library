package pl.sggw.restserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class RouteHandler {

    // Sets view for a specific route ex. http://localhost:8080/books
    public static String setView(String route, ConcurrentHashMap<Integer, Book> books, HashMap<String, String> requestBody, String method) {
        String view = "";
        if (route == null) {
            return view;
        }
        if (route.equals("favicon.ico") || route.equals("post.asp") || route.isEmpty()) {
            return view;
        } else {
            if (route.equals("books") && method.equals("GET")) {
                String booksAsHtml = DatabaseHandler.dictToJSON(books);
                view += booksAsHtml;
            }
            if (route.equals("books") && method.equals("POST")) {
                if (books.isEmpty()) {
                    Book book = new Book(0, requestBody.get("title"), requestBody.get("authorName"), requestBody.get("authorSurname"));
                    books.put(0, book);
                    view += "{\r\n\"id\": " + "0" + "\r\n    }";
                } else {
                    int lastId = Collections.max(books.keySet());
                    lastId += 1;
                    Book book = new Book(lastId, requestBody.get("title"), requestBody.get("authorName"), requestBody.get("authorSurname"));
                    books.put(lastId, book);
                    view += "{\r\n\"id\": " + lastId + "\r\n    }";
                }
            }
            if (route.matches("books/\\d+") && method.equals("PUT")) {
                String[] routeArray = route.split("/");
                int id = Integer.parseInt(routeArray[1]);
                books.put(id, new Book(
                        id,
                        requestBody.get("title"),
                        requestBody.get("authorName"),
                        requestBody.get("authorSurname")));
                view += "{\r\n\"id\": " + id + "\r\n    }";
            }
            if (route.matches("books/\\d+") && method.equals("DELETE")) {
                String[] routeArray = route.split("/");
                int id = Integer.parseInt(routeArray[1]);
                books.remove(id);
                view += "{\r\n\"id\": " + id + "\r\n    }";
            }
        }
        return view;
    }
}
