import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");

        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait for connection from client
                new Thread(() -> {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String line = bufferedReader.readLine();
                        String userAgent = null;
                        boolean isUserAgentEndpoint = false;

                        while (line != null && !line.isEmpty()) {
                            if (line.startsWith("GET")) {
                                String path = line.split(" ")[1];
                                if (path.equals("/")) {
                                    clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                                } else if (path.startsWith("/echo")) {
                                    String temp = path.substring("/echo/".length());
                                    clientSocket.getOutputStream().write(String.format(
                                            "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                                            temp.length(), temp).getBytes());
                                } else if (path.startsWith("/user-agent")) {
                                    isUserAgentEndpoint = true;
                                } else {
                                    clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                                }
                            } else if (line.startsWith("User-Agent:")) {
                                userAgent = line.substring("User-Agent:".length()).trim();
                            }
                            line = bufferedReader.readLine();
                        }

                        if (isUserAgentEndpoint && userAgent != null) {
                            clientSocket.getOutputStream().write(String.format(
                                    "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                                    userAgent.length(), userAgent).getBytes());
                        }
                    } catch (IOException e) {
                        System.out.println("IOException: " + e.getMessage());
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            System.out.println("IOException: " + e.getMessage());
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}