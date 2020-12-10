package gairrymander;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import com.sun.net.httpserver.*;

public class Server {
    // Port number used to connect to this server
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println(PORT);

        // Code below is for testing purposes. Comment out everything else in main when testing.
        /*boolean isD = true;
        Random rand = new Random();
        Precinct[] precincts = new Precinct[8];
        int population = 0;
        int numDistricts = 2;
        for (int i = 0; i < precincts.length; i++) {
            int precinctPop = 100;
            precincts[i] = new Precinct(i, precinctPop, rand.nextDouble());
            population += precinctPop;
        }
        Gerrymanderer gerry = new Gerrymanderer(population, precincts.length, numDistricts, precincts);
        gerry.rect();
        System.out.println("average district population " + gerry.population / gerry.numDistricts);
        Set<District> districts = gerry.gerrymander(gerry.population / gerry.numDistricts, isD);
        // System.out.println(districts);
        gerry.labelPrecincts(districts);
        System.out.println(gerry.toJSON());*/

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("C:\\Users\\zhouc\\vscode-workspace\\gairrymander\\Map.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        server.createContext("/query", (HttpExchange t) -> {
            System.out.println("query");
            String party = parse("party", t.getRequestURI().getQuery().split("&"));
            boolean isD = party.equals("D");
            // send(t, "application/json", String.format(QUERY_TEMPLATE, "", ""));
            // return;
            // Precinct[] precincts = Gerrymanderer.fromFile(new File("gairrymander\\oregon_data.csv"));
            // int population = Gerrymanderer.population(precincts);
            // int numDistricts = 5;
            // Test begin
            Random rand = new Random();
            Precinct[] precincts = new Precinct[8];
            int population = 0;
            int numDistricts = 2;
            for (int i = 0; i < precincts.length; i++) {
                int precinctPop = 100;
                precincts[i] = new Precinct(i, precinctPop, rand.nextDouble());
                population += precinctPop;
            }
            // Test end
            Gerrymanderer gerry = new Gerrymanderer(population, precincts.length, numDistricts, precincts);
            gerry.rect();
            System.out.println("average district population " + gerry.population / gerry.numDistricts);
            Set<District> districts = gerry.gerrymander(gerry.population / gerry.numDistricts, isD);
            // System.out.println(districts);
            gerry.labelPrecincts(districts);
            System.out.println(gerry.toJSON());
            send(t, "application/json", gerry.toJSON());
        });
        server.setExecutor(null);
        server.start();
    }

    private static String parse(String key, String... params) {
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return "";
    }

    private static void send(HttpExchange t, String contentType, String data)
            throws IOException, UnsupportedEncodingException {
        t.getResponseHeaders().set("Content-Type", contentType);
        byte[] response = data.getBytes("UTF-8");
        t.sendResponseHeaders(200, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }
}
