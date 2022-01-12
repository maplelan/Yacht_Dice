import java.net.*;
import java.io.*;

public class Server {
    private DataOutputStream   outstream;
    private DataInputStream    instream;
    private Socket  socket;

    public Server(Socket s) {
        try{
            socket = s;
            instream = new DataInputStream(socket.getInputStream());
            outstream = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
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
        System.out.println("needReceive");
        outstream.writeUTF("2");
    }
    public void cls() throws IOException {
        outstream.writeUTF("3");
    }
    public void pressEnter() throws IOException {
        System.out.println("pressEnter");
        outstream.writeUTF("4");
    }
    public String receive() throws IOException {
        String str = instream.readUTF();
        System.out.println(str);
        return str;
    }
    public void close() throws IOException {
        outstream.writeUTF("9");
        socket.close();
    }
}
