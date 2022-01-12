import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Yacht_Dice_Server {
    //main
    public static void main(String[] args){
        ArrayList<player> playerlist = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        //���A���]�w
        int port = 9487;
        System.out.print("���U\"Enter\"�H�w�]���~��,�ο�J�@�Ӱ�\n>");
        String co = pressEnter();
        if(co.length() != 0){
            boolean sec = false;
            while (true){
                try{
                    if(sec){
                        port = sc.nextInt();
                    }else {
                        port = Integer.parseInt(co,10);
                        sec = true;
                    }
                    if(port >= 1024 && port <= 65535){
                        break;
                    }else{
                        System.out.print("��J���𸹤��i�� �Э��s��J\n>");
                    }
                }catch (InputMismatchException | NumberFormatException e) {
                    System.out.print("��J���𸹤��i�� �Э��s��J\n>");
                }
            }
        }
        if(port == 9487){
            System.out.println("�H�w�]���~��");
        }else {
            System.out.println("�H" + port + "���~��");
        }
        //�D�{��
        try{
            System.out.println("�إ߳s�u...");
            ServerSocket SS = new ServerSocket(port);
            while (true) {
                System.out.println("���ݳs����...");
                Socket socket = SS.accept();
                System.out.println("���\���o�s�u");
                Server server = new Server(socket);
                int usepos = playerlist.size();
                player play = new player();
                play.frset(usepos);
                play.setserver(server);
                playerlist.add(play);
                Server_thread st = new Server_thread(playerlist, usepos);
                Thread th = new Thread(st);
                th.start();
                System.out.println("�Ұ�Server Thread");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //��ENTER�~��
    public static String pressEnter(){
        Scanner sc = new Scanner(System.in);
        String ck;
        ck = sc.nextLine();
        return ck;
    }
}
