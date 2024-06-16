import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Player {
    private ArrayList<Integer> piece_candidate;//駒の候補を保持
    private List<Integer> piece = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6));//残りの駒
    private Scanner scan = new Scanner(System.in);

    public void decidePiece(Move m, int diceroll, int turn) {//動かす駒を決定
        //サイコロの値と今ある駒との差分をとり、最小の差分を求める
        //最小の差分となった駒をpiece_candidateに格納
        piece_candidate = new ArrayList<Integer>();
        int min_dis = 100;
        int move_piece;
        for (int i = 0; i < piece.size(); i++) {
            int dis = Math.abs(piece.get(i)-diceroll);
            min_dis = Math.min(min_dis,dis);
        }
        for (int i = 0; i < piece.size(); i++) {
            int dis = Math.abs(piece.get(i)-diceroll);
            if (dis == min_dis) piece_candidate.add(piece.get(i));
        }
        //動かす駒を決定
        Random rand = new Random();
        if (piece_candidate.size() == 1) {
            move_piece = piece_candidate.get(0);
            System.out.println(piece_candidate.get(0) + "を動かしてください");  
        } else {
            System.out.print(piece_candidate.get(0) + "，" + piece_candidate.get(1));
            System.out.println("のどちらを動かすか選んでください");
            if(turn == 1) move_piece = choicePiece(piece_candidate);// 動かす方向を入力
            else move_piece = piece_candidate.get(rand.nextInt(2));//2つの中からランダムで選択
        }
        if (turn == 2) move_piece += 6;//後手の駒は7~12で管理しているため
        m.setMove_piece(move_piece);
    }

    // 動かす駒を入力してもらう
    public int choicePiece(ArrayList<Integer> piece_candidate) {
        int piece_num = scan.nextInt();
        int move_piece = 0;
        if (piece_candidate.contains(piece_num)) {
            move_piece = piece_num;
        } else {
            choicePiece(piece_candidate);
        }
        return move_piece;
    }

    // 動かす駒をランダムに決定する
    public void decidePieceRandomly(Move m, int diceroll, int turn) {//動かす駒を決定
        //サイコロの値と今ある駒との差分をとり、最小の差分を求める
        //最小の差分となった駒をpiece_candidateに格納
        piece_candidate = new ArrayList<Integer>();
        int min_dis = 100;
        int move_piece;
        // System.out.println(piece);
        for (int i = 0; i < piece.size(); i++) {
            int dis = Math.abs(piece.get(i)-diceroll);
            min_dis = Math.min(min_dis,dis);
        }
        for (int i = 0; i < piece.size(); i++) {
            int dis = Math.abs(piece.get(i)-diceroll);
            if (dis == min_dis) piece_candidate.add(piece.get(i));
        }
        //動かす駒を決定
        Random rand = new Random();
        if (piece_candidate.size() == 1) {
            move_piece = piece_candidate.get(0);
        } else {
            move_piece = piece_candidate.get(rand.nextInt(2));//2つの中からランダムで選択
        }
        if (turn == 2) move_piece += 6;//後手の駒は7~12で管理しているため
        m.setMove_piece(move_piece);
    }

    public void removePiece(int x) {//残りの駒の情報を変更
        piece.remove(piece.indexOf(x));
    }

    public boolean isEmpty() {//駒が1つもないか
        return piece.size() == 0;
    }

    public List<Integer> getPiece(){
        return piece;
    }

    public void setPiece(List<Integer> piece) {
        this.piece = piece;
    }
}
