package mySocket.src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static long id = 0;
    private HashMap<Long,ServerThread> socketList = new HashMap<>();
    private ServerSocket serverSocket;
    public Server(int port) {
        try{
            this.serverSocket = new ServerSocket(port);
            System.out.println("服务器启动完成 使用端口："+port);
            System.out.println("正在等待客户端的连接...");
        } catch (IOException e) {
            System.out.println("该段端口未处于listening或端口已被占用，请更换服务器端口!");
            main(new String[0]);
        }
    }
    /**
     * 启动服务器，先让Writer对象启动等待键盘输入，然后不断等待客户端接入
     * 如果有客户端接入，就可一个服务器线程放入Map中
     */
    public void start() {
        new Writer().start();
        try{
           while(true) {
               Socket socket = serverSocket.accept();
               System.out.println(++id + "号客户端接入 (" + socket.getInetAddress()
                       +","+ socket.getPort() + ")");
               ServerThread thread = new ServerThread(id, socket);
               socketList.put(id, thread);
               thread.run();
           }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭服务器
     */
    public void close() {
        broadcast("服务器关闭中...");
        try {
            if(serverSocket!=null) {
                serverSocket.close();
            }
        }catch (IOException e) {
            System.out.println("服务器关闭中...");
        }finally {
            System.exit(0);
        }
    }
    public void broadcast(String message, Long...args) {
        LinkedList<Long> list = new LinkedList<>();
        Set<Map.Entry<Long, ServerThread>> set = socketList.entrySet();
        for(Map.Entry<Long, ServerThread> entry:set) {
            if(args.length!=0 && Objects.equals(entry.getKey(), args[0])) continue;
            list.add(entry.getKey());
        }
        for(Long id : list){
            unicast(id, message);
        }
    }
    public void unicast(Long id, String data) {
        ServerThread socket = socketList.get(id);
        if(socket!=null) socket.send(data);
    }
    private class ServerThread implements Runnable {
        private Long id;
        private Socket socket;
        private InputStream in;
        private OutputStream out;
        private PrintWriter writer;

        ServerThread(Long id, Socket socket) {
            try{
                this.id = id;
                this.socket = socket;
                this.in = socket.getInputStream();
                this.out = socket.getOutputStream();
                this.writer = new PrintWriter(new OutputStreamWriter(out));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            new Reader().start();
        }
        public void send(String data) {
            if(!socket.isClosed() && data!=null) {
                writer.println(data);
                writer.flush();
            }
        }
        public void close(){
            try{
                if(writer!=null){
                    writer.close();
                }
                if(in!=null){
                    in.close();
                }
                if(out!=null){
                    out.close();
                }
                if(socket!=null){
                    socket.close();
                }
                socketList.remove(id);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        private class Reader extends Thread{
            private BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            @Override
            public void run() {
                try{
                    String line = "";
                    while(!socket.isClosed() && line!=null && !"exit".equals(line)){
                        line=reader.readLine();
                        if(line!=null && !line.equals("exit")) {
                            try {
                                System.out.println("客户端"+id+"号: "+line);
                            } catch (NumberFormatException e) {
                                System.out.println("必须输入连接id号");
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.println("发送的消息不能为空");
                            } catch (NullPointerException e) {
                                System.out.println("连接不存在或已经断开");
                            }
                        }
                    }
                    System.out.println(id+":客户端主动断开连接");
                    close();
                }catch(IOException e) {
                    System.out.println(id+":连接已断开");
                }finally{
                    try{
                        if(reader!=null){
                            reader.close();
                        }
                        close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private class Writer extends Thread{
        private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        @Override
        public void run() {
           String line="";
           while(true) {
               try{
                   line = reader.readLine();
                   if("exit".equals(line)) break;
               }catch (IOException e) {
                   e.printStackTrace();
               }
               if(line!=null && !line.equals("exit")){
                   try{
                       String[] data = line.split(":");
                       if("*".equals(data[0])){
                           broadcast("Server:"+data[1]);
                       }else{
                           unicast(Long.parseLong(data[0]),"Server:"+data[1]);
                       }
                       // 有可能发生的异常
                   }catch(NumberFormatException e){
                       System.out.print("必须输入连接id号\n");
                   }catch(ArrayIndexOutOfBoundsException e){
                       System.out.print("发送的消息不能为空\n");
                   }catch(NullPointerException e){
                       System.out.print("连接不存在或已经断开\n");
                   }
               }
           }
           System.out.println("服务器退出");
           close();
        }
    }
    public static void main(String[] args) {
        int port;
        if(args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入端口号：");
            port = sc.nextInt();
        }
        new Server(port).start();
    }
}
