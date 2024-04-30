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
            int choice = endpoint(path, method, body, query);

            String response = "Обрабатываем запрос от клиента";

            switcher(choice, exchange, body, fileBackedTasksManager);

            exchange.sendResponseHeaders(200, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }


        private void switcher(int choice, HttpExchange exchange, String body, TaskManager fileBackedTasksManager) {
            switch (choice) {
                case 2:
                    outputAllTasks(exchange, fileBackedTasksManager);
                    break;
                case 3:
                    outputAllEpics(exchange, fileBackedTasksManager);
                    break;
                case 4:
                    outputAllSubTasks(exchange, fileBackedTasksManager);
                    break;
                case 5:
                    writeResponse(exchange, "Неверно составлен запрос", 400);
                case 6:
                    receiveTaskById(exchange, fileBackedTasksManager);
                    break;
                case 7:
                    receiveEpicById(exchange, fileBackedTasksManager);
                    break;
                case 8:
                    receiveSubTaskById(exchange, fileBackedTasksManager);
                    break;
                case 9:
                    createNewTask(body, exchange, fileBackedTasksManager);
                    break;
                case 10:
                    createNewEpic(body, exchange, fileBackedTasksManager);
                    break;
                case 11:
                    createNewSubTask(body, exchange, fileBackedTasksManager);
                    break;
                case 12:
                    changeTask(body, exchange, fileBackedTasksManager);
                    break;
                case 13:
                    changeEpic(body, exchange, fileBackedTasksManager);
                    break;
                case 14:
                    changeSubTask(body, exchange, fileBackedTasksManager);
                    break;
                case 16:
                    removeAllTasks(exchange, fileBackedTasksManager);
                    break;
                case 17:
                    removeAllEpics(exchange, fileBackedTasksManager);
                    break;
                case 18:
                    removeAllSubTasks(exchange, fileBackedTasksManager);
                    break;
                case 19:
                    removeTaskById(exchange, fileBackedTasksManager);
                    break;
                case 20:
                    removeEpicById(exchange, fileBackedTasksManager);
                    break;
                case 21:
                    removeSubTaskById(exchange, fileBackedTasksManager);
                    break;
                case 22:
                    receiveHistory(exchange, fileBackedTasksManager);
                    break;
                case 23:
                    receiveAllEpicSubtasks(exchange, fileBackedTasksManager);
                    break;
                case 24:
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


        private int endpoint(String path, String method, String body, String query) {
            String[] pathParts = path.split("/");
            if (method.equals("GET")) {
                if (pathParts.length == 2) return 1;

                else if (pathParts.length == 3 && query == null) {
                    if (pathParts[2].equals("task")) return 2;
                    else if (pathParts[2].equals("epic")) return 3;

                    else if (pathParts[2].equals("subtask")) return 4;
                    else if (pathParts[2].equals("history")) return 22;
                    else if (pathParts[2].equals("prioritized")) return 24;
                    else return 5;
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return 5;
                    try {
                        Integer.parseInt(queryParts[1]);
                        if (pathParts[2].equals("task")) return 6;
                        else if (pathParts[2].equals("epic")) return 7;

                        else if (pathParts[2].equals("subtask")) return 8;
                        else return 5;
                    } catch (NumberFormatException e) {
                        return 5;
                    }
                } else if (pathParts.length > 3) {
                    if (pathParts[2].equals("epics") && pathParts[4].equals("subtasks")) {
                        return 23;
                    }
                } else return 5;
            } else if (method.equals("POST")) {
                if (body.isBlank()) return 5;

                if (pathParts.length == 3 && query == null) {
                    if (pathParts[2].equals("task")) return 9;
                    else if (pathParts[2].equals("epic")) return 10;

                    else if (pathParts[2].equals("subtask")) return 11;
                    else return 5;
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return 5;
                    try {
                        Integer.parseInt(queryParts[1]);
                        if (pathParts[2].equals("task")) {
                            System.out.println("dddd");
                            return 12;
                        } else if (pathParts[2].equals("epic")) return 13;

                        else if (pathParts[2].equals("subtask")) return 14;
                        else return 5;
                    } catch (NumberFormatException e) {
                        return 5;
                    }
                } else return 5;
            } else if (method.equals("DELETE")) {
                if (pathParts.length == 2) return 15;
                else if (pathParts.length == 3 && query == null) {
                    if (pathParts[2].equals("task")) return 16;
                    else if (pathParts[2].equals("epic")) return 17;
                    else if (pathParts[2].equals("subtask")) return 18;
                    else return 5;
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return 5;
                    try {
                        Integer.parseInt(queryParts[1]);
                        if (pathParts[2].equals("task")) return 19;
                        else if (pathParts[2].equals("epic")) return 20;

                        else if (pathParts[2].equals("subtask")) return 21;
                        else return 5;
                    } catch (NumberFormatException e) {
                        return 5;
                    }
                } else return 5;
            }
            return 5;
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
