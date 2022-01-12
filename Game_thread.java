import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;

class Game_thread implements Runnable{
    private final player player1, player2;
    private final Server server1, server2;
    private final ArrayList<player> playerlist;
    //組合名稱
    final String[] Categories_Map_ZHT = {"  一點  ","  二點  ","  三點  ","  四點  ","  五點  ","  六點  ","  小計  ","獎勵 +35","  全選  ","四骰同花","  葫蘆  ","  小順  ","  大順  ","  快艇  "," 總積分 "} ;
    //陣列轉換用Map
    final int[] DataToShow = {1,2,3,4,5,6,-1,-1,0,7,8,9,10,11,-1}, ShowToYacht = {1,2,3,4,5,6,0,7,8,9,10,11}
            , YachtToScore = {8,0,1,2,3,4,5,9,10,11,12,13};
    //分數表
    int[][] score = new int[2][15];
    //玩家名稱
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
            //現在是否為伺服端的回合,遊戲是否繼續
            boolean Sturn = true, continued = true;
            player_sf = player1.name();
            player_s = player_sf.trim();
            player_cf = player2.name();
            player_c = player_cf.trim();
            //主程式迴圈
            while (continued) {
                s_print("1.由" + player_s + "開始 2.由" + player_c + "開始 3.隨機\n" + player_s + ">");
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
                //分數表初始化
                for (int i = 0; i < score.length; i++) {
                    for (int j = 0; j < score[i].length; j++) {
                        if (j == 6 || j == 14) {
                            score[i][j] = 0;
                        } else {
                            score[i][j] = -1;
                        }
                    }
                }
                s_println("由" + (Sturn ? player_s : player_c) + "開始");
                s_print(player_s + "按下Enter繼續");
                co = pressEnter(true);
                //遊戲回合
                for (int turn = 2; turn < 26; turn++) {
                    showTable((Sturn ? player_s : player_c) + "的回合,按下Enter開始骰\n" + (Sturn ? player_s : player_c) + ">", turn / 2);
                    co = pressEnter(Sturn);
                    int[][] dice = new int[2][5];
                    for (int i = 0; i < 5; i++) {
                        dice[0][i] = 0;
                        dice[1][i] = 1;
                    }
                    boolean check = false;
                    //骰骰子
                    for (int TryTimes = 0; TryTimes < 3; TryTimes++) {
                        if (check) {
                            break;
                        }
                        //骰骰子
                        for (int i = 0; i < 5; i++) {
                            if (dice[1][i] != 0) {
                                dice[1][i] = random(1, 6);
                            }
                        }
                        //選骰與選擇組合
                        boolean rechose = true;
                        do {
                            //選骰
                            if (TryTimes < 2) {
                                while (true) {
                                    showTableDice(Yacht(dice), turn / 2);
                                    showDice(dice);
                                    s_print("輸入想交換的骰子編號,輸入\"0\"進入組合選擇或重骰\n" + (Sturn ? player_s : player_c) + "(" + (TryTimes + 1) + "/3)>");
                                    int ch = InputNumb(0, 5, Sturn,25565);
                                    if (ch == 0) {
                                        break;
                                    }else if(ch == 25565){
                                        s_print("測試模式(ID.25565): 回合數直接加至12,結束遊戲回合");
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
                            //選擇組合
                            int[] yacht = Yacht(dice);
                            showTableDice(yacht, turn / 2);
                            showDice(dice);
                            s_print("輸入想選擇的組合編號" + (TryTimes < 2 ? ",輸入\"0\"重骰,輸入\"13\"回到骰子選擇\n" : "\n") + (Sturn ? player_s : player_c) + "(" + (TryTimes + 1) + "/3)>");
                            //不與已選過重複
                            while (true) {
                                int ch = TryTimes < 2 ? InputNumb(0, 13, Sturn,0) : InputNumb(1, 12, Sturn,0);
                                if (ch == 0) {
                                    //重骰
                                    rechose = false;
                                    break;
                                } else if (ch != 13) {
                                    //將辨識結果存至分數表 並計算分數
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
                                        s_println("輸入的編號已經有填過了,請重新輸入\n" + (Sturn ? player_s : player_c) + "(" + (TryTimes + 1) + "/3)>");
                                    }
                                } else {
                                    break;
                                }
                            }
                        } while (rechose);
                    }
                    System.out.print(turn + ", ");
                }
                //遊戲結束
                showTable("遊戲結束,", 12);
                if (score[0][14] > score[1][14]) {
                    s_println(player_s + "勝利");
                } else if (score[0][14] < score[1][14]) {
                    s_println(player_c + "勝利");
                } else {
                    s_println("平手");
                }
                //決定是否繼續
                s_print("輸入\"0\"結束 輸入\"1\"繼續\n" + player_s + ">");
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
    //解除暫停
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
    //顯示骰子點數
    public void showDice(int[][] dice) throws IOException {
        s_println("+----+-1-+-2-+-3-+-4-+-5-+");
        for(int i = 0; i < 2; i++){
            s_print(i == 0 ? "|保留|" : "|骰出|");
            for (int j = 0; j < 5; j++){
                s_print(" " + (dice[i][j] != 0 ? dice[i][j] : " ") + " |");
            }
            s_print("\n");
        }
        s_println("+----+---+---+---+---+---+");
    }
    //顯示分數表
    public void showTable(String input,int turn) throws IOException {
        cls();
        s_println("回合:" + turn + "/12");
        String leftAlignFormat = "| %s | %-5s | %-5s |%n", subFormat = "| %s | %-2d/63 | %-2d/63 |%n", Separate = "+----------+-------+-------+\n";
        s_print(Separate);
        s_print(String.format("| %s | %s | %s |%n", "組合名稱" , player_sf, player_cf));
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
    //顯示有辨識結果的分數表
    public void showTableDice(int[] yacht,int turn) throws IOException {
        cls();
        s_println("回合:" + turn + "/12");
        String Format = "| %s | %-5s | %-5s |%n", LFormat = "| %s | %-5s | %-5s |%02d| %-5s |%n"
                , subFormat = "| %s | %-2d/63 | %-2d/63 |%n", Separate = "+----------+-------+-------+--+-------+\n";
        s_print(Separate);
        s_print(String.format("| %s | %s | %s |  | %s |%n", "組合名稱" , player_sf, player_cf, "分數 "));
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
    //隨機產生器
    public Integer random(int min, int max) {
        return (int)(Math.floor(Math.random() * (max - min + 1)) + min);
    }
    //清空版面
    public void cls() {
        try {
            server1.cls();
            server2.cls();
        }catch (IOException e){
            System.out.println("CLS failed!");
        }
    }
    //辨識骰子組合
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
    //排序
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
    //輸入指定範圍數字
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
                        s_print("輸入錯誤 請重新輸入介於" + min + "到" + max + "的數\n>");
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    s_print("輸入錯誤 請重新輸入介於" + min + "到" + max + "的數\n>");
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
    //取得輸入
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
    //按ENTER繼續
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

      
