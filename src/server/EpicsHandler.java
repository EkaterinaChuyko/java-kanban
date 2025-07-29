package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Epic;
import tasks.TaskManager;
import tasks.NotFoundException;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicsHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();

            String query = uri.getQuery();
            Integer id = null;
            if (query != null && query.startsWith("id=")) {
                try {
                    id = Integer.parseInt(query.substring(3));
                } catch (NumberFormatException ignored) {}
            }

            switch (method) {
                case "GET" -> handleGet(exchange, id);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange, id);
                default -> sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, Integer id) throws IOException {
        if (id == null) {
            List<Epic> epics = manager.getAllEpics();
            sendOk(exchange, gson.toJson(epics));
        } else {
            try {
                Epic epic = manager.getEpic(id);
                sendOk(exchange, gson.toJson(epic));
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic == null) {
            sendResponse(exchange, 400, "{\"error\":\"Bad Request\"}");
            return;
        }

        try {
            if (epic.getId() == 0) {
                manager.createEpic(epic);
            } else {
                manager.updateEpic(epic);
            }
            sendCreated(exchange, gson.toJson(epic));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Integer id) throws IOException {
        try {
            if (id == null) {
                manager.deleteAllEpics();
            } else {
                manager.deleteEpic(id);
            }
            sendOk(exchange, "{\"result\":\"Deleted\"}");
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }
}