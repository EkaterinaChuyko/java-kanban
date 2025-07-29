package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Subtask;
import tasks.TaskManager;
import tasks.NotFoundException;
import tasks.OverlappingTasksException;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtasksHandler(TaskManager manager, Gson gson) {
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
                default -> sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    protected void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    private void handleGet(HttpExchange exchange, Integer id) throws IOException {
        if (id == null) {
            List<Subtask> subtasks = manager.getAllSubtasks();
            sendOk(exchange, gson.toJson(subtasks));
        } else {
            try {
                Subtask subtask = manager.getSubtask(id);
                sendOk(exchange, gson.toJson(subtask));
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        if (subtask == null) {
            sendResponse(exchange, 400, "{\"error\":\"Bad Request\"}");
            return;
        }

        try {
            manager.createSubtask(subtask);
            sendCreated(exchange, gson.toJson(subtask));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (OverlappingTasksException e) {
            sendHasOverlaps(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Integer id) throws IOException {
        try {
            if (id == null) {
                manager.deleteAllSubtasks();
            } else {
                manager.deleteSubtask(id);
            }
            sendNoContent(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}