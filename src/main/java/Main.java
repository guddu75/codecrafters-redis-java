import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    private static void handleClient( Socket clientSocket , DB database) throws  IOException{
        PrintWriter out =  new PrintWriter(clientSocket.getOutputStream());
        BufferedReader in =  new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
        String str;
        ArrayList<String> arr = new ArrayList<>();
        int cnt = 0 ;
        while((str = in.readLine()) != null){
            arr.add(str);
            if(arr.size() == 1){
                cnt = 1 + 2*( Integer.parseInt(str.substring(1)));
            }
            System.out.println(cnt);
            if(arr.size() == cnt){
                String cmd = arr.get(2);
                if(cmd.toLowerCase().contentEquals("ping")){
                    out.print("+PONG\r\n");
                    out.flush();
                }else if(cmd.toLowerCase().contentEquals("echo")){
                    String output = arr.get(4);
                    out.printf("$%d\r\n%s\r\n",output.length(),output);
                    out.flush();
                }else if(cmd.toLowerCase().contentEquals("set")){
                    String key = arr.get(4);
                    String value = arr.get(6);
                    if(arr.size() > 6){
                        Long ttl = Long.parseLong(arr.get(10));
                        database.set(key,value,ttl);
                    }else{
                        database.set(key,value,0L);
                    }
                    out.print("+OK\r\n");
                    out.flush();
                }else if(cmd.toLowerCase().contentEquals("get")){
                    String key = arr.get(4);
                    String output = database.get(key);
                    if(output.contentEquals("null")){
                        out.print("$-1\r\n");
                        out.flush();
                    }else{
                        out.printf("$%d\r\n%s\r\n",output.length(),output);
                        out.flush();
                    }

                }
                arr.clear();
                cnt = 0;

            }
        }
        clientSocket.close();
    }
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket = null;
        DB database = new DB();
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
                        handleClient(clientSocket , database);
                    }catch (IOException e){
                        System.out.println("IOException: " + e.getMessage());
                    }

                }).start();
            }
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        }
  }

}
