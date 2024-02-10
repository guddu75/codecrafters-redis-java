import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Main {

    private static String dir = null;
    private static String fileName = null;

    private static String buildResponse(String s){
        String res = "$";
        res += String.valueOf(s.length());
        res += "\r\n";
        res += s;
        res += "\r\n";
        return res;
    }

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
//            System.out.println(cnt);
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
                    if(arr.size() > 7){
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

                }else if(cmd.toLowerCase().contentEquals("config") && arr.get(4).toLowerCase().contentEquals("get")){
                    String ans = "*2\r\n";
                    if(arr.get(6).toLowerCase().contentEquals("dir")){

                        ans += buildResponse("dir");
                        ans += buildResponse(dir);

                    }else if(arr.get(6).toLowerCase().contentEquals("dbfilename")){
                        ans += buildResponse("dbfilename");
                        ans += buildResponse(fileName);

                    }
                    out.print(ans);
                    out.flush();
                }
                arr.clear();
                cnt = 0;

            }
        }
        clientSocket.close();
    }
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
      //extra
    System.out.println("Logs from your program will appear here!");
    if(args.length > 0){
        dir = args[1];
        fileName = args[3];
    }

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
