package main.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public static void main(String[] args) throws IOException {
        FileBackedTaskManager inMemoryTaskManager = (FileBackedTaskManager) Managers.getDefault();
        File file = File.createTempFile("test", "txt");
        ((FileBackedTaskManager) inMemoryTaskManager).loadFromFile(file);

        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler(inMemoryTaskManager));
            httpServer.start();
            System.out.println("Starting server at port: " + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    static class TaskHandler implements HttpHandler {

        TaskManager fileBackedTasksManager;

        public TaskHandler(TaskManager taskManager) {
            fileBackedTasksManager = taskManager;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Choices choice = endpoint(path, method, body, query);


            switcher(choice, exchange, body, fileBackedTasksManager);
        }


        private void switcher(Choices choice, HttpExchange exchange, String body, TaskManager fileBackedTasksManager) {
            switch (choice) {
                case GET_ALL_TASKS:
                    outputAllTasks(exchange, fileBackedTasksManager);
                    break;
                case GET_ALL_EPICS:
                    outputAllEpics(exchange, fileBackedTasksManager);
                    break;
                case GET_ALL_SUBTASKS:
                    outputAllSubTasks(exchange, fileBackedTasksManager);
                    break;
                case ERROR:
                    writeResponse(exchange, "Неверно составлен запрос", 400);
                case GET_BY_ID_TASK:
                    receiveTaskById(exchange, fileBackedTasksManager);
                    break;
                case GET_BY_ID_EPIC:
                    receiveEpicById(exchange, fileBackedTasksManager);
                    break;
                case GET_BY_ID_SUBTASK:
                    receiveSubTaskById(exchange, fileBackedTasksManager);
                    break;
                case CREATE_TASK:
                    createNewTask(body, exchange, fileBackedTasksManager);
                    break;
                case CREATE_EPIC:
                    createNewEpic(body, exchange, fileBackedTasksManager);
                    break;
                case CREATE_SUBTASK:
                    createNewSubTask(body, exchange, fileBackedTasksManager);
                    break;
                case UPDATE_TASK:
                    changeTask(body, exchange, fileBackedTasksManager);
                    break;
                case UPDATE_EPIC:
                    changeEpic(body, exchange, fileBackedTasksManager);
                    break;
                case UPDATE_SUBTASK:
                    changeSubTask(body, exchange, fileBackedTasksManager);
                    break;
                case DELETE_TASKS:
                    removeAllTasks(exchange, fileBackedTasksManager);
                    break;
                case DELETE_EPICS:
                    removeAllEpics(exchange, fileBackedTasksManager);
                    break;
                case DELETE_SUBTASKS:
                    removeAllSubTasks(exchange, fileBackedTasksManager);
                    break;
                case DELETE_BY_ID_TASK:
                    removeTaskById(exchange, fileBackedTasksManager);
                    break;
                case DELETE_BY_ID_EPIC:
                    removeEpicById(exchange, fileBackedTasksManager);
                    break;
                case DELETE_BY_ID_SUBTASK:
                    removeSubTaskById(exchange, fileBackedTasksManager);
                    break;
                case GET_HISTORY:
                    receiveHistory(exchange, fileBackedTasksManager);
                    break;
                case GET_EPICS_SUBTASKS:
                    receiveAllEpicSubtasks(exchange, fileBackedTasksManager);
                    break;
                case GET_PRIORITIZED:
                    receivePrioritize(exchange, fileBackedTasksManager);
                    break;

            }

        }


        private void receiveAllEpicSubtasks(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String[] path = exchange.getRequestURI().getPath().split("/");
            Integer.parseInt(path[3]);
            String stringJson = gson.toJson(fileBackedTasksManager.getAllEpicSubtask(Integer.parseInt(path[3])));
            writeResponse(exchange, stringJson, 200);
        }

        private void receivePrioritize(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String stringJson = gson.toJson(fileBackedTasksManager.getPrioritizedTasks());
            writeResponse(exchange, stringJson, 200);

        }

        private void receiveHistory(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String stringJson = gson.toJson(fileBackedTasksManager.getHistory());
            writeResponse(exchange, stringJson, 200);
        }

        private void removeSubTaskById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            int id = Integer.parseInt(queryParts[1]);
            fileBackedTasksManager.delSubtaskById(id);
            writeResponse(exchange, "Сабтаск удален", 200);
        }

        private void removeEpicById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            int id = Integer.parseInt(queryParts[1]);
            fileBackedTasksManager.delEpicById(id);
            writeResponse(exchange, "Эпик удален", 200);
        }

        private void removeTaskById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            int id = Integer.parseInt(queryParts[1]);
            fileBackedTasksManager.delTaskById(id);
            writeResponse(exchange, "Задача удалена", 200);
        }

        private void removeAllSubTasks(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            fileBackedTasksManager.delAllSubtasks();
            writeResponse(exchange, "Все подзадачи удалены", 200);
        }

        private void removeAllEpics(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            fileBackedTasksManager.delAllEpics();
            writeResponse(exchange, "Все эпики удалены", 200);
        }

        private void removeAllTasks(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            fileBackedTasksManager.delAllTask();
            writeResponse(exchange, "Все задачи удалены", 200);
        }

        private void changeSubTask(String body, HttpExchange exchange, TaskManager fileBackedTasksManager) {
            try {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                fileBackedTasksManager.updateSubtask(subtask);
                writeResponse(exchange, "Подзадача изменена", 200);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            } catch (ValidException e) {
                writeResponse(exchange, "Задачи пересекаются", 400);
            }
        }

        private void changeEpic(String body, HttpExchange exchange, TaskManager fileBackedTasksManager) {
            try {

                Epic epic = gson.fromJson(body, Epic.class);
                fileBackedTasksManager.updateEpic(epic);
                writeResponse(exchange, "Эпик изменен", 200);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            } catch (ValidException e) {
                writeResponse(exchange, "Задачи пересекаются", 400);
            }
        }


        private void changeTask(String body, HttpExchange exchange, TaskManager fileBackedTasksManager) {
            try {
                System.out.println("ee");
                System.out.println(body);
                System.out.println(gson.fromJson(body, Task.class));
                Task task = gson.fromJson(body, Task.class);
                System.out.println(task);
                fileBackedTasksManager.updateTask(task);
                writeResponse(exchange, "Задача изменена", 200);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            } catch (ValidException e) {
                writeResponse(exchange, "Задачи пересекаются", 400);
            }
        }


        private void createNewSubTask(String body, HttpExchange exchange, TaskManager fileBackedTasksManager) {
            try {

                Subtask subtask = gson.fromJson(body, Subtask.class);
                fileBackedTasksManager.putSubtask(subtask);
                writeResponse(exchange, "Подзадача добалена", 201); // Что возвращать? Отклик или задачу из мапы?
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            } catch (ValidException e) {
                writeResponse(exchange, "Задачи пересекаются", 400);
            }
        }


        private void createNewEpic(String body, HttpExchange exchange, TaskManager fileBackedTasksManager) {
            try {

                Epic epic = gson.fromJson(body, Epic.class);
                fileBackedTasksManager.putEpic(epic);
                writeResponse(exchange, "Эпик добален", 201);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            } catch (ValidException e) {
                writeResponse(exchange, "Задачи пересекаются", 400);
            }
        }


        private void createNewTask(String body, HttpExchange exchange, TaskManager fileBackedTasksManager) {
            try {

                System.out.println("Тело запроса: " + body);
                Task task = gson.fromJson(body, Task.class);
                fileBackedTasksManager.putTask(task);
                writeResponse(exchange, "Задача добалена", 201);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            } catch (ValidException e) {
                writeResponse(exchange, "Задачи пересекаются", 400);
            }
        }

        private void receiveSubTaskById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            String stringJson = gson.toJson(fileBackedTasksManager.getSubtaskById(Integer.parseInt(queryParts[1])));
            writeResponse(exchange, stringJson, 200);
        }

        private void receiveEpicById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            String stringJson = gson.toJson(fileBackedTasksManager.getEpicById(Integer.parseInt(queryParts[1])));
            writeResponse(exchange, stringJson, 200);
        }

        private void receiveTaskById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            String stringJson = gson.toJson(fileBackedTasksManager.getTaskById(Integer.parseInt(queryParts[1])));
            writeResponse(exchange, stringJson, 200);
        }

        private void outputAllSubTasks(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String stringJson = gson.toJson(fileBackedTasksManager.getSubtask());
            writeResponse(exchange, stringJson, 200);
        }

        private void outputAllEpics(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String stringJson = gson.toJson(fileBackedTasksManager.getEpic());
            writeResponse(exchange, stringJson, 200);
        }

        private void outputAllTasks(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String stringJson = gson.toJson(fileBackedTasksManager.getTasks());
            writeResponse(exchange, stringJson, 200);
        }


        private Choices endpoint(String path, String method, String body, String query) {
            String[] pathParts = path.split("/");
            if (method.equals("GET")) {
                if (pathParts.length == 3 && query == null) {
                    if (pathParts[2].equals("task")) return Choices.GET_ALL_TASKS;
                    else if (pathParts[2].equals("epic")) return Choices.GET_ALL_EPICS;

                    else if (pathParts[2].equals("subtask")) return Choices.GET_ALL_SUBTASKS;
                    else if (pathParts[2].equals("history")) return Choices.GET_HISTORY;
                    else if (pathParts[2].equals("prioritized")) return Choices.GET_PRIORITIZED;
                    else return Choices.ERROR;
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return Choices.ERROR;
                    try {
                        Integer.parseInt(queryParts[1]);
                        if (pathParts[2].equals("task")) return Choices.GET_BY_ID_TASK;
                        else if (pathParts[2].equals("epic")) return Choices.GET_BY_ID_EPIC;

                        else if (pathParts[2].equals("subtask")) return Choices.GET_BY_ID_SUBTASK;
                        else return Choices.ERROR;
                    } catch (NumberFormatException e) {
                        return Choices.ERROR;
                    }
                } else if (pathParts.length > 3) {
                    if (pathParts[2].equals("epics") && pathParts[4].equals("subtasks")) {
                        return Choices.GET_EPICS_SUBTASKS;
                    }
                } else return Choices.ERROR;
            } else if (method.equals("POST")) {
                if (body.isBlank()) return Choices.ERROR;

                if (pathParts.length == 3 && query == null) {
                    if (pathParts[2].equals("task")) return Choices.CREATE_TASK;
                    else if (pathParts[2].equals("epic")) return Choices.CREATE_EPIC;

                    else if (pathParts[2].equals("subtask")) return Choices.CREATE_SUBTASK;
                    else return Choices.ERROR;
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return Choices.ERROR;
                    try {
                        Integer.parseInt(queryParts[1]);
                        if (pathParts[2].equals("task")) {
                            return Choices.UPDATE_TASK;
                        } else if (pathParts[2].equals("epic")) return Choices.UPDATE_EPIC;

                        else if (pathParts[2].equals("subtask")) return Choices.UPDATE_SUBTASK;
                        else return Choices.ERROR;
                    } catch (NumberFormatException e) {
                        return Choices.ERROR;
                    }
                } else return Choices.ERROR;
            } else if (method.equals("DELETE")) {

                if (pathParts.length == 3 && query == null) {
                    if (pathParts[2].equals("task")) return Choices.DELETE_TASKS;
                    else if (pathParts[2].equals("epic")) return Choices.DELETE_EPICS;
                    else if (pathParts[2].equals("subtask")) return Choices.DELETE_SUBTASKS;
                    else return Choices.ERROR;
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return Choices.ERROR;
                    try {
                        Integer.parseInt(queryParts[1]);
                        if (pathParts[2].equals("task")) return Choices.DELETE_BY_ID_TASK;
                        else if (pathParts[2].equals("epic")) return Choices.DELETE_BY_ID_EPIC;

                        else if (pathParts[2].equals("subtask")) return Choices.DELETE_BY_ID_SUBTASK;
                        else return Choices.ERROR;
                    } catch (NumberFormatException e) {
                        return Choices.ERROR;
                    }
                } else return Choices.ERROR;
            }
            return Choices.ERROR;
        }

        private void writeResponse(HttpExchange exchange, String responseString, int responseCode) {
            if (responseString.isBlank()) {
                try {
                    exchange.sendResponseHeaders(responseCode, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                try {
                    exchange.sendResponseHeaders(responseCode, bytes.length);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            exchange.close();
        }
    }
}
