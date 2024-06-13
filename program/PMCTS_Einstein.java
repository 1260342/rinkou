import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PMCTS_Einstein {
    private List<Integer> list;// 盤面シャッフル用
    private List<Integer> player1 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6));// 先手の残りの駒
    private List<Integer> player2 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6));// 後手の残りの駒
    private int[][] Board = new int[5][5];// player2の駒の数字は7~12で管理していることに注意!!!!!!!!!!!!!!!!!!!!!!!!
    private int diceroll;// サイコロの出目
    private int turn;// 誰のターンか
    private boolean move1, move2, move3;// 3方向それぞれ動かせるかどうか
    private int move_piece;// 動かす駒
    private Scanner scan = new Scanner(System.in);

    public PMCTS_Einstein() {// コンストラクタ
        init();// 初期化
        letsPlay();// ゲームスタート
    }

    public void letsPlay() {// ゲームをはじめる
        while (!isGameEnd())
            play();// ゲームの終了条件を満たしているか
    }

    public void play() {// ゲームの手順
        showBoard();// 盤面表示
        rollDice();// サイコロを振る
        if (turn == 2) {
            selectMoveForPlayer2(); // PMCTSを用いてplayer2の手を選択
        } else {
            decidePiece(); // 動かす駒を決定
            movePiece();// 駒を動かす
        }
        changeTurn();// 先後交代
    }

    public boolean isGameEnd() {// ゲームの終了条件を満たしているかチェック
        boolean player1_win = false, player2_win = false;
        if ((Board[4][4] != 0 && Board[4][4] <= 6) || player2.size() == 0)
            player1_win = true;// 先手の勝利条件確認
        if (Board[0][0] > 6 || player1.size() == 0)
            player2_win = true;// 後手の勝利条件確認
        if (player1_win)
            System.out.println("先手が勝ちました");
        if (player2_win)
            System.out.println("後手が勝ちました");
        System.out.print("\u001b[m");
        return player1_win | player2_win;// どちらかが勝利したら終了
    }

    public void movePiece() {// 駒を動かす
        checkMoving();// 3方向について移動できるかチェック
        decideDirection();// 移動する方向を選ぶようにアナウンス
        move();// 入力から移動先を決定して駒を動かす
    }

    public void move() {// 入力から移動先を決定して駒を動かす
        int now_row = 0, now_col = 0;// 移動先の行と列
        int dir = scan.nextInt();// 動かす方向を入力
        while (((!move1 && dir == 1) || (!move2 && dir == 2) || (!move3 && dir == 3))
                || (dir != 1 && dir != 2 && dir != 3))
            dir = scan.nextInt();// 指定した方向が入力されなかった場合もう一度入力してもらう
        if (turn == 1) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (move_piece == Board[i][j]) {// move_pieceは移動させる駒の数字
                        Board[i][j] = 0;
                        now_row = i;
                        now_col = j;
                    }
                }
            }
            if (dir == 1)
                changeBoard(now_row + 1, now_col);// 下に移動
            else if (dir == 2)
                changeBoard(now_row + 1, now_col + 1);// 右下に移動
            else if (dir == 3)
                changeBoard(now_row, now_col + 1);// 右に移動
        } else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (move_piece + 6 == Board[i][j]) {// Board内のplayer2の駒は7~12の数字で管理していることに注意、move_pieceは1~6で管理されている
                        Board[i][j] = 0;
                        now_row = i;
                        now_col = j;
                    }
                }
            }
            if (dir == 1)
                changeBoard(now_row, now_col - 1);// 左に移動
            else if (dir == 2)
                changeBoard(now_row - 1, now_col - 1);// 左上に移動
            else if (dir == 3)
                changeBoard(now_row - 1, now_col);// 上に移動
        }
    }

    public void changeBoard(int row, int col) {// move()に呼び出されて実際に移動を行う
        if (turn == 1) {
            if (Board[row][col] == 0)
                Board[row][col] = move_piece;
            else if (Board[row][col] <= 6) {
                player1.remove(player1.indexOf(Board[row][col]));// 移動先にいたplayer1の駒を削除
                Board[row][col] = move_piece;
            } else {
                player2.remove(player2.indexOf(Board[row][col] - 6));// 移動先にいたplayer2の駒を削除
                Board[row][col] = move_piece;
            }
        } else {
            if (Board[row][col] == 0)
                Board[row][col] = move_piece + 6;
            else if (Board[row][col] <= 6) {
                player1.remove(player1.indexOf(Board[row][col]));
                Board[row][col] = move_piece + 6;
            } else {
                player2.remove(player2.indexOf(Board[row][col] - 6));
                Board[row][col] = move_piece + 6;
            }
        }
    }

    public void decideDirection() {// 移動する方向を決めてもらうようにアナウンスする
        fontColor();
        System.out.println("動かす方向を選んでください");
        if (turn == 1) {
            if (move1)
                System.out.print("下:1 ");
            if (move2)
                System.out.print("右下:2 ");
            if (move3)
                System.out.print("右:3 ");
        } else {
            if (move1)
                System.out.print("左:1 ");
            if (move2)
                System.out.print("左上:2 ");
            if (move3)
                System.out.print("上:3 ");
        }
        System.out.println();
    }

    public void checkMoving() {// 移動できるかチェックする
        int now_row = 0, now_col = 0;
        if (turn == 1) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (move_piece == Board[i][j]) {
                        now_row = i;
                        now_col = j;
                    }
                }
            }
            if (now_row + 1 < 5)
                move1 = true;// 下に進めるか
            else
                move1 = false;
            if (now_col + 1 < 5)
                move3 = true;// 右に進めるか
            else
                move3 = false;
            if (move1 && move3)
                move2 = true;// 右下に進めるか
            else
                move2 = false;
        } else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (move_piece + 6 == Board[i][j]) {
                        now_row = i;
                        now_col = j;
                    }
                }
            }
            if (now_col > 0)
                move1 = true;// 左に進めるか
            else
                move1 = false;
            if (now_row > 0)
                move3 = true;// 上に進めるか
            else
                move3 = false;
            if (move1 && move3)
                move2 = true;// 左上に進めるか
            else
                move2 = false;
        }
    }

    public void decidePiece() {// 動かす駒を決定
        // サイコロの値と今ある駒との差分をとり、最小の差分を求める
        // 最小の差分となった駒をpiece_candidateに格納
        int min_dis = 100;
        ArrayList<Integer> piece_candidate = new ArrayList<>();// 駒の候補を保持
        if (turn == 1) {
            for (int i = 0; i < player1.size(); i++) {
                int dis = Math.abs(player1.get(i) - diceroll);
                min_dis = Math.min(min_dis, dis);
            }
            for (int i = 0; i < player1.size(); i++) {
                int dis = Math.abs(player1.get(i) - diceroll);
                if (dis == min_dis)
                    piece_candidate.add(player1.get(i));
            }
        } else {
            for (int i = 0; i < player2.size(); i++) {
                int dis = Math.abs(player2.get(i) - diceroll);
                min_dis = Math.min(min_dis, dis);
            }
            for (int i = 0; i < player2.size(); i++) {
                int dis = Math.abs(player2.get(i) - diceroll);
                if (dis == min_dis)
                    piece_candidate.add(player2.get(i));
            }
        }
        pieceAnnounce(piece_candidate);
    }

    public void pieceAnnounce(ArrayList<Integer> piece_candidate) {// 動かすコマのうちどちらを動かすか選択するようにアナウンス
        fontColor();
        if (piece_candidate.size() == 1) {
            move_piece = piece_candidate.get(0);
            System.out.println(piece_candidate.get(0) + "を動かしてください");
        } else {
            for (int i = 0; i < piece_candidate.size(); i++) {
                if (i == 0)
                    System.out.print(piece_candidate.get(i));
                else
                    System.out.print("，" + piece_candidate.get(i));
            }
            System.out.println("のどちらを動かすか選んでください");
            choicePiece(piece_candidate);
        }
    }

    public void choicePiece(ArrayList<Integer> piece_candidate) {// 動かす駒を入力してもらう
        int piece_num = scan.nextInt();
        if (piece_candidate.contains(piece_num))
            move_piece = piece_num;
        else
            pieceAnnounce(piece_candidate);
    }

    public void changeTurn() {// 先後交代
        if (turn == 2)
            turn = 1;
        else
            turn = 2;
    }

    public void fontColor() {// 文字の色を変える
        if (turn == 1) {
            System.out.print("\u001b[31m");
        } else {
            System.out.print("\u001b[36m");
        }
    }

    public void rollDice() {// さいころ振る
        Random rand = new Random();
        diceroll = rand.nextInt(6) + 1;
        fontColor();
        System.out.println(diceroll + "が出ました");
    }

    public void showBoard() {// 盤面表示
        System.out.print("\u001b[m");
        System.out.println("#########");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (Board[i][j] == 0) {
                    System.out.print("- ");
                } else if (Board[i][j] <= 6) {
                    System.out.print("\u001b[31m");
                    System.out.print(Board[i][j] + " ");
                    System.out.print("\u001b[m");
                } else {
                    System.out.print("\u001b[36m");
                    System.out.print(Board[i][j] - 6 + " ");
                    System.out.print("\u001b[m");
                }
            }
            System.out.println();
        }
        System.out.println("#########");
    }

    public void init() {// 初期化
        initTurn();
        initBoard();
    }

    public void initTurn() {// ターンの初期化
        turn = 1;
    }

    public void initBoard() {// 盤面の初期化
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                Board[i][j] = 0;
        }
        list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        Collections.shuffle(list);
        int n = 6;
        Board[0][0] = list.get(0);
        Board[4][4] = list.get(0) + n;
        Board[0][1] = list.get(1);
        Board[4][3] = list.get(1) + n;
        Board[0][2] = list.get(2);
        Board[4][2] = list.get(2) + n;
        Board[1][0] = list.get(3);
        Board[3][4] = list.get(3) + n;
        Board[1][1] = list.get(4);
        Board[3][3] = list.get(4) + n;
        Board[2][0] = list.get(5);
        Board[2][4] = list.get(5) + n;
    }

////////// ここから追加 ///////////

    // PMCTSを用いてplayer2の手を選択するメソッド
    public void selectMoveForPlayer2() {
        int bestMove = -1;
        double bestWinRate = -1.0;

        // すべての可能な手を試す
        for (int move : player2) {
            double winRate = simulateMove(move);
            if (winRate > bestWinRate) {
                bestWinRate = winRate;
                bestMove = move;
            }
        }

        
        // 最良の手を選択
        move_piece = bestMove;
    }

    // 指定された手をシミュレートし、勝率を計算するメソッド
    private double simulateMove(int move) {
        int wins = 0;
        int simulations = 100; // シミュレーションの回数

        for (int i = 0; i < simulations; i++) {
            if (simulateGame(move)) {
                wins++;
            }
        }


        return (double) wins / simulations;
    }

    // 指定された手でゲームをシミュレートし、勝利したかどうかを返すメソッド
    private boolean simulateGame(int move) {
        // ゲームの状態を保存
        int[][] tempBoard = copyBoard(Board);
        List<Integer> tempPlayer1 = new ArrayList<>(player1);
        List<Integer> tempPlayer2 = new ArrayList<>(player2);
        int tempTurn = turn;

        // 指定された手を実行
        move_piece = move;
        simulateDecidePiece();

        // ランダムにゲームを進行
        while (!isGameEnd()) {
            rollDice();
            simulateDecidePiece();
            simulateMovePiece();
            changeTurn();
        }

        // ゲームの結果を取得
        boolean player2_win = Board[0][0] > 6 || player1.size() == 0;

        
        // ゲームの状態を元に戻す
        Board = tempBoard;
        player1 = tempPlayer1;
        player2 = tempPlayer2;
        turn = tempTurn;

        return player2_win;
    }

    public void simulateMovePiece() {
        checkMoving(); // 3方向について移動できるかチェック
        int dir = decideDirectionRandomly(); // 移動する方向をランダムに決定
        move(dir); // ランダムに決定した方向に駒を動かす
    }

    private int decideDirectionRandomly() {
        List<Integer> possibleDirections = new ArrayList<>();
        if (turn == 1) {
            if (move1) possibleDirections.add(1); // 下
            if (move2) possibleDirections.add(2); // 右下
            if (move3) possibleDirections.add(3); // 右
        } else {
            if (move1) possibleDirections.add(1); // 左
            if (move2) possibleDirections.add(2); // 左上
            if (move3) possibleDirections.add(3); // 上
        }

        Random rand = new Random();
        return possibleDirections.get(rand.nextInt(possibleDirections.size()));
    }

    private void move(int dir) {
        int now_row = 0, now_col = 0;

        if (turn == 1) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (move_piece == Board[i][j]) {
                        Board[i][j] = 0;
                        now_row = i;
                        now_col = j;
                    }
                }
            }
            if (dir == 1) changeBoard(now_row + 1, now_col); // 下に移動
            else if (dir == 2) changeBoard(now_row + 1, now_col + 1); // 右下に移動
            else if (dir == 3) changeBoard(now_row, now_col + 1); // 右に移動
        } else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (move_piece + 6 == Board[i][j]) {
                        Board[i][j] = 0;
                        now_row = i;
                        now_col = j;
                    }
                }
            }
            if (dir == 1) changeBoard(now_row, now_col - 1); // 左に移動
            else if (dir == 2) changeBoard(now_row - 1, now_col - 1); // 左上に移動
            else if (dir == 3) changeBoard(now_row - 1, now_col); // 上に移動
        }
    }

    //動かす駒を決定
    public void simulateDecidePiece() {
        int min_dis = 100;
        ArrayList<Integer> piece_candidate = new ArrayList<>(); // 駒の候補を保持

        if (turn == 1) {
            for (int piece : player1) {
                int dis = Math.abs(piece - diceroll);
                min_dis = Math.min(min_dis, dis);
            }
            for (int piece : player1) {
                int dis = Math.abs(piece - diceroll);
                if (dis == min_dis) piece_candidate.add(piece);
            }
        } else {
            for (int piece : player2) {
                int dis = Math.abs(piece - diceroll);
                min_dis = Math.min(min_dis, dis);
            }
            for (int piece : player2) {
                int dis = Math.abs(piece - diceroll);
                if (dis == min_dis) piece_candidate.add(piece);
            }
        }

        // 駒の候補からランダムに選択
        Random rand = new Random();
        move_piece = piece_candidate.get(rand.nextInt(piece_candidate.size()));
    }

    // 盤面をコピーするメソッド
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = board[i].clone();
        }
        return newBoard;
    }

    public static void main(String[] args) {
        new PMCTS_Einstein();
    }
}