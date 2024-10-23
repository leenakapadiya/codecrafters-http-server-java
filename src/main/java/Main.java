import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
     Socket clientSocket = null;
     try {
       ServerSocket serverSocket = new ServerSocket(4221);
       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);

       clientSocket = serverSocket.accept(); // Wait for connection from client
       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
       String line = bufferedReader.readLine();
       while(line != null && !line.isEmpty()) {
           if(line.startsWith("GET")) {
               String path = line.split(" ")[1];
               if (path.equals(("/"))) {
                   clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
               } else if(path.startsWith(("/echo"))) {
                   String temp = path.substring("/echo/".length());
                   clientSocket.getOutputStream().write(String.format(
                           "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s",
                           temp.length(), temp).getBytes());
               } else {
                     clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
               }
               break;
           }

       }
//       System.out.println("accepted new connection");
//       clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
