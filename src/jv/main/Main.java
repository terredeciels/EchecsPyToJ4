package jv.main;
/*

Version python to java OK_v1

 */

import jv.Board;
import jv.Engine;

import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        Board b = new Board();
        Engine e = new Engine();
        // engine.legalmoves(board);

        // python
//        1 20
//        2	400
//        3	8902
//        4	197281
//        5	4865609
        // java
//        depth 2:400
//        depth 3:8902
//        depth 4:197281
//        depth 5:4865609

        // engine.perft(3, board);

        while (true) {

            b.renderBoard();

            Scanner obj = new Scanner(System.in);
            //String  c = input('>>> ');
            System.out.println("> ");
            String c = obj.nextLine();

            if (c.equals("quit") || c.equals("exit"))
                System.exit(0);

            else if (c.equals("undomove"))
                e.undomove(b);

//            else if ("setboard".contains(c))
//                e.setboard(b, c);

//            else if (c.equals("getboard"))
//                e.getboard(b);

//            else if (c.equals("go"))
//                e.search(b);

//            else if (c.equals("new"))
//                e.newgame(b);
//
//            else if (c.equals("bench"))
//                e.bench(b);
//
//            else if ("sd ".contains(c))
//                e.setDepth(c);

            else if ("perft ".contains(c)) {
                int depth = 4;
                e.perft(depth, b);
            }
            // e.perft(3, b);

            else if (c.equals("legalmoves"))
                e.legalmoves(b);

            else
                // coup à jouer ? ex : e2e4
                e.usermove(b, c, "", "");
        }
    }


}
