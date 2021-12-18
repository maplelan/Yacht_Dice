import java.net.*;
import java.io.*;

public class Server {
    ServerSocket  SS;
    DataOutputStream   outstream;
    DataInputStream    instream;
    Socket  socket;

    public Server(int port) {
        try{
            SS = new ServerSocket(port);
            System.out.println("等待連接中...");
        }
        catch(IOException e){
            System.out.println(e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void accept() throws IOException {
        socket = SS.accept();
        instream = new DataInputStream(socket.getInputStream());
        outstream = new DataOutputStream(socket.getOutputStream());
        System.out.println("連接成功!");
    }
    public void print(String s) throws IOException {
        System.out.print(s);
        outstream.writeUTF("0" + s);
    }
    public void send(String s) throws IOException {
        outstream.writeUTF("0" + s);
    }
    public void println(String s) throws IOException {
        System.out.println(s);
        outstream.writeUTF("1" + s);
    }
    public void sendln(String s) throws IOException {
        outstream.writeUTF("1" + s);
    }
    public void needReceive() throws IOException {
        outstream.writeUTF("2");
    }
    public void cls() throws IOException {
        outstream.writeUTF("3");
    }
    public void pressEnter() throws IOException {
        outstream.writeUTF("4");
    }
    public String receive() throws IOException {
        return instream.readUTF();
    }
    public void close() throws IOException {
        outstream.writeUTF("9");
        socket.close();
    }
}
