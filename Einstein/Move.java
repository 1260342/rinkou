import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Move {
    private boolean move1, move2, move3;//3方向それぞれ動かせるかどうか
    private int move_piece;//動かす駒
    private Scanner scan = new Scanner(System.in);
    private List<Integer> Directions = new ArrayList<>();

    public void move(Player first, Player second, Board b, int turn, int dir) {//入力から移動先を決定して駒を動かす
        int[][] board = b.getBoard();
        int now_row = 0, now_col = 0;//移動先の行と列
        // Random rand = new Random();
        // int dir = rand.nextInt(3)+1;
        if(dir < 0) {
            dir = DirectionRandomly(turn);
            // System.out.println(dir);
        }

        while (((!move1&&dir==1)||(!move2&&dir==2)||(!move3&&dir==3))||(dir!=1&&dir!=2&&dir!=3)) {
            System.out.println("move_pi" + move_piece);
            System.out.println("move1" + move1);
            System.out.println("move2" + move2);
            System.out.println("move3" + move3);
            dir = scan.nextInt();//指定した方向が入力されなかった場合もう一度入力してもらう
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(move_piece == board[i][j]) {//move_pieceは移動させる駒の数字
                    board[i][j] = 0;//移動元の位置は駒がなくなる
                    now_row = i;
                    now_col = j;
                }
            }
        }
        if (turn == 1) {
            if(dir==1)changeBoard(now_row+1, now_col, first, second, b, turn);//下に移動
            else if(dir==2)changeBoard(now_row+1, now_col+1, first, second, b, turn);//右下に移動
            else if(dir==3)changeBoard(now_row, now_col+1, first, second, b, turn);//右に移動
        } else {
            if(dir==1)changeBoard(now_row, now_col-1, second, first, b, turn);//左に移動
            else if(dir==2)changeBoard(now_row-1, now_col-1, second, first, b, turn);//左上に移動
            else if(dir==3)changeBoard(now_row-1, now_col, second, first, b, turn);//上に移動
        }
        
    }

    public void changeBoard(int row, int col, Player first, Player second, Board b, int turn) {//move()に呼び出されて実際に移動を行う
        int[][] board = b.getBoard();
        if (1 <= board[row][col] && board[row][col] <= 6) first.removePiece(board[row][col]);//移動先にいたplayer1の駒を削除
        if (board[row][col] > 6) second.removePiece(board[row][col]-6);//移動先にいたplayer2の駒を削除
        board[row][col] = move_piece;//移動
        b.setBoard(board);//移動後の盤面に書き換え
    }

    public void announceDirection(int turn) {
        System.out.println("動かす方向を選んでください");  
        if (turn == 1) {
            if(move1)System.out.print("下:1 ");
            if(move2)System.out.print("右下:2 ");
            if(move3)System.out.print("右:3 ");
        } else {
            if(move1)System.out.print("左:1 ");
            if(move2)System.out.print("左上:2 ");
            if(move3)System.out.print("上:3 ");
        }
        System.out.println();
    }

    public int selectDirection() {
        int dir = scan.nextInt();// 動かす方向を入力
        return dir;
    }

    public void checkMoving(int[][] board, int turn) {//移動できるかチェックする
        
        int now_row = 0, now_col = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (move_piece == board[i][j]) {
                    now_row=i;
                    now_col=j;
                }
            }
        }  

        if (turn == 1) {
            move1 = now_row+1 < 5;//下に進めるか
            move3 = now_col+1 < 5;//右に進めるか
            move2 = move1 && move3;//右下に進めるか
            if (move1) Directions.add(1);
            if (move2) Directions.add(2);
            if (move3) Directions.add(3);
        } else {    
            move1 = now_col > 0;//左に進めるか
            move3 = now_row > 0;//上に進めるか
            move2 = move1 && move3;//左上に進めるか
            if (move1) Directions.add(1);
            if (move2) Directions.add(2);
            if (move3) Directions.add(3);
        }
    }

    // 動かす方向をランダムに決定
    private int DirectionRandomly(int turn) {
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

    public int getMove_piece() {
        return move_piece;
    }

    public void setMove_piece(int move_piece) {//move_pieceのsetter
        this.move_piece = move_piece;
    }

    public List<Integer> getDirections() {
        return Directions;
    }

    public boolean[] getMoves() {
        boolean[] moves = {move1, move2, move3};
        return moves;
    }

}