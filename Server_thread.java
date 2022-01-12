import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;

class Server_thread implements Runnable{
    private player currplayer;
    private Object lock = new Object();
    private boolean running = true;
    private final ArrayList<player> playerlist;

    public Server_thread(ArrayList<player> p,int po){
        playerlist = p;
        currplayer = playerlist.get(po);
    }
    public synchronized void run(){
        try{
            Server server = currplayer.server();
            System.out.println("Server Thread已啟動");
            server.print("請輸入暱稱(5個英文內)\n>");
            String name = Str5byte(server);
            currplayer.setname(name);
            currplayer.setlock(lock);
            System.out.println("暱稱:" + currplayer.name() + ",陣列位址:" + currplayer.pos());
            for(int i = 0; i < playerlist.size(); i++){
                System.out.println(i + "." + playerlist.get(i).name() + ", " + playerlist.get(i).pos() + ", " + (playerlist.get(i).waiting() ? "true" : "false") + ", " + (playerlist.get(i).playing() ? "true" : "false"));
            }
            while (running){
                currplayer.setwait(false);
                server.cls();
                server.print("輸入0進入等待區,輸入1進入邀請發送區,輸入2結束連線\n" + currplayer.name().trim() + ">");
                switch (InputNumb(0,2,server)){
                    case 0:
                        server.cls();
                        server.println("進入等待區,等待邀請中...");
                        currplayer.setwait(true);
                        pause();
                        break;
                    case 1:
                        ArrayList<player> cov = new ArrayList<>();
                        for(int i = 0; i < playerlist.size(); i++){
                            System.out.println(i + "." + playerlist.get(i).name() + ", " + playerlist.get(i).pos() + ", " + (playerlist.get(i).waiting() ? "true" : "false") + ", " + (playerlist.get(i).playing() ? "true" : "false"));
                            if(playerlist.get(i).waiting()){
                                cov.add(playerlist.get(i));
                            }
                        }
                        server.cls();
                        if(cov.size() <= 0){
                            server.println("目前無人等待中\n按下Enter繼續");
                            pressEnter(server);
                        }else{
                            server.println("目前等待中:");
                            for (int i = 0; i < cov.size(); i++) {
                                server.println((i+1) + ". \"" + cov.get(i).name().trim() + "\"");
                            }
                            server.print("輸入編號發送邀請 輸入0回到上一頁\n" + currplayer.name().trim() + ">");
                            int chose = InputNumb(0,cov.size(),server);
                            if(chose != 0) {
                                player covp = cov.get(chose - 1);
                                Server covs = covp.server();
                                server.println("你對\"" + covp.name().trim() + "\"發送了邀請 等待對方回應...");
                                covs.print("\"" + currplayer.name().trim() + "\"對你送出了邀請 輸入0拒絕 輸入1同意\n" + covp.name().trim() + ">");
                                if (InputNumb(0, 1, covs) == 0) {
                                    server.println("\"" + covp.name().trim() + "\"拒絕了你的邀請\n按下Enter繼續");
                                    covs.println("你拒絕了\"" + currplayer.name().trim() + "\"的邀請");
                                    pressEnter(server);
                                }else{
                                    server.cls();
                                    covs.cls();
                                    server.println("\"" + covp.name().trim() + "\"接受了你的邀請");
                                    covs.println("你接受了\"" + currplayer.name().trim() + "\"的邀請");
                                    currplayer.setplay(true);
                                    currplayer.setwait(false);
                                    covp.setplay(true);
                                    covp.setwait(false);
                                    //開遊戲用Thread
                                    Game_thread Gt = new Game_thread(currplayer, covp, playerlist);
                                    Thread th = new Thread(Gt);
                                    th.start();
                                    pause();
                                    //wait後
                                    System.out.println("解除wait");
                                    covp.end();
                                    unpause(covp.lock());
                                }
                            }
                        }
                        break;
                    case 2:
                        server.cls();
                        server.println("結束連線中...");
                        server.close();
                        running = false;
                        playerlist.remove(currplayer.pos());
                        for (int i = currplayer.pos(); i < playerlist.size(); i++){
                            playerlist.get(i).setpos(i);
                        }
                        break;
                }
            }
            System.out.println("Thread結束");
        }
        catch(IOException e){
            e.printStackTrace();
            running = false;
            playerlist.remove(currplayer.pos());
            for (int i = currplayer.pos(); i < playerlist.size(); i++){
                playerlist.get(i).setpos(i);
            }
        }
    }
    //暫停
    public void pause(){
        try {
            while (true){
                synchronized (lock) {
                    lock.wait();
                }
                System.out.println("notify!");
                if(!currplayer.playing()){
                    break;
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    //開始
    public synchronized void unpause(Object lo) {
        synchronized (lo) {
            lo.notify();
        }
    }
    //按下Enter
    public void pressEnter(Server server){
        try {
            while (true) {
                server.pressEnter();
                if(server.receive().charAt(0) == '4'){
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //輸入指定範圍數字
    public Integer InputNumb(int min, int max, Server s) {
        int input = 0;
        boolean con = true;
        try {
            do {
                try {
                    s.needReceive();
                    input = Integer.parseInt(s.receive(), 10);
                    if (input >= min && input <= max) {
                        con = false;
                    } else {
                        s.print("輸入錯誤 請重新輸入介於" + min + "到" + max + "的數\n>");
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    s.print("輸入錯誤 請重新輸入介於" + min + "到" + max + "的數\n>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (con);
        }catch (IOException e){
            System.out.println("連線失敗!");
            System.exit(1);
        }
        return input;
    }
    //取得5bytes以內的文字 並補足至5bytes
    public String Str5byte(Server server) throws IOException {
        StringBuilder str;
        while (true){
            str = new StringBuilder(input(server));
            byte[] bytes = str.toString().trim().getBytes();
            if(bytes.length <= 5 && bytes.length > 0){
                boolean norep = true;
                for(int i = 0; i < playerlist.size() - 1; i++){
                    System.out.println(str.toString().trim() + ", " + playerlist.get(i).name());
					if(playerlist.get(i).name() != null){
						if(str.toString().trim().equals(playerlist.get(i).name().trim())){
							norep = false;
						}
					}
                }
                if(norep) {
                    for (int i = 0; i < 5 - bytes.length; i++){
                        str.append(" ");
                    }
                    break;
                }else{
                    server.print("\"" + str.toString().trim() + "\"已被使用 請重新輸入\n>");
                }
            }else if (bytes.length < 1){
                server.print("輸入的文字為空 請重新輸入\n>");
            }else {
                server.print("輸入的文字大於5個英文 請重新輸入\n>");
            }
        }
        return str.toString();
    }
    public String input(Server server) throws IOException {
        String out;
        server.needReceive();
        out = server.receive();
        return out;
    }
}

      
