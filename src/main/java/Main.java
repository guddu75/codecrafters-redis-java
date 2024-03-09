import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Main {

    private static String dir = null;
    private static String fileName = null;

    private static String buildResponse(String s) {
        String res = "$";
        res += String.valueOf(s.length());
        res += "\r\n";
        res += s;
        res += "\r\n";
        return res;
    }

    private static void response(PrintWriter out, String s) {
        out.print("$" + s.length() + "\r\n");
        out.print(s + "\r\n");
    }

    private static void responseList(PrintWriter out, String... values) {
        out.print("*" + values.length + "\r\n");
        for (String value : values) {
            response(out, value);
        }
    }

    private static void handleClient(Socket clientSocket, DB database) throws IOException {

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String str;
        ArrayList<String> arr = new ArrayList<>();
        int cnt = 0;
        while ((str = in.readLine()) != null) {
            arr.add(str);
            if (arr.size() == 1) {
                cnt = 1 + 2 * (Integer.parseInt(str.substring(1)));
            }
            // System.out.println(cnt);
            if (arr.size() == cnt) {
                String cmd = arr.get(2);
                if (cmd.toLowerCase().contentEquals("ping")) {
                    out.print("+PONG\r\n");
                    out.flush();
                } else if (cmd.toLowerCase().contentEquals("echo")) {
                    String output = arr.get(4);
                    response(out, output);
                    out.flush();
                } else if (cmd.toLowerCase().contentEquals("set")) {
                    String key = arr.get(4);
                    String value = arr.get(6);
                    if (arr.size() > 7) {
                        Long ttl = Long.parseLong(arr.get(10));
                        database.set(key, value, ttl);
                    } else {
                        database.set(key, value, 0L);
                    }
                    System.out.println(database.get(key));
                    out.print("+OK\r\n");
                    out.flush();
                } else if (cmd.toLowerCase().contentEquals("get")) {
                    String key = arr.get(4);
                    String output = database.get(key);
                    System.out.println(output);
                    if (output.contentEquals("null")) {
                        out.print("$-1\r\n");
                        out.flush();
                    } else {
                        response(out, output);
                        out.flush();
                    }

                } else if (cmd.toLowerCase().contentEquals("config") && arr.get(4).toLowerCase().contentEquals("get")) {
                    if (arr.get(6).toLowerCase().contentEquals("dir")) {

                        responseList(out, "dir", dir);

                    } else if (arr.get(6).toLowerCase().contentEquals("dbfilename")) {
                        responseList(out, "dbfilename", fileName);

                    }
                    out.flush();
                } else if (cmd.toLowerCase().contentEquals("keys")) {
                    String[] keys = database.getKeys();
                    responseList(out, keys);
                    out.flush();
                }
                arr.clear();
                cnt = 0;

            }
        }
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        // You can use print statements as follows for debugging, they'll be visible
        // when running tests.
        // extra
        System.out.println("Logs from your program will appear here!");
        System.out.println("continue");
        ServerSocket serverSocket = null;
        DB database = new DB();
        // Socket clientSocket = null;
        int port = 6379;
        if (args.length == 4) {
            dir = args[1];
            fileName = args[3];

            File file = new File(dir, fileName);

            if (file.exists()) {
                InputStream in = new FileInputStream(file);
                int b;
                int lengthEncoding;
                int valueType;

                while ((b = in.read()) != -1) {

                    if (b == 0xFB) {
                        getLength(in);
                        getLength(in);
                        break;
                    }
                    // System.out.println("skipped");
                    valueType = in.read(); // value-type
                    // System.out.println("valueType=" + valueType);
                    lengthEncoding = getLength(in);
                    // System.out.println("Length=" + lengthEncoding);
                    byte[] bytes = in.readNBytes(lengthEncoding);
                    String key = new String(bytes);
                    System.out.println(key);
                    database.set(key, "star");
                }

            }

        }

        try {
            // System.out.println("Hello world!");
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            // Wait for connection from client.
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        handleClient(clientSocket, database);
                    } catch (IOException e) {
                        System.out.println("IOException: " + e.getMessage());
                    }

                }).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static int getLength(InputStream in) throws IOException {
        int length = 0;
        byte b = (byte) in.read();

        switch (b & 0b11000000) {
            case 0 -> {
                length = b & 0b00111111;
            }
            case 128 -> {
                ByteBuffer buffer = ByteBuffer.allocate(2);
                buffer.put((byte) (b & 00111111));
                buffer.put((byte) in.read());
                buffer.rewind();
                length = buffer.getShort();
            }
            case 256 -> {
                ByteBuffer buffer = ByteBuffer.allocate(4);
                buffer.put(b);
                buffer.put(in.readNBytes(3));
                buffer.rewind();
                length = buffer.getInt();
            }
            case 384 -> {
                System.out.println("Special Type");
            }
        }
        return length;
    }

}
