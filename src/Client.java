package mySocket.src;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    /**
     * 构造函数 通过 IPaddress:port 构造socket
     * @param address
     * @param port
     */
    private Client(String address, String port) {
        while (true) {
            try {
                this.socket = new Socket(address, Integer.parseInt(port));
                this.in = socket.getInputStream();
                this.out = socket.getOutputStream();
                break;
            } catch (IOException e) {
                System.out.println("该(IP:端口)未在接听或已被占用,请修改");
                Scanner sc = new Scanner(System.in);
                System.out.println("请输入服务器IP：");
                address = sc.next();
                System.out.println("请输入端口号：");
                port = sc.next();
            } catch (IllegalArgumentException e) {
                System.out.println("端口号不合法，请修改");
                Scanner sc = new Scanner(System.in);
                System.out.println("请输入服务器IP：");
                address = sc.next();
                System.out.println("请输入端口号：");
                port = sc.next();
            }
        }
        System.out.println("连接到服务器"+"("+address+","+port+")");
    }
    /**
     * 启动客户端的 Reader线程 和 Writer 线程
     */
    public void start() {
        Reader reader = new Reader();
        Writer writer = new Writer();
        reader.start();
        writer.start();
    }
    /**
     * 关闭连接并退出
     */
    public void close () {
        try{
            if(in != null) this.in.close();
            if(out != null) this.out.close();
            if(socket != null) this.socket.close();
            System.exit(0);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class Reader extends Thread {
        private BufferedReader reader = new BufferedReader(new InputStreamReader (in, StandardCharsets.UTF_8));

        @Override
        public void run() {
            try{
                String line="";
                while (!socket.isClosed() && line!=null) {
                    line = reader.readLine();
                    if(line != null){
                        System.out.println(line);
                    }
                }
                System.out.println("与服务器断开连接");
                close();
            } catch (IOException e) {
                System.out.println("连接已断开");
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class Writer extends Thread {
        private PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        @Override
        public void run() {
            try {
                String line = "";
                String message = "";

                while(!socket.isClosed() && line != null) {
                    boolean isFile = false;
                    line = reader.readLine();

                    for(int i = 0; i + 1 < line.length(); i++)
                        if(line.charAt(i)=='-' && line.charAt(i+1)=='F')
                            isFile = true;

                    if(!isFile){
                        if(!line.equals("")) {
                            message += line + "\n";
                        } else {
                            for (int i = 0; i < message.length(); i++)
                                writer.print(message.charAt(i));
                            message = "";
                            writer.flush();
                        }
                    }else {
                        String[] path = line.split(" ");
                        File file = new File(path[1]);
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        while ((line = br.readLine()) != null) {
                            System.out.println(line);
                            writer.println(line);
                            writer.flush();
                        }
                        br.close();
                        line = "";
                        System.out.println("文件信息发送完成.");
                    }
                }
                System.out.println("客户端退出");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error:连接已关闭");
            } finally {
                try{
                    if(writer!=null){
                        writer.close();
                    }
                    if(reader!=null){
                        reader.close();
                    }
                    close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String address, port;
        if(args.length == 2) {
            address = args[0];
            port = args[1];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入服务器IP：");
            address = sc.next();
            System.out.println("请输入端口号：");
            port = sc.next();
        }
        new Client(address, port).start();
    }
}
