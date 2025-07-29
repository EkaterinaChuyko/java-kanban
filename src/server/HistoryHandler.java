package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;
import tasks.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager.getGson());
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

            List<Task> history = manager.getHistory();
            sendOk(exchange, gson.toJson(history));
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }
}
