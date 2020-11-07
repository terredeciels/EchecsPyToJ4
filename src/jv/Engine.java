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

//        """PERFformance Test :
//        This is a debugging function through the move generation tree
//        for the current board until depth [x].
//        'c' is the command line written by user : perft [x]
//        """

        // Checking the requested depth
        // cmd = c.split()
        // cmd[0]='perft'
//
//        try:
//            d = int(cmd[1])
//        except ValueError:
//            print('Please type an integer as depth i.e. : perft 5')
//            return

//        if (d < 1 or d > self.MAX_PLY):
//            print('Depth must be between 1 and', self.MAX_PLY)
//            return

        // System.out.print("Depth\tNodes\tCaptures\tE.p.\tCastles\tPromotions\tChecks\tCheckmates");

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
        // "The user requested a 'undomove' in command line"

        b.undomove();
        endgame = false;
    }

    public void usermove(Board b, String c, String depart, String arrivee) {

//
//        """Move a piece for the side to move, asked in command line.
//        The command 'c' in argument is like 'e2e4' or 'b7b8q'.
//        Argument 'b' is the chessboard.
//        """

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
        // Generate moves list to check
        // if the given move (pos1,pos2,promote) is correct
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

        //  "Check if the game is over and print the result"

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
//      """Check if the command 'c' typed by user is like a move,
//        i.e. 'e2e4','b7b8n'...
//        Returns '' if correct.
//        Returns a string error if not.
//        """

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
//
//    public void setboard(Board b, String c) {
////        """Set the chessboard to the FEN position given by user with
////        the command line 'setboard ...'.
////        'c' in argument is for example :
////        'setboard 8/5k2/5P2/8/8/5K2/8/8 w - - 0 0'
////        """
//
//        String[] cmd = c.split(" "); //  # split command with spaces
//        cmd.pop(0); //  # drop the word 'setboard' written by user
//
//
//
//        // set the FEN position on board
//        if (b.setboard(' '.join(cmd)))
//        endgame = false; //  # success, so no endgame
//    }

//    int get_ms() {
//        return  round(time.time() * 1000);
//    }


    public void clear_pv() {

        //    "Clear the triangular PV table containing best moves lines"
        // pv = [[0 for x in range(self.MAX_PLY)] for x in range(self.MAX_PLY)]

        for (int x = 0; x < MAX_PLY; x++)
            for (int y = 0; y < MAX_PLY; y++)
                pv[x][y] = null;
    }


    void search(Board b) {

//        """Search the best move for the side to move,
//        according to the given chessboard 'b'
//        """

        if (endgame) {
            print_result(b);
            return;
        }
        // TODO
        // search in opening book

        clear_pv();
        nodes = 0;
        b.ply = 0;

        // System.out.print("ply\tnodes\tscore\tpv");

        for (int i = 1; i < init_depth + 1; i++) {

            double score = alphabeta(i, -INFINITY, INFINITY, b);

            //print("{}\t{}\t{}\t".format(i, self.nodes, score / 10), end='')
            System.out.print(i + "  " + nodes + "  " + score / 10 + "  ");
            // print PV informations : ply, nodes...
            int j = 0;
            while (pv[j][j] != null) {
                Move c = pv[j][j];
                String pos1 = b.caseInt2Str(c.pos1);
                String pos2 = b.caseInt2Str(c.pos2);
                //print("{}{}{}".format(pos1, pos2, c[2]), end=' ')
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
            //Do the move 'm'.
            // If it lets king in check, undo it and ignore it
            // remind : a move is defined with (pos1,pos2,promote)
            // i.e. : 'e7e8q' is (12,4,'q')
            if (!b.domove(m.pos1, m.pos2, m.s))
                continue;

            f = true;  // a move has passed

            double score = -alphabeta(depth - 1, -beta, -alpha, b);

            // Unmake move
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
