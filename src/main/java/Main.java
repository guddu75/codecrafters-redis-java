import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static int getNum(String str , int idx){
        int res = 0;
        for(int i = idx ; i < str.length() ; i++){
            if(str.charAt(i) >= '0' && str.charAt(i)<='9'){
                res = (res*10) + (str.charAt(i)-'0');
            }else{
                break;
            }
        }
        return  res;
    }

    private static String parseCommand(String str){
        // *2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n
        String response = null;
        int numCommands = getNum(str,1);
        if(numCommands==1){
            response = "+PONG\r\n";
        }else if(numCommands == 2){
            int idx = str.lastIndexOf("$");
            response = str.substring(idx);
        }
        return response;

    }


    private static void handleClient( Socket clientSocket) throws  IOException{
        PrintWriter out =  new PrintWriter(clientSocket.getOutputStream());
        BufferedReader in =  new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
        String str;
        while((str = in.readLine()) != null){
            String response = parseCommand(str);
            out.print(response);
            out.flush();
        }
        clientSocket.close();
    }
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket = null;
//        Socket clientSocket = null;
        int port = 6379;
        try {
//            System.out.println("Hello world!");
          serverSocket = new ServerSocket(port);
          serverSocket.setReuseAddress(true);
          // Wait for connection from client.
            while(true){
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try{
                        handleClient(clientSocket);
                    }catch (IOException e){
                        System.out.println("IOException: " + e.getMessage());
                    }

                }).start();
            }
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        } finally {
//          try {
//            if (clientSocket != null) {
//              clientSocket.close();
//            }
//          } catch (IOException e) {
//            System.out.println("IOException: " + e.getMessage());
//          }
        }
//      System.out.println("Here i am");
  }


}
