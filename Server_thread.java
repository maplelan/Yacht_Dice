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
            System.out.println("Server Thread�w�Ұ�");
            server.print("�п�J�ʺ�(5�ӭ^�夺)\n>");
            String name = Str5byte(server);
            currplayer.setname(name);
            currplayer.setlock(lock);
            System.out.println("�ʺ�:" + currplayer.name() + ",�}�C��}:" + currplayer.pos());
            for(int i = 0; i < playerlist.size(); i++){
                System.out.println(i + "." + playerlist.get(i).name() + ", " + playerlist.get(i).pos() + ", " + (playerlist.get(i).waiting() ? "true" : "false") + ", " + (playerlist.get(i).playing() ? "true" : "false"));
            }
            while (running){
                currplayer.setwait(false);
                server.cls();
                server.print("��J0�i�J���ݰ�,��J1�i�J�ܽеo�e��,��J2�����s�u\n" + currplayer.name().trim() + ">");
                switch (InputNumb(0,2,server)){
                    case 0:
                        server.cls();
                        server.println("�i�J���ݰ�,�����ܽФ�...");
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
                            server.println("�ثe�L�H���ݤ�\n���UEnter�~��");
                            pressEnter(server);
                        }else{
                            server.println("�ثe���ݤ�:");
                            for (int i = 0; i < cov.size(); i++) {
                                server.println((i+1) + ". \"" + cov.get(i).name().trim() + "\"");
                            }
                            server.print("��J�s���o�e�ܽ� ��J0�^��W�@��\n" + currplayer.name().trim() + ">");
                            int chose = InputNumb(0,cov.size(),server);
                            if(chose != 0) {
                                player covp = cov.get(chose - 1);
                                Server covs = covp.server();
                                server.println("�A��\"" + covp.name().trim() + "\"�o�e�F�ܽ� ���ݹ��^��...");
                                covs.print("\"" + currplayer.name().trim() + "\"��A�e�X�F�ܽ� ��J0�ڵ� ��J1�P�N\n" + covp.name().trim() + ">");
                                if (InputNumb(0, 1, covs) == 0) {
                                    server.println("\"" + covp.name().trim() + "\"�ڵ��F�A���ܽ�\n���UEnter�~��");
                                    covs.println("�A�ڵ��F\"" + currplayer.name().trim() + "\"���ܽ�");
                                    pressEnter(server);
                                }else{
                                    server.cls();
                                    covs.cls();
                                    server.println("\"" + covp.name().trim() + "\"�����F�A���ܽ�");
                                    covs.println("�A�����F\"" + currplayer.name().trim() + "\"���ܽ�");
                                    currplayer.setplay(true);
                                    currplayer.setwait(false);
                                    covp.setplay(true);
                                    covp.setwait(false);
                                    //�}�C����Thread
                                    Game_thread Gt = new Game_thread(currplayer, covp, playerlist);
                                    Thread th = new Thread(Gt);
                                    th.start();
                                    pause();
                                    //wait��
                                    System.out.println("�Ѱ�wait");
                                    covp.end();
                                    unpause(covp.lock());
                                }
                            }
                        }
                        break;
                    case 2:
                        server.cls();
                        server.println("�����s�u��...");
                        server.close();
                        running = false;
                        playerlist.remove(currplayer.pos());
                        for (int i = currplayer.pos(); i < playerlist.size(); i++){
                            playerlist.get(i).setpos(i);
                        }
                        break;
                }
            }
            System.out.println("Thread����");
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
    //�Ȱ�
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
    //�}�l
    public synchronized void unpause(Object lo) {
        synchronized (lo) {
            lo.notify();
        }
    }
    //���UEnter
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
    //��J���w�d��Ʀr
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
                        s.print("��J���~ �Э��s��J����" + min + "��" + max + "����\n>");
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    s.print("��J���~ �Э��s��J����" + min + "��" + max + "����\n>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (con);
        }catch (IOException e){
            System.out.println("�s�u����!");
            System.exit(1);
        }
        return input;
    }
    //���o5bytes�H������r �øɨ���5bytes
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
                    server.print("\"" + str.toString().trim() + "\"�w�Q�ϥ� �Э��s��J\n>");
                }
            }else if (bytes.length < 1){
                server.print("��J����r���� �Э��s��J\n>");
            }else {
                server.print("��J����r�j��5�ӭ^�� �Э��s��J\n>");
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

      
