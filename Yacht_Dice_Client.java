import java.io.*;
import java.util.Scanner;

class Yacht_Dice_Client {
    public static void main(String[] args) {
        try {
            Scanner sc;
            sc = new Scanner(System.in);
            //======================
            Client client;
            System.out.print("�п�J�n�s�u��IP��}\n>");
            //local  = "127.0.0.1"
            String IP;
            int port = 9487;
            String[] IPport = sc.next().trim().split(":");
            if (IPport.length < 2) {
                IP = IPport[0];
                System.out.println("IP:" + IP + " �𸹬��w�]");
            } else {
                IP = IPport[0];
                port = Integer.parseInt(IPport[1], 10);
                System.out.println("IP:" + IP + " ��:" + port);
            }
            client = new Client(IP, port);
            System.out.println("�s�u���\!");
            //======================
            boolean run = true;
            while (run) {
                String get;
                get = client.receive();
                switch (get.substring(0, 1)) {
                    case "0":
                        System.out.print(get.substring(1));
                        break;
                    case "1":
                        System.out.println(get.substring(1));
                        break;
                    case "2":
                        client.send(sc.next());
                        break;
                    case "3":
                        cls();
                        break;
                    case "4":
                        Scanner Ent = new Scanner(System.in);
                        Ent.nextLine();
                        client.send("4");
                        break;
                    case "9":
                        System.out.print(get.substring(1));
                        client.close();
                        run = false;
                        break;
                }
            }
        }catch (IOException e){
            System.out.println("�s�u����!");
        }
    }
    //�M�Ū���
    public static void cls() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }catch (IOException | InterruptedException e){
            System.out.println("CLS failed!");
        }
    }
}