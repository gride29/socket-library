package pl.sggw.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    static ConcurrentHashMap<Integer, Book> books = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        int PORT = 8080;
        ServerSocket serverSocket = new ServerSocket(PORT);
        books = DatabaseHandler.readJsonFile();

        for (Map.Entry<Integer, Book> book : books.entrySet()) {
            System.out.println(book.getKey() + " " + book.getValue().toString());
        }

        System.out.println("Server running on http://localhost:" + PORT);

        while (true) {
            final Socket clientSocket = serverSocket.accept();
            Thread thread = new Thread(
                    () -> {
                        try {
                            newConnection(clientSocket);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
            thread.start();
        }
    }

    private static void newConnection(Socket client) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream());
        HashMap<String, String> requestHeaders = getRequestHeaders(client.getInputStream());
        HashMap<String, String> requestBody = getRequestBody(requestHeaders.get("Body"));

        String route = requestHeaders.get("Url");
        String view = RouteHandler.setView(route, books, requestBody);

        printResponseHeadersAndView(out, view);
    }

    private static HashMap<String, String> getRequestHeaders(InputStream inputStream) throws IOException {
        HashMap<String, String> requestHeaders = new HashMap<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line = in.readLine();
        String[] methodAndUrl = line.split(" ");

        requestHeaders.put("Method", methodAndUrl[0]);
        requestHeaders.put("Url", methodAndUrl[1].substring(1));

        while (!(line = in.readLine()).equals("")) {
            String[] keyValuePair = line.split(":", 2);
            requestHeaders.put(keyValuePair[0].trim(), keyValuePair[1].trim());
        }

        if (requestHeaders.get("Method").equals("POST")) {
            StringBuilder body = new StringBuilder();
            int c;
            for (int i = 0; i < Integer.parseInt(requestHeaders.get("Content-Length")); i++) {
                c = in.read();
                body.append((char) c);
            }
            requestHeaders.put("Body", body.toString());
        }

        return requestHeaders;
    }

    public static HashMap<String, String> getRequestBody(String body) throws IOException {
        HashMap<String, String> requestBody = new HashMap<>();
        if (body != null) {
            String[] parameters = body.split("[&]");
            for (String parameter : parameters) {
                String[] nameAndValue = parameter.split("=", 2);
                String name = URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8.name());
                String value = null;
                if (nameAndValue.length > 1) {
                    value = URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8.name());
                }
                requestBody.put(name, value);
            }
        }
        return requestBody;
    }

    private static void printResponseHeadersAndView(PrintWriter out, String view) {
        out.println("HTTP/1.1 200 OK");
        out.println("Connection: close");
        out.println("Content-Type: text/html\n");
        out.println(view);
        out.flush();
        out.close();
    }
}
