package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;
import tasks.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (!method.equals("GET")) {
                sendMethodNotAllowed(exchange);
                return;
            }

            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            sendOk(exchange, gson.toJson(prioritizedTasks));
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }
}