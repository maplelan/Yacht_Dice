import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;

class Game_thread implements Runnable{
    private final player player1, player2;
    private final Server server1, server2;
    private final ArrayList<player> playerlist;
    //�զX�W��
    final String[] Categories_Map_ZHT = {"  �@�I  ","  �G�I  ","  �T�I  ","  �|�I  ","  ���I  ","  ���I  ","  �p�p  ","���y +35","  ����  ","�|��P��","  ��Ī  ","  �p��  ","  �j��  ","  �ָ�  "," �`�n�� "} ;
    //�}�C�ഫ��Map
    final int[] DataToShow = {1,2,3,4,5,6,-1,-1,0,7,8,9,10,11,-1}, ShowToYacht = {1,2,3,4,5,6,0,7,8,9,10,11}
            , YachtToScore = {8,0,1,2,3,4,5,9,10,11,12,13};
    //���ƪ�
    int[][] score = new int[2][15];
    //���a�W��
    String player_s="",player_c="",player_sf="",player_cf="", co = "";

    public Game_thread(player p1, player p2, ArrayList<player> p){
        playerlist = p;
        player1 = p1;
        player2 = p2;
        server1 = player1.server();
        server2 = player2.server();
    }
    public void run(){
        try {
            //�{�b�O�_�����A�ݪ��^�X,�C���O�_�~��
            boolean Sturn = true, continued = true;
            player_sf = player1.name();
            player_s = player_sf.trim();
            player_cf = player2.name();
            player_c = player_cf.trim();
            //�D�{���j��
            while (continued) {
                s_print("1.��" + player_s + "�}�l 2.��" + player_c + "�}�l 3.�H��\n" + player_s + ">");
                switch (InputNumb(1, 3, true,0)) {
                    case 1:
                        Sturn = true;
                        break;
                    case 2:
                        Sturn = false;
                        break;
                    case 3:
                        Sturn = random(0, 1) == 1;
                }
                //���ƪ��l��
                for (int i = 0; i < score.length; i++) {
                    for (int j = 0; j < score[i].length; j++) {
                        if (j == 6 || j == 14) {
                            score[i][j] = 0;
                        } else {
                            score[i][j] = -1;
                        }
                    }
                }
                s_println("��" + (Sturn ? player_s : player_c) + "�}�l");
                s_print(player_s + "���UEnter�~��");
                co = pressEnter(true);
                //�C���^�X
                for (int turn = 2; turn < 26; turn++) {
                    showTable((Sturn ? player_s : player_c) + "���^�X,���UEnter�}�l��\n" + (Sturn ? player_s : player_c) + ">", turn / 2);
                    co = pressEnter(Sturn);
                    int[][] dice = new int[2][5];
                    for (int i = 0; i < 5; i++) {
                        dice[0][i] = 0;
                        dice[1][i] = 1;
                    }
                    boolean check = false;
                    //���l
                    for (int TryTimes = 0; TryTimes < 3; TryTimes++) {
                        if (check) {
                            break;
                        }
                        //���l
                        for (int i = 0; i < 5; i++) {
                            if (dice[1][i] != 0) {
                                dice[1][i] = random(1, 6);
                            }
                        }
                        //���P��ܲզX
                        boolean rechose = true;
                        do {
                            //���
                            if (TryTimes < 2) {
                                while (true) {
                                    showTableDice(Yacht(dice), turn / 2);
                                    showDice(dice);
                                    s_print("��J�Q�洫����l�s��,��J\"0\"�i�J�զX��ܩέ���\n" + (Sturn ? player_s : player_c) + "(" + (TryTimes + 1) + "/3)>");
                                    int ch = InputNumb(0, 5, Sturn,25565);
                                    if (ch == 0) {
                                        break;
                                    }else if(ch == 25565){
                                        s_print("���ռҦ�(ID.25565): �^�X�ƪ����[��12,�����C���^�X");
                                        turn = 25;
                                        pressEnter(Sturn);
                                        break;
                                    }else{
                                        int sa = dice[0][ch - 1];
                                        dice[0][ch - 1] = dice[1][ch - 1];
                                        dice[1][ch - 1] = sa;
                                    }
                                }
                            }
                            //��ܲզX
                            int[] yacht = Yacht(dice);
                            showTableDice(yacht, turn / 2);
                            showDice(dice);
                            s_print("��J�Q��ܪ��զX�s��" + (TryTimes < 2 ? ",��J\"0\"����,��J\"13\"�^���l���\n" : "\n") + (Sturn ? player_s : player_c) + "(" + (TryTimes + 1) + "/3)>");
                            //���P�w��L����
                            while (true) {
                                int ch = TryTimes < 2 ? InputNumb(0, 13, Sturn,0) : InputNumb(1, 12, Sturn,0);
                                if (ch == 0) {
                                    //����
                                    rechose = false;
                                    break;
                                } else if (ch != 13) {
                                    //�N���ѵ��G�s�ܤ��ƪ� �íp�����
                                    if (score[Sturn ? 0 : 1][YachtToScore[ShowToYacht[ch - 1]]] == -1) {
                                        score[Sturn ? 0 : 1][YachtToScore[ShowToYacht[ch - 1]]] = yacht[ShowToYacht[ch - 1]];
                                        score[Sturn ? 0 : 1][6] = 0;
                                        for (int i = 0; i < 6; i++) {
                                            if (score[Sturn ? 0 : 1][i] != -1) {
                                                score[Sturn ? 0 : 1][6] += score[Sturn ? 0 : 1][i];
                                            }
                                        }
                                        if (score[Sturn ? 0 : 1][6] >= 63) {
                                            score[Sturn ? 0 : 1][7] = 35;
                                        }
                                        score[Sturn ? 0 : 1][14] = 0;
                                        for (int i = 6; i < 14; i++) {
                                            if (score[Sturn ? 0 : 1][i] != -1) {
                                                score[Sturn ? 0 : 1][14] += score[Sturn ? 0 : 1][i];
                                            }
                                        }
                                        rechose = false;
                                        check = true;
                                        Sturn = !Sturn;
                                        break;
                                    } else {
                                        s_println("��J���s���w�g����L�F,�Э��s��J\n" + (Sturn ? player_s : player_c) + "(" + (TryTimes + 1) + "/3)>");
                                    }
                                } else {
                                    break;
                                }
                            }
                        } while (rechose);
                    }
                    System.out.print(turn + ", ");
                }
                //�C������
                showTable("�C������,", 12);
                if (score[0][14] > score[1][14]) {
                    s_println(player_s + "�ӧQ");
                } else if (score[0][14] < score[1][14]) {
                    s_println(player_c + "�ӧQ");
                } else {
                    s_println("����");
                }
                //�M�w�O�_�~��
                s_print("��J\"0\"���� ��J\"1\"�~��\n" + player_s + ">");
                if (InputNumb(0, 1, true,0) == 0) {
                    continued = false;
                } else {
                    cls();
                }
            }
            playerlist.get(player1.pos()).end();
            unpause(playerlist.get(player1.pos()).lock());
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    //�Ѱ��Ȱ�
    public synchronized void unpause(Object lock) {
        synchronized (lock) {
            lock.notify();
        }
    }
    public void s_print(String s) throws IOException {
        server1.print(s);
        server2.print(s);
    }
    public void s_println(String s) throws IOException {
        server1.println(s);
        server2.println(s);
    }
    //��ܻ�l�I��
    public void showDice(int[][] dice) throws IOException {
        s_println("+----+-1-+-2-+-3-+-4-+-5-+");
        for(int i = 0; i < 2; i++){
            s_print(i == 0 ? "|�O�d|" : "|��X|");
            for (int j = 0; j < 5; j++){
                s_print(" " + (dice[i][j] != 0 ? dice[i][j] : " ") + " |");
            }
            s_print("\n");
        }
        s_println("+----+---+---+---+---+---+");
    }
    //��ܤ��ƪ�
    public void showTable(String input,int turn) throws IOException {
        cls();
        s_println("�^�X:" + turn + "/12");
        String leftAlignFormat = "| %s | %-5s | %-5s |%n", subFormat = "| %s | %-2d/63 | %-2d/63 |%n", Separate = "+----------+-------+-------+\n";
        s_print(Separate);
        s_print(String.format("| %s | %s | %s |%n", "�զX�W��" , player_sf, player_cf));
        s_print(Separate);
        for (int i = 0; i < Categories_Map_ZHT.length; i++){
            if(i == 6){
                s_print(String.format(subFormat, Categories_Map_ZHT[i], score[0][i], score[1][i]));
            }else{
                s_print(String.format(leftAlignFormat, Categories_Map_ZHT[i], score[0][i]!=-1 ? score[0][i] : "", score[1][i]!=-1 ? score[1][i] : ""));
            }
            switch (i){
                case 5:
                case 7:
                case 13:
                    s_print(Separate);
            }
        }
        s_print(Separate);
        s_print(input);
    }
    //��ܦ����ѵ��G�����ƪ�
    public void showTableDice(int[] yacht,int turn) throws IOException {
        cls();
        s_println("�^�X:" + turn + "/12");
        String Format = "| %s | %-5s | %-5s |%n", LFormat = "| %s | %-5s | %-5s |%02d| %-5s |%n"
                , subFormat = "| %s | %-2d/63 | %-2d/63 |%n", Separate = "+----------+-------+-------+--+-------+\n";
        s_print(Separate);
        s_print(String.format("| %s | %s | %s |  | %s |%n", "�զX�W��" , player_sf, player_cf, "���� "));
        s_print(Separate);
        int can = 1;
        for (int i = 0; i < Categories_Map_ZHT.length; i++){
            if(DataToShow[i] != -1){
                s_print(String.format(LFormat, Categories_Map_ZHT[i], score[0][i]!=-1 ? score[0][i] : "", score[1][i]!=-1 ? score[1][i] : "", can++ ,yacht[DataToShow[i]]));
            }else if(i == 6){
                s_print(String.format(subFormat, Categories_Map_ZHT[i], score[0][i], score[1][i]));
            }else {
                s_print(String.format(Format, Categories_Map_ZHT[i], score[0][i]!=-1 ? score[0][i] : "", score[1][i]!=-1 ? score[1][i] : ""));
            }
            switch (i){
                case 5:
                case 7:
                case 13:
                    s_print(Separate);
            }
        }
        s_print("+----------+-------+-------+\n");
    }
    //�H�����;�
    public Integer random(int min, int max) {
        return (int)(Math.floor(Math.random() * (max - min + 1)) + min);
    }
    //�M�Ū���
    public void cls() {
        try {
            server1.cls();
            server2.cls();
        }catch (IOException e){
            System.out.println("CLS failed!");
        }
    }
    //���ѻ�l�զX
    public int[] Yacht(int[][] dicein) {
        int[] dice = new int[5];
        for (int i =0; i < 5;i++){
            dice[i] = dicein[0][i] != 0 ? dicein[0][i] : dicein[1][i];
        }
        sort(dice);
        int[] analyze = new int[12];
        //Aces 1, Deuces 2, Threes 3, Fours 4, Fives 5, Sixes 6, Choice 0
        for(int i = 0; i < 5; i++){
            analyze[dice[i]] += dice[i];
            analyze[0] += dice[i];
        }
        //4 of a Kind 7
        if((dice[0] == dice[1] && dice[0] == dice[2] && dice[0] == dice[3]) || (dice[1] == dice[2] && dice[1] == dice[3] && dice[1] == dice[4])){
            analyze[7] = analyze[0];
        }
        //Full House 8
        if(dice[0] != dice[4] && ((dice[0] == dice[1] && dice[0] == dice[2] && dice[3] == dice[4]) || (dice[0] == dice[1] && dice[2] == dice[3] && dice[2] == dice[4]))){
            analyze[8] = analyze[0];
        }
        //S. Straight 9, L. Straight 10
        int Pcount = 0, Crcount = 0,start = dice[0];
        for(int i = 0; i < 5; i++){
            if(dice[i] == start){
                Crcount++;
                start++;
            }else if (dice[i] == start+1){
                start += 2;
                Crcount = 1;
            }
            if(Crcount > Pcount){Pcount = Crcount;}
            if(start > 6){break;}
        }
        if(Pcount >= 4){
            analyze[9] = 15;
        }
        if(Pcount == 5){
            analyze[10] = 30;
        }
        //Yacht 11
        if(dice[0] == dice[1] && dice[0] == dice[2] && dice[0] == dice[3] && dice[0] == dice[4]){
            analyze[11] = 50;
        }
        return analyze;
    }
    //�Ƨ�
    public void sort(int[] sco) {
        int temp;
        for (int i = 0; i < sco.length - 1; i++) {
            boolean Flag = false;
            for (int j = 0; j < sco.length - 1 - i; j++) {
                if (sco[j] > sco[j + 1]) {
                    temp = sco[j];
                    sco[j] = sco[j + 1];
                    sco[j + 1] = temp;
                    Flag = true;
                }
            }
            if (!Flag){break;}
        }
    }
    //��J���w�d��Ʀr
    public Integer InputNumb(int min, int max, boolean IsServer, int sp) {
        int input = 0;
        boolean con = true;
        try {
            do {
                try {
                    input = Integer.parseInt(inputNet(IsServer), 10);
                    if ((input >= min && input <= max) || (sp != 0 && input == sp)) {
                        con = false;
                    } else {
                        s_print("��J���~ �Э��s��J����" + min + "��" + max + "����\n>");
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    s_print("��J���~ �Э��s��J����" + min + "��" + max + "����\n>");
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
    //���o��J
    public String inputNet(boolean IsServer) throws IOException {
        String out;
        if(IsServer){
            server1.needReceive();
            out = server1.receive();
            server2.sendln(out);
        }else {
            server2.needReceive();
            out = server2.receive();
            server1.sendln(out);
        }
        return out;
    }
    //��ENTER�~��
    public String pressEnter(boolean IsServer) throws IOException {
        String ck;
        if(IsServer){
            server1.pressEnter();
            ck = server1.receive();
        }else {
            server2.pressEnter();
            ck = server2.receive();
        }
        return ck;
    }
}

      
