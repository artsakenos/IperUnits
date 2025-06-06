/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.web;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.java.Log;
import tk.artsakenos.iperunits.string.SuperString;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Un server minimale ristretto ad un context che invia risposte testuali. In
 * grado di leggere il body anche da una get. Utilizzo:
 * <p>
 * SimpleHttpServer simpleHttpServer = new SimpleHttpServer(8089, "/mello/");
 * </p>
 * <a href="http://localhost:8000/mello/denny?comando=qualcosa">...</a>
 * <p>
 * Override the method "handle(...)" to handle the requests. *
 *
 * @author Andrea
 * @version 2017.10.15
 */
@SuppressWarnings("unused")
@Log
public class SimpleHttpServer {

    private HttpServer server;

    /**
     * Starts the server.
     *
     * @param port       The Server Port
     * @param uriContext The Server URI Context, e.g., / o /ultra_server. Deve iniziare con /.
     */
    public final void start(int port, String uriContext) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException ex) {
            log.info("ERROR Starting Started at port " + port + " with endpoint:" + uriContext + ": " + ex.getLocalizedMessage());
            Logger.getLogger(SimpleHttpServer.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        server.createContext(uriContext, this::handleRequest);
        server.setExecutor(null); // creates a default executor
        server.start();
        log.info("Server Started at port " + port + " with endpoint:" + uriContext + ".");
    }

    public final void stop() {
        if (server != null) {
            server.stop(10_000);
        }
    }

    private void handleRequest(HttpExchange request) throws IOException {
        URI requestURI = request.getRequestURI();
        Headers requestHeaders = request.getRequestHeaders();
        String requestBody = SuperString.toStringStream(request.getRequestBody()).trim();
        String response = handle(requestURI.getPath(), requestURI.getQuery(), requestHeaders, requestBody);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        request.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = request.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    /**
     * Override this to hanlde the answer.
     *
     * @param path    lo uri path (context is included);
     * @param query   La query GET (Può essere null!)
     * @param headers gli Headers
     * @param body    Il Body
     * @return restituisce la response in formato testo.
     */
    public String handle(String path, String query, Headers headers, String body) {
        StringBuilder response = new StringBuilder();
        response.append("IperUnits SimpleHttpServer Response\n");
        response.append("------------------------------------\n");
        response.append(String.format("Path: %s\n", path));
        response.append(String.format("Query: %s\n", query != null ? query : "N/A"));
        response.append("Headers:\n");
        headers.forEach((key, values) -> response.append(String.format("  %s: %s\n", key, String.join(", ", values))));
        response.append(String.format("Timestamp: %s\n", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date())));
        response.append("------------------------------------\n");
        response.append("Request Body:\n");
        response.append(body.isEmpty() ? "Empty" : body);
        return response.toString();
    }

    public static void main(String[] args) {
        SimpleHttpServer s = new SimpleHttpServer();
        s.start(3400, "/hey");
    }

}
