import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board {
    private List<Integer> list;//盤面シャッフル用
    private int[][] Board = new int[5][5];//player2の駒の数字は7~12で管理していることに注意
    public void showBoard() {//盤面表示
        System.out.println("\u001b[m" + "#########");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(Board[i][j] == 0) {
                    System.out.print("\u001b[m" + "- ");
                } else if(Board[i][j] <= 6) {
                    System.out.print("\u001b[31m" + Board[i][j] + " ");
                } else {
                    System.out.print("\u001b[36m" + (Board[i][j]-6) + " ");
                }
            }
            System.out.println();
        }
        System.out.println("\u001b[m" + "#########");
    }

    public void initBoard() {//盤面の初期化
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Board[i][j] = 0;
            }
        }
        list = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
        Collections.shuffle(list);
        Board[0][0] = list.get(0);
        Board[4][4] = list.get(0)+6;
        Board[0][1] = list.get(1);
        Board[4][3] = list.get(1)+6;
        Board[0][2] = list.get(2);
        Board[4][2] = list.get(2)+6;
        Board[1][0] = list.get(3);
        Board[3][4] = list.get(3)+6;
        Board[1][1] = list.get(4);
        Board[3][3] = list.get(4)+6;
        Board[2][0] = list.get(5);
        Board[2][4] = list.get(5)+6;
    }

    public int[][] getBoard() {//盤面のgetter
        return Board;
    }

    public void setBoard(int[][] board) {//盤面のsetter
        this.Board = board;
    }
}
