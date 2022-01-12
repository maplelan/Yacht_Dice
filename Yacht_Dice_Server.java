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
        //伺服器設定
        int port = 9487;
        System.out.print("按下\"Enter\"以預設埠號繼續,或輸入一個埠號\n>");
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
                        System.out.print("輸入的埠號不可用 請重新輸入\n>");
                    }
                }catch (InputMismatchException | NumberFormatException e) {
                    System.out.print("輸入的埠號不可用 請重新輸入\n>");
                }
            }
        }
        if(port == 9487){
            System.out.println("以預設埠號繼續");
        }else {
            System.out.println("以" + port + "埠繼續");
        }
        //主程式
        try{
            System.out.println("建立連線...");
            ServerSocket SS = new ServerSocket(port);
            while (true) {
                System.out.println("等待連接中...");
                Socket socket = SS.accept();
                System.out.println("成功取得連線");
                Server server = new Server(socket);
                int usepos = playerlist.size();
                player play = new player();
                play.frset(usepos);
                play.setserver(server);
                playerlist.add(play);
                Server_thread st = new Server_thread(playerlist, usepos);
                Thread th = new Thread(st);
                th.start();
                System.out.println("啟動Server Thread");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //按ENTER繼續
    public static String pressEnter(){
        Scanner sc = new Scanner(System.in);
        String ck;
        ck = sc.nextLine();
        return ck;
    }
}
