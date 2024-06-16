import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PMCTS {
    private int diceroll; // サイコロの出目
    private int turn; // 誰のターンか
    private Board b; // 盤面
    private Player player1, player2;// 先手，後手のプレイヤー
    private Move m; // 駒の移動

    public PMCTS(Board board, Player player1, Player player2, int turn, int diceroll, Move move) {
        this.init(board, player1, player2, turn, diceroll, move);
    }

    public void init(Board board, Player p1, Player p2, int turn, int diceroll, Move move) {
        b = new Board();
        player1 = new Player();
        player2 = new Player();
        m = new Move();
        this.b = board;
        this.player1 = p1;
        this.player2 = p2;
        this.m = move;
        this.turn = turn;
        
    }

    // PMCTSを用いてplayer2の手を選択するメソッド
    public int selectMoveForPlayer2(Board board, Player player1, Player player2, int turn, int diceroll, Move m) {
        int bestMove = -1;
        double bestWinRate = -1.0;
        double[] winRates = {-1.0, -1.0, -1.0};

        m.checkMoving(b.getBoard(), turn);
        boolean[] moves = m.getMoves();
        System.out.println("左, 左上, 上");
        System.out.println(moves[0] + "," + moves[1] + "," + moves[2] + ",");

        if (moves[0]) {
            double winRate = simulateMove(1, board, player1, player2, turn, diceroll, m);
            winRates[0] = winRate;
            if (winRate > bestWinRate) {
                bestWinRate = winRate;
                bestMove = 1;
            }
        }
        if (moves[1]) {
            double winRate = simulateMove(2, board, player1, player2, turn, diceroll, m);
            winRates[1] = winRate;
            if (winRate > bestWinRate) {
                bestWinRate = winRate;
                bestMove = 2;
            }
        }
        if (moves[2]) {
            double winRate = simulateMove(3, board, player1, player2, turn, diceroll, m);
            winRates[2] = winRate;
            if (winRate > bestWinRate) {
                bestWinRate = winRate;
                bestMove = 3;
            }
        }

        System.out.println(winRates[0] + "," + winRates[1] + "," + winRates[2] + ",");
        // 最良の手を選択
        return bestMove;
    }

    // 指定された手をシミュレートし、勝率を計算するメソッド
    private double simulateMove(int dir, Board board, Player player1, Player player2, int turn, int diceroll, Move m) {
        int wins = 0; // 勝利数
        int simulations = 1000; // シミュレーションの回数

        for (int i = 0; i < simulations; i++) {
            // System.out.println("move" + dir);      
            if (simulateGame(dir, board, player1, player2, turn, diceroll, m)) {
                wins++;
            }
        }
        double winRate = (double) wins / simulations; // 勝率の計算

        return winRate;
    }

    
    
    // 指定された手でゲームをシミュレートし、勝利したかどうかを返すメソッド
    private boolean simulateGame(int dir, Board board, Player p1, Player p2, int turn, int diceroll, Move m) {
        // ゲームの状態を元に戻す
        int[][] tempBoard = copyBoard(board.getBoard());
        ArrayList<Integer> tempPlayer1 = new ArrayList<Integer>(p1.getPiece());
        ArrayList<Integer> tempPlayer2 = new ArrayList<Integer>(p2.getPiece());

        int tempTurn = turn;
        int tempMove_piece = m.getMove_piece();

        init(board, p1, p2, turn, diceroll, m); 
        this.player1.setPiece(p1.getPiece());
        this.player2.setPiece(p2.getPiece());
        // System.out.println("Restart");
        // System.out.println(player2.getPiece());
        // b.showBoard();// 盤面表示
        // System.out.println(p1.getPiece());
        // System.out.println(player1.getPiece());
        // System.out.println(p2.getPiece());
        // System.out.println(player2.getPiece());
        

        // 指定された手を実行
        m.checkMoving(b.getBoard(), this.turn);
        this.m.move(player2, player1, b, this.turn, dir);
        this.changeTurn();

        // ランダムにゲームを進行
        while (!isGameEnd()) {
            // b.showBoard();// 盤面表示
            // 動かす駒をランダムに決定
            rollDice();
            // simulateDecidePiece(player1, player2);
            if (this.turn == 1) {
                player1.decidePieceRandomly(this.m, diceroll, this.turn);
            } else {
                player2.decidePieceRandomly(this.m, diceroll, this.turn);
            }
            // System.out.println("movepiece" + m.getMove_piece());
            m.checkMoving(b.getBoard(), this.turn);
            // 駒を動かす
            if (this.turn == 1) {
                this.m.move(player1, player2, b, this.turn, -1);
            } else {
                this.m.move(player2, player1, b, this.turn, -1);
            }
            this.changeTurn();
        }

        // ゲームの結果を取得
        int[][] a = b.getBoard();
        // b.showBoard();
        boolean player2_win = a[0][0]>6 || player1.isEmpty();//後手の勝利条件確認

        // System.out.println("temp");
        // System.out.println(tempPlayer1);
        // System.out.println(tempPlayer2);
        // ゲームの状態を元に戻す
        board.setBoard(tempBoard);
        p1.setPiece(tempPlayer1);
        p2.setPiece(tempPlayer2);
        turn = tempTurn;
        m.setMove_piece(tempMove_piece);

        return player2_win;
    }
    

    // 動かす駒を決定
    public void simulateDecidePiece(Player player1, Player player2) {
        int min_dis = 100;
        ArrayList<Integer> piece_candidate = new ArrayList<>(); // 駒の候補を保持

        if (turn == 1) {
            for (int piece : player1.getPiece()) {
                int dis = Math.abs(piece - diceroll);
                min_dis = Math.min(min_dis, dis);
            }
            for (int piece : player1.getPiece()) {
                int dis = Math.abs(piece - diceroll);
                if (dis == min_dis) piece_candidate.add(piece);
            }
        } else {
            for (int piece : player2.getPiece()) {
                int dis = Math.abs(piece - diceroll);
                min_dis = Math.min(min_dis, dis);
            }
            for (int piece : player2.getPiece()) {
                int dis = Math.abs(piece - diceroll);
                if (dis == min_dis) piece_candidate.add(piece);
            }
        }

        System.out.println(piece_candidate);
        // 駒の候補からランダムに選択
        Random rand = new Random();
        int move_piece = piece_candidate.get(rand.nextInt(piece_candidate.size()));
        if (turn == 2) move_piece += 6;//後手の駒は7~12で管理しているため
        m.setMove_piece(move_piece);
    }


    public boolean isGameEnd() {//ゲームの終了条件を満たしているかチェック
        int[][] board = b.getBoard();
        boolean p1_win = (board[4][4]!=0 && board[4][4]<=6) || player2.isEmpty();//先手の勝利条件確認
        boolean p2_win = board[0][0]>6 || player1.isEmpty();//後手の勝利条件確認
        return p1_win|p2_win;//どちらかが勝利したら終了
    }

    public void changeTurn() {//先後交代
        this.turn = 3 - this.turn;
    }

    public void rollDice() {//さいころ振る
        Random rand = new Random();
        diceroll = rand.nextInt(6)+1;
    }

    // 盤面をコピーするメソッド
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = board[i].clone();
        }
        return newBoard;
    }
}
