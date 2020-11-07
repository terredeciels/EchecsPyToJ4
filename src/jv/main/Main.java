package jv.main;

import jv.Board;
import jv.Engine;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Board b = new Board();
        Engine e = new Engine();

        while (true) {
            b.renderBoard();
            Scanner obj = new Scanner(System.in);
            System.out.println("> ");
            String c = obj.nextLine();

            if (c.equals("quit") || c.equals("exit"))
                System.exit(0);

            else if (c.equals("undomove"))
                e.undomove(b);

            else if ("perft ".contains(c)) {
                int depth = 4;
                e.perft(depth, b);
            } else if (c.equals("legalmoves"))
                e.legalmoves(b);

            else
                // coup à jouer ? ex : e2e4
                e.usermove(b, c, "", "");
        }
    }


}
