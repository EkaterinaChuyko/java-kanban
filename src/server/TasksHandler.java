package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;
import tasks.TaskManager;
import tasks.NotFoundException;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager, Gson gson) {
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
                } catch (NumberFormatException ignored) {
                }
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
            List<Task> tasks = manager.getAllTasks();
            sendOk(exchange, gson.toJson(tasks));
        } else {
            try {
                Task task = manager.getTask(id);
                sendOk(exchange, gson.toJson(task));
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Task task = gson.fromJson(body, Task.class);
        if (task == null) {
            sendResponse(exchange, 400, "{\"error\":\"Bad Request\"}");
            return;
        }

        try {
            if (task.getId() == 0) { // create
                if (manager.isTaskTimeIntersect(task)) {
                    sendHasOverlaps(exchange);
                    return;
                }
                manager.createTask(task);
            } else { // update
                if (manager.isTaskTimeIntersect(task)) {
                    sendHasOverlaps(exchange);
                    return;
                }
                manager.updateTask(task);
            }
            sendCreated(exchange, gson.toJson(task));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Integer id) throws IOException {
        try {
            if (id == null) {
                manager.deleteAllTasks();
            } else {
                manager.deleteTask(id);
            }
            sendOk(exchange, "{\"result\":\"Deleted\"}");
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }
}
