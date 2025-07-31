package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final Gson gson;

    public BaseHttpHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected void sendOk(HttpExchange exchange, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendCreated(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, 201, response);
    }

    protected void sendNoContent(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "{\"error\":\"Not Found\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        String response = "{\"error\":\"Task overlaps with existing tasks\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = "{\"error\":\"Internal Server Error\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(500, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String response = "{\"error\":\"Method Not Allowed\"}";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(405, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}