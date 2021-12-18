import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Yacht_Dice_Server {
    //���A��
    static Server server;
    //�զX�W��
    //Categories_Map_EN = {"Aces","Deuces","Threes","Fours","Fives","Sixes","Subtotal","+35 Bonus","Choice","4 of a Kind","Full House","S. Straight","L. Straight","Yacht","Total"},
    final static String[] Categories_Map_ZHT = {"  �@�I  ","  �G�I  ","  �T�I  ","  �|�I  ","  ���I  ","  ���I  ","  �p�p  ","���y +35","  ����  ","�|��P��","  ��Ī  ","  �p��  ","  �j��  ","  �ָ�  "," �`�n�� "} ;
    //�}�C�ഫ��Map
    final static int[] DataToShow = {1,2,3,4,5,6,-1,-1,0,7,8,9,10,11,-1}, ShowToYacht = {1,2,3,4,5,6,0,7,8,9,10,11}
            , YachtToScore = {8,0,1,2,3,4,5,9,10,11,12,13};
    //���ƪ�
    static int[][] score = new int[2][15];
    //���a�W��
    static String player_s="",player_c="",player_sf="",player_cf="", co = "";
    //main
    public static void main(String[] args){
        try {
            Scanner sc = new Scanner(System.in);
            //�{�b�O�_�����A�ݪ��^�X,�C���O�_�~��
            boolean Sturn = true, continued = true;
            //���A���]�w
            int port = 9487;
            System.out.print("���U\"Enter\"�H�w�]���~��,�ο�J�@�Ӱ�\n>");
            co = pressEnter(true);
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
            server = new Server(port);
            server.accept();
            //��l�]�w
            server.print("���A�ݿ�J�ʺ�(5�ӭ^�夺)\n>");
            player_sf = Str5byte(true);
            player_s = player_sf.trim();
            server.print("�Ȥ�ݿ�J�ʺ�(5�ӭ^�夺)\n>");
            player_cf = Str5byte(false);
            player_c = player_cf.trim();
            //�D�{���j��
            while (continued){
                server.print("1.��" + player_s + "�}�l 2.��" + player_c + "�}�l 3.�H��\n" + player_s + ">");
                switch (InputNumb(1,3, true)){
                    case 1:
                        Sturn = true;
                        break;
                    case 2:
                        Sturn = false;
                        break;
                    case 3:
                        Sturn = random(0,1) == 1;
                }
                //���ƪ��l��
                for (int i = 0;i < score.length; i++){
                    for (int j = 0; j < score[i].length; j++){
                        if(j == 6 || j == 14) {
                            score[i][j] = 0;
                        }else{
                            score[i][j] = -1;
                        }
                    }
                }
                server.println("��" + (Sturn ? player_s : player_c) + "�}�l");
                server.print(player_s + "���UEnter�~��");
                co = pressEnter(true);
                //�C���^�X
                for(int turn = 2; turn < 26 ; turn++){
                    showTable((Sturn ? player_s : player_c) + "���^�X,���UEnter�}�l��\n" + (Sturn ? player_s : player_c) + ">",turn/2);
                    co = pressEnter(Sturn);
                    int[][] dice = new int[2][5];
                    for (int i = 0; i < 5; i++){
                        dice[0][i] = 0;
                        dice[1][i] = 1;
                    }
                    boolean check = false;
                    //���l
                    for (int TryTimes = 0; TryTimes < 3; TryTimes++){
                        if(check){
                            break;
                        }
                        //���l
                        for (int i = 0; i < 5; i++){
                            if(dice[1][i] != 0) {
                                dice[1][i] = random(1, 6);
                            }
                        }
                        //���P��ܲզX
                        boolean rechose = true;
                        do {
                            //���
                            if(TryTimes < 2){
                                while (true) {
                                    showTableDice(Yacht(dice), turn / 2);
                                    showDice(dice);
                                    server.print("��J�Q�洫����l�s��,��J\"0\"�i�J�զX��ܩέ���\n" + (Sturn ? player_s : player_c) + "(" + (TryTimes+1) + "/3)>");
                                    int ch = InputNumb(0, 5, Sturn);
                                    if (ch == 0) {
                                        break;
                                    } else {
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
                            server.print("��J�Q��ܪ��զX�s��" + (TryTimes < 2 ? ",��J\"0\"����,��J\"13\"�^���l���\n" : "\n") + (Sturn ? player_s : player_c) + "(" + (TryTimes+1) + "/3)>");
                            //���P�w��L����
                            while (true){
                                int ch = TryTimes < 2 ? InputNumb(0, 13, Sturn) : InputNumb(1, 12, Sturn);
                                if (ch == 0) {
                                    //����
                                    rechose = false;
                                    break;
                                }else if (ch != 13) {
                                    //�N���ѵ��G�s�ܤ��ƪ� �íp�����
                                    if(score[Sturn ? 0 : 1][YachtToScore[ShowToYacht[ch - 1]]] == -1) {
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
                                    }else {
                                        server.println("��J���s���w�g����L�F,�Э��s��J\n" + (Sturn ? player_s : player_c) + "(" + (TryTimes+1) + "/3)>");
                                    }
                                }else {
                                    break;
                                }
                            }
                        }while (rechose);
                    }
                }
                showTable("�C������,",12);
                if(score[0][14] > score[1][14]){
                    server.println(player_s + "�ӧQ");
                }else if(score[0][14] < score[1][14]){
                    server.println(player_c + "�ӧQ");
                }else {
                    server.println("����");
                }
                server.print("��J\"0\"���� ��J\"1\"�~��\n" + player_s + ">");
                if(InputNumb(0,1, true) == 0){
                    continued = false;
                }else {
                    cls();
                }
            }
            server.close();
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    //��ܻ�l
    public static void showDice(int[][] dice) throws IOException {
        server.println("+----+-1-+-2-+-3-+-4-+-5-+");
        for(int i = 0; i < 2; i++){
            server.print(i == 0 ? "|�O�d|" : "|��X|");
            for (int j = 0; j < 5; j++){
                server.print(" " + (dice[i][j] != 0 ? dice[i][j] : " ") + " |");
            }
            server.print("\n");
        }
        server.println("+----+---+---+---+---+---+");
    }
    //��ܤ��ƪ�
    public static void showTable(String input,int turn) throws IOException {
        cls();
        server.println("�^�X:" + turn + "/12");
        String leftAlignFormat = "| %s | %-5s | %-5s |%n", subFormat = "| %s | %-2d/63 | %-2d/63 |%n", Separate = "+----------+-------+-------+\n";
        server.print(Separate);
        server.print(String.format("| %s | %s | %s |%n", "�զX�W��" , player_sf, player_cf));
        server.print(Separate);
        for (int i = 0; i < Categories_Map_ZHT.length; i++){
            if(i == 6){
                server.print(String.format(subFormat, Categories_Map_ZHT[i], score[0][i], score[1][i]));
            }else{
                server.print(String.format(leftAlignFormat, Categories_Map_ZHT[i], score[0][i]!=-1 ? score[0][i] : "", score[1][i]!=-1 ? score[1][i] : ""));
            }
            switch (i){
                case 5:
                case 7:
                case 13:
                    server.print(Separate);
            }
        }
        server.print(Separate);
        server.print(input);
    }
    //��ܦ����ѵ��G�����ƪ�
    public static void showTableDice(int[] yacht,int turn) throws IOException {
        cls();
        server.println("�^�X:" + turn + "/12");
        String Format = "| %s | %-5s | %-5s |%n", LFormat = "| %s | %-5s | %-5s |%02d| %-5s |%n"
                , subFormat = "| %s | %-2d/63 | %-2d/63 |%n", Separate = "+----------+-------+-------+--+-------+\n";
        server.print(Separate);
        server.print(String.format("| %s | %s | %s |  | %s |%n", "�զX�W��" , player_sf, player_cf, "���� "));
        server.print(Separate);
        int can = 1;
        for (int i = 0; i < Categories_Map_ZHT.length; i++){
            if(DataToShow[i] != -1){
                server.print(String.format(LFormat, Categories_Map_ZHT[i], score[0][i]!=-1 ? score[0][i] : "", score[1][i]!=-1 ? score[1][i] : "", can++ ,yacht[DataToShow[i]]));
            }else if(i == 6){
                server.print(String.format(subFormat, Categories_Map_ZHT[i], score[0][i], score[1][i]));
            }else {
                server.print(String.format(Format, Categories_Map_ZHT[i], score[0][i]!=-1 ? score[0][i] : "", score[1][i]!=-1 ? score[1][i] : ""));
            }
            switch (i){
                case 5:
                case 7:
                case 13:
                    server.print(Separate);
            }
        }
        server.print("+----------+-------+-------+\n");
    }
    //�H�����;�
    public static Integer random(int min, int max) {
        return (int)(Math.floor(Math.random() * (max - min + 1)) + min);
    }
    //�M�Ū���
    public static void cls() {
        try {
            server.cls();
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }catch (IOException | InterruptedException e){
            System.out.println("CLS failed!");
        }
    }
    //���ѻ�l�զX
    public static int[] Yacht(int[][] dicein) {
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
    public static void sort(int[] sco) {
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
    public static Integer InputNumb(int min, int max, boolean IsServer) {
        int input = 0;
        boolean con = true;
        try {
            do {
                try {
                    input = Integer.parseInt(inputNet(IsServer), 10);
                    if (input >= min && input <= max) {
                        con = false;
                    } else {
                        server.print("��J���~ �Э��s��J����" + min + "��" + max + "����\n>");
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    server.print("��J���~ �Э��s��J����" + min + "��" + max + "����\n>");
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
    public static String Str5byte(boolean IsServer) throws IOException {
        StringBuilder str;
        while (true){
            str = new StringBuilder(inputNet(IsServer));
            byte[] bytes = str.toString().getBytes();
            if(bytes.length <= 5 && bytes.length > 0){
                for (int i = 0; i < 5 - bytes.length; i++){
                    str.append(" ");
                }
                break;
            }else if (bytes.length < 1){
                server.print("��J����r���� �Э��s��J\n>");
            }else {
                server.print("��J����r�j��5�ӭ^�� �Э��s��J\n>");
            }
        }
        return str.toString();
    }
    //���o��J
    public static String inputNet(boolean IsServer) throws IOException {
        Scanner sc = new Scanner(System.in);
        String out;
        if(IsServer){
            out = sc.next();
            server.sendln(out);
        }else {
            server.needReceive();
            out = server.receive();
            System.out.println(out);
        }
        return out;
    }
    //��ENTER�~��
    public static String pressEnter(boolean IsServer) throws IOException {
        Scanner sc = new Scanner(System.in);
        String ck;
        if(IsServer){
            ck = sc.nextLine();
        }else {
            server.pressEnter();
            ck = server.receive();
        }
        return ck;
    }
}
