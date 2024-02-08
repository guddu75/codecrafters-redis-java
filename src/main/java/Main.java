import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {


//    private static int numCommands(String str){
//
//    }
//
//    private static int getNum(String str , int idx){
//        int res = 0;
//        for(int i = idx ; i < str.length() ; i++){
//            if(str.charAt(i) >= '0' && str.charAt(i)<='9'){
//                res = (res*10) + (str.charAt(i)-'0');
//            }else{
//                break;
//            }
//        }
//        return  res;
//    }

    private static String parseCommand(String str){
        // *2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n
        int i = 0;
        int lenCommand = 0;
        boolean flag = false;
        for( ; i< str.length() ; i++){
            if(!flag){
                if(str.charAt(i)=='*'){
                    flag = true;
                }
            }else{
                if(str.charAt(i)>='0' && str.charAt(i)<= '9'){
                    lenCommand = (lenCommand*10) + (str.charAt(i)-'0');
                }else{
                    break;
                }
            }

        }
        ArrayList<String> commands = new ArrayList<String>();
//        System.out.println(lenCommand);
        for(int j = 0 ; j < lenCommand ; j++) {
            flag = false;
            int len = 0;
            for (; i < str.length(); i++) {
                if (!flag) {
                    if (str.charAt(i) == '$') {
                        flag = true;
                    }
                } else {
                    if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                        len = (len * 10) + (str.charAt(i) - '0');
                    } else {
                        break;
                    }
                }
            }
//            System.out.println(len);
            i += 4;
            String c = "";
            for(int k = 0 ; k < len ; k++){
                c += str.charAt(i);
                i++;
            }
//            System.out.println(c);
            commands.add(c);
        }
        String response = null;
        for(String s : commands){
            System.out.println(s);
        }
        if(commands.get(0).contentEquals("PING")){
            response = "+PONG";
        }else if(commands.get(0).contentEquals("ECHO") ){
            response = commands.get(1);
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
