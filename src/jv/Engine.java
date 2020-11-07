package jv;

import jv.move.Move;

import java.util.ArrayList;

public class Engine {

    public static double INFINITY = 32000;

    public int nodes;
    public int init_depth;
    public Move[][] pv;
    public boolean endgame;
    private int[] pv_length;
    private int MAX_PLY;

    public Engine() {
        endgame = false;
        init_depth = 4; // search in fixed depth
        nodes = 0; // number of nodes
        clear_pv();
        MAX_PLY = 32;
        //pv_length = [0 for x in range(self.MAX_PLY)];
        pv_length = new int[MAX_PLY];
        pv = new Move[MAX_PLY][MAX_PLY];
        for (int x = 0; x < MAX_PLY; x++) pv_length[x] = 0;


    }

    public void legalmoves(Board b) {

        //  "Show legal moves for side to move"

        ArrayList<Move> mList = b.gen_moves_list("", false);

        int cpt = 1;
        for (Move m : mList) {
            if (!b.domove(m.pos1, m.pos2, m.s)) continue;
            System.out.println("move #" + cpt + ":" + b.caseInt2Str(m.pos1) + b.caseInt2Str(m.pos2) + m.s);
            b.undomove();
            cpt += 1;
        }
    }

    public void perft(int depth, Board b) {

        //time1 = get_ms();
        for (int i = 2; i <= depth + 1; i++) {
            int total = perftoption(0, i - 1, b);
            //System.out.print("{}\t{}".format(i, total));
            System.out.println("depth " + i + ":" + total);
        }
        // time2 = =get_ms();
        // timeDiff = round((time2 - time1) / 1000, 2)
        // print('Done in', timeDiff, 's')
    }

    private int perftoption(int prof, int limit, Board b) {
        int cpt = 0;
        if (prof > limit) return 0;
        ArrayList<Move> L = b.gen_moves_list("", false);
        for (Move m : L) {
            if (!b.domove(m.pos1, m.pos2, m.s))
                continue;
            cpt += perftoption(prof + 1, limit, b);
            if (limit == prof)
                cpt += 1;
            b.undomove();
        }
        return cpt;
    }

    public void undomove(Board b) {
        b.undomove();
        endgame = false;
    }

    public void usermove(Board b, String c, String depart, String arrivee) {

        if (endgame) {
            print_result(b);
            return;
        }

        // Testing the command 'c'. Exit if incorrect.
        String chk = chkCmd(c);
        if (!chk.equals("")) {
            System.out.print(chk);
            return;
        }
        // Convert cases names to int, ex : e3 -> 44
        int pos1 = b.caseStr2Int(c.charAt(0) + Character.toString(c.charAt(1)));
        int pos2 = b.caseStr2Int(c.charAt(2) + Character.toString(c.charAt(3)));

        // Promotion asked ?
        String promote = "";
        if (c.length() > 4) {
            promote = Character.toString(c.charAt(4));
            switch (promote) {
                case "q":
                    promote = "q";
                    break;
                case "r":
                    promote = "r";
                    break;
                case "n":
                    promote = "n";
                    break;
                case "b":
                    promote = "b";
                    break;
            }
        }

        ArrayList<Move> mList = b.gen_moves_list("", false);

        // The move is not in list ? or let the king in check ?
        Move m = new Move(pos1, pos2, promote);
        //boolean b1 = !mList.contains(m);
        boolean b1 = false;
        for (Move mv : mList) {
            if (mv.pos1 == m.pos1 && mv.pos2 == m.pos2 && mv.s.equals(m.s)) {
                b1 = true;
                break;
            }
        }
        boolean b2 = !b.domove(pos1, pos2, promote);
        if (!b1 || b2) {
            System.out.print("\n" + c + " : incorrect move or let king in check" + "\n");
            return;
        }
        // Display the chess board
        b.renderBoard();

        // Check if game is over
        print_result(b);

        // Let the engine play
        search(b);

    }

    public void print_result(Board b) {

        // Is there at least one legal move left ?
        boolean f = false;

        for (Move m : b.gen_moves_list("", false)) {
            if (b.domove(m.pos1, m.pos2, m.s)) {
                b.undomove();
                f = true;  //yes, a move can be done
                break;
            }
        }
        // No legal move left, print result
        if (!f) {
            if (b.in_check(b.side2move)) {
                if (b.side2move.equals("blanc"))
                    System.out.print("0-1 {Black mates}");
                else
                    System.out.print("1-0 {White mates}");
            } else {
                System.out.print("1/2-1/2 {Stalemate}");
            }
            endgame = true;

        }
//        # TODO
//        # 3 reps
//        # 50 moves rule
    }

    private String chkCmd(String c) {

        String[] err = {
                "The move must be 4 or 5 letters : e2e4, b1c3, e7e8q...", "Incorrect move."};

        String letters = "abcdefgh";
        String numbers = "12345678";

        if (c.length() < 4 || c.length() > 5)
            return err[0];

        if (!letters.contains(Character.toString(c.charAt(0))))
            return err[1];

        if (!numbers.contains(Character.toString(c.charAt(1))))
            return err[1];

        if (!letters.contains(Character.toString(c.charAt(2))))
            return err[1];

        if (!numbers.contains(Character.toString(c.charAt(3))))
            return err[1];

        return "";

    }

    public void clear_pv() {
        for (int x = 0; x < MAX_PLY; x++)
            for (int y = 0; y < MAX_PLY; y++)
                pv[x][y] = null;
    }


    private void search(Board b) {

        if (endgame) {
            print_result(b);
            return;
        }
        // TODO
        // search in opening book

        clear_pv();
        nodes = 0;
        b.ply = 0;

        for (int i = 1; i < init_depth + 1; i++) {

            double score = alphabeta(i, -INFINITY, INFINITY, b);
            System.out.print(i + "  " + nodes + "  " + score / 10 + "  ");
            // print PV informations : ply, nodes...
            int j = 0;
            while (pv[j][j] != null) {
                Move c = pv[j][j];
                String pos1 = b.caseInt2Str(c.pos1);
                String pos2 = b.caseInt2Str(c.pos2);
                System.out.print(pos1 + "" + pos2 + c.s + " ");
                j += 1;
            }

            System.out.println();
            // Break if MAT is found
            if (score > INFINITY - 100 || score < -INFINITY + 100)
                break;
        }
        // root best move found, do it, and print result
        Move best = pv[0][0];
        b.domove(best.pos1, best.pos2, best.s);
        print_result(b);
    }

    public double alphabeta(int depth, double alpha, double beta, Board b) {

        // We arrived at the end of the search : return the board score
        if (depth == 0)
            return b.evaluer();
        // TODO : return quiesce(alpha,beta)

        nodes += 1;
        pv_length[b.ply] = b.ply;

        // Do not go too deep
        if (b.ply >= MAX_PLY - 1)
            return b.evaluer();

        // Extensions
        // If king is in check, let's go deeper
        boolean chk = b.in_check(b.side2move);// 'chk' used at the end of func too
        if (chk)
            depth += 1;

        // TODO
        // sort moves : captures first

        // Generate all moves for the side to move. Those who
        // let king in check will be processed in domove()
        ArrayList<Move> mList = b.gen_moves_list("", false);

        boolean f = false;  // flag to know if at least one move will be done
        //for i, m in enumerate(mList)
        for (Move m : mList) {
            if (!b.domove(m.pos1, m.pos2, m.s))
                continue;
            f = true;  // a move has passed
            double score = -alphabeta(depth - 1, -beta, -alpha, b);

            b.undomove();

            if (score > alpha) {

                // TODO
                // this move caused a cutoff,
                // should be ordered higher for the next search

                if (score >= beta)
                    return beta;
                alpha = score;

                // Updating the triangular PV-Table
                pv[b.ply][b.ply] = m;
                int j = b.ply + 1;
                while (j < pv_length[b.ply + 1]) {
                    pv[b.ply][j] = pv[b.ply + 1][j];
                    pv_length[b.ply] = pv_length[b.ply + 1];
                    j += 1;
                }
            }
        }
        // If no move has been done : it is DRAW or MAT
        if (!f) {
            if (chk)
                return -INFINITY + b.ply;  //MAT
            else
                return 0; //DRAW
        }
        // TODO
        // 50 moves rule

        return alpha;
    }

}
