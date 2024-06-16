import java.util.Random;
public class Einstein { 
    private int diceroll; // サイコロの出目
    private int turn; // 誰のターンか
    private Board b; // 盤面
    private Player p1, p2;// 先手，後手のプレイヤー
    private Move m; // 駒の移動
    private PMCTS pmcts; // シミュレーション用のインスタンス

    public Einstein() {//コンストラクタ
        init();//初期化
    }

    public void letsPlay() {//ゲームをはじめる
        while (!isGameEnd()) play();//ゲームの終了条件を満たしているか
    }

    public void play() {//ゲームの手順
        b.showBoard();// 盤面表示
        rollDice();// サイコロを振る
        decidePiece(); // 動かす駒を決定
        if (turn == 2) {
            int bestMove = pmcts.selectMoveForPlayer2(b, p1, p2, turn, diceroll, m); // PMCTSを用いてplayer2の手を選択
            m.checkMoving(b.getBoard(), turn);//3方向について移動できるかチェック
            m.move(p2, p1, b, turn, bestMove);
        } else {
            movePiece();// 駒を動かす
        }
        changeTurn();// 先後交代
    }

    public boolean isGameEnd() {//ゲームの終了条件を満たしているかチェック
        int[][] board = b.getBoard();
        boolean p1_win = (board[4][4]!=0 && board[4][4]<=6) || p2.isEmpty();//先手の勝利条件確認
        boolean p2_win = board[0][0]>6 || p1.isEmpty();//後手の勝利条件確認
        if (p1_win || p2_win) {
            b.showBoard();
            System.out.print(turn == 2 ? "\u001b[31m" : "\u001b[36m");//文字色変える（判定位置的にturnが書き換わったあとなので逆）
            System.out.println((p1_win ? "先" : "後") + "手が勝ちました");
            System.out.print("\u001b[m");
        }
        return p1_win|p2_win;//どちらかが勝利したら終了
    }

    public void movePiece() {//駒を動かす
        m.checkMoving(b.getBoard(), turn);//3方向について移動できるかチェック
        m.announceDirection(turn);//移動する方向を選ぶようにアナウンス
        int dir = m.selectDirection();//移動する方向を入力する
        this.move(dir);//駒を動かす
    }

    public void move(int dir){//駒を動かす
        if (turn == 1) {
            m.move(p1, p2, b, turn, dir);
        } else {
            m.move(p2, p1, b, turn, dir);
        }
    }

    public void decidePiece() {//動かす駒を決定
        if (turn == 1) {
            p1.decidePiece(m, diceroll, turn);
        } else {
            p2.decidePiece(m, diceroll, turn);
        }
    }

    public void changeTurn() {//先後交代
        turn = 3 - turn;
    }

    public void rollDice() {//さいころ振る
        Random rand = new Random();
        diceroll = rand.nextInt(6)+1;
        System.out.print(turn == 1 ? "\u001b[31m" : "\u001b[36m");//文字色変える
        System.out.println(diceroll + "が出ました");
    }
    
    public void init() {//初期化
        b = new Board();
        p1 = new Player();
        p2 = new Player();
        m = new Move();
        turn = 1;
        b.initBoard();
        pmcts = new PMCTS(b, p2, p1, turn, diceroll, m);
    }

    public static void main(String[] args) {
        Einstein e = new Einstein();
        e.letsPlay();
    }
}