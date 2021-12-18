import java.io.*;
import java.net.*;

public class Client {
    Socket socket;
    DataOutputStream  outstream;
    DataInputStream  instream;
    String messagein;
    static String messageout;

    public Client(String ip, int port) {
        try{
            socket=new Socket(InetAddress.getByName(ip),port);
            outstream = new DataOutputStream(socket.getOutputStream());
            instream=new DataInputStream(socket.getInputStream());
        } catch(IOException e){
            System.out.println("³s½u¥¢±Ñ!");
            System.exit(1);
        }
    }
    public void send(String s) throws IOException {
        outstream.writeUTF(s);
    }
    public String receive() throws IOException {
        return instream.readUTF();
    }
    public void close() throws IOException {
        socket.close();
    }
}
