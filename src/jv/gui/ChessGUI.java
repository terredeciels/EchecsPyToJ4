package jv.gui;


import jv.Board;
import jv.Engine;
import jv.move.Move;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;


public class ChessGUI implements ActionListener {

    private static final String COLS = "ABCDEFGH";
    private static final int QUEEN = 0, KING = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
    private static final int[] STARTING_ROW = {ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK};
    private static final int BLACK = 0, WHITE = 1;
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private final JLabel message = new JLabel("Chess Champ is ready to play!");
    private Board board;
    private Engine engine;
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private Image[][] chessPieceImages = new Image[2][6];
    private boolean first = true;
    private JButton depButton;
    private JButton arrButton;

    private ChessGUI() {
        board = new Board();
        engine = new Engine();
        initializeGui();
    }

    public static void main(String[] args) {

        Runnable r = () -> {
            ChessGUI cg = new ChessGUI();

            JFrame f = new JFrame("");
            f.add(cg.getGui());
            // Ensures JVM closes after frame(s) closed and
            // all non-daemon threads are finished
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // See https://stackoverflow.com/a/7143398/418556 for demo.
            f.setLocationByPlatform(true);

            // ensures the frame is the minimum size it needs to be
            // in order display the components within it
            f.pack();
            // ensures the minimum size is enforced.
            // f.setSize(600,600);
            f.setMinimumSize(f.getSize());
            f.setVisible(true);
        };
        // Swing GUIs should be created and updated on the EDT
        // http://docs.oracle.com/javase/tutorial/uiswing/concurrency
        SwingUtilities.invokeLater(r);
    }

    private void initializeGui() {
        // create the images for the chess pieces
        createImages();

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        Action newGameAction = new AbstractAction("New") {

            @Override
            public void actionPerformed(ActionEvent e) {
                setupNewGame();
            }
        };
        tools.add(newGameAction);
        tools.add(new JButton("Save")); // TODO - add functionality!
        tools.add(new JButton("Restore")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(new JButton("Resign")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(message);

        gui.add(new JLabel("?"), BorderLayout.LINE_START);
        /*
         * Override the preferred size to return the largest it can, in
         * a square shape.  Must (must, must) be added to a GridBagLayout
         * as the only component (it uses the parent as a guide to size)
         * with no GridBagConstaint (so it is centered).
         */
        // the smaller of the two sizes
        JPanel chessBoard = new JPanel(new GridLayout(0, 9)) {

            /**
             * Override the preferred size to return the largest it can, in
             * a square shape.  Must (must, must) be added to a GridBagLayout
             * as the only component (it uses the parent as a guide to size)
             * with no GridBagConstaint (so it is centered).
             */
            @Override
            public final Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension(
                            (int) d.getWidth(), (int) d.getHeight());
                } else if (c.getWidth() > d.getWidth() && c.getHeight() > d.getHeight()) {
                    prefSize = c.getSize();
                } else {
                    prefSize = d;
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                // the smaller of the two sizes
                int s = (Math.min(w, h));
                return new Dimension(s, s);
            }
        };
//        chessBoard.setBorder(new CompoundBorder(
//                new EmptyBorder(8, 8, 8, 8),
//                new LineBorder(Color.BLACK)
//        ));
//        // Set the BG to be ochre
//        Color ochre = new Color(204, 119, 34);
//        chessBoard.setBackground(ochre);
        JPanel boardConstrain = new JPanel(new GridBagLayout());
//        boardConstrain.setBackground(ochre);
        boardConstrain.add(chessBoard);
        gui.add(boardConstrain);

        // create the chess board squares
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 1)
                        //) {
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
                chessBoardSquares[jj][ii] = b;
            }
        }
        /*
         * fill the chess board
         */
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                            SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        int c = 0;
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                if (jj == 0) {
                    chessBoard.add(new JLabel("" + (9 - (ii + 1)),
                            SwingConstants.CENTER));
                }
                chessBoardSquares[jj][ii].setText(String.valueOf(c++));
                chessBoardSquares[jj][ii].addActionListener(this);
                chessBoard.add(chessBoardSquares[jj][ii]);

            }
        }

    }

    private JComponent getGui() {
        return gui;
    }

    private void createImages() {
        try {
            URL url = new URL("http://i.stack.imgur.com/memI0.png");
            BufferedImage bi = ImageIO.read(url);
            for (int ii = 0; ii < 2; ii++) {
                for (int jj = 0; jj < 6; jj++) {
                    chessPieceImages[ii][jj] = bi.getSubimage(
                            jj * 64, ii * 64, 64, 64);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initializes the icons of the initial chess board piece places
     */
    private void setupNewGame() {
        message.setText("Make your move!");
        // set up the black pieces
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][0].setIcon(new ImageIcon(
                    chessPieceImages[BLACK][STARTING_ROW[ii]]));
        }
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][1].setIcon(new ImageIcon(
                    chessPieceImages[BLACK][PAWN]));
        }
        // set up the white pieces
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][6].setIcon(new ImageIcon(
                    chessPieceImages[WHITE][PAWN]));
        }
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][7].setIcon(new ImageIcon(
                    chessPieceImages[WHITE][STARTING_ROW[ii]]));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
//        System.out.println("clic");
//        System.out.println(source.getX() + "," + source.getY());
//        System.out.println(source.getText());

        if (first) {
            first = false;
            //depart = source.getText();
            depButton = source;
        } else {
            //arrivee = source.getText();
            arrButton = source;
            first = true;
            usermove();

        }


    }

    private void usermove() {
        String depart = depButton.getText();
        String arrivee = arrButton.getText();
        System.out.println(depart + "," + arrivee);
        int pos1 = Integer.parseInt(depart);
        int pos2 = Integer.parseInt(arrivee);
        if (engine.endgame) {
            engine.print_result(board);
            return;
        }

        // Testing the command 'c'. Exit if incorrect.
//            String chk = chkCmd(c);
//            if (!chk.equals("")) {
//                System.out.print(chk);
//                return;
//            }
        // Convert cases names to int, ex : e3 -> 44
//            int pos1 = b.caseStr2Int(c.charAt(0) + Character.toString(c.charAt(1)));
//            int pos2 = b.caseStr2Int(c.charAt(2) + Character.toString(c.charAt(3)));

        // Promotion asked ?
        //TODO promote gui
//            String promote = "";
//            if (c.length() > 4) {
//                promote = Character.toString(c.charAt(4));
//                switch (promote) {
//                    case "q":
//                        promote = "q";
//                        break;
//                    case "r":
//                        promote = "r";
//                        break;
//                    case "n":
//                        promote = "n";
//                        break;
//                    case "b":
//                        promote = "b";
//                        break;
//                }
//            }
        // Generate moves list to check
        // if the given move (pos1,pos2,promote) is correct
        ArrayList<Move> mList = board.gen_moves_list("", false);

        // The move is not in list ? or let the king in check ?
        //TODO promote gui
        // Move m = new Move(pos1, pos2, promote);
        Move m = new Move(pos1, pos2, "");
        //boolean b1 = !mList.contains(m);
        boolean b1 = false;
        for (Move mv : mList) {
            if (mv.pos1 == m.pos1 && mv.pos2 == m.pos2 && mv.s.equals(m.s)) {
                b1 = true;
                break;
            }
        }
        //TODO promote gui
        //boolean b2 = !b.domove(pos1, pos2, promote);
        boolean b2 = !board.domove(pos1, pos2, "");
        if (!b1 || b2) {
            // System.out.print("\n" + c + " : incorrect move or let king in check" + "\n");
            System.out.println("incorrect move or let king in check");
            return;
        }
        // Display the chess board
        board.render();

        // Check if game is over
        engine.print_result(board);

        // Let the engine play
        search(board);
    }

    private void search(Board board) {
        //        """Search the best move for the side to move,
//        according to the given chessboard 'b'
//        """

        if (engine.endgame) {
            engine.print_result(board);
            return;
        }
        // TODO
        // search in opening book

        engine.clear_pv();
        engine.nodes = 0;
        board.ply = 0;

        // System.out.print("ply\tnodes\tscore\tpv");

        for (int i = 1; i < engine.init_depth + 1; i++) {

            double score = engine.alphabeta(i, -Engine.INFINITY, Engine.INFINITY, board);

            //print("{}\t{}\t{}\t".format(i, self.nodes, score / 10), end='')
            System.out.print(i + "  " + engine.nodes + "  " + score / 10 + "  ");
            // print PV informations : ply, nodes...
            int j = 0;
            while (engine.pv[j][j] != null) {
                Move c = engine.pv[j][j];
                String pos1 = board.caseInt2Str(c.pos1);
                String pos2 = board.caseInt2Str(c.pos2);
                //print("{}{}{}".format(pos1, pos2, c[2]), end=' ')
                System.out.print(pos1 + "" + pos2 + c.s + " ");
                j += 1;
            }

            System.out.println();

            // Break if MAT is found
            if (score > Engine.INFINITY - 100 || score < -Engine.INFINITY + 100)
                break;
        }
        // root best move found, do it, and print result
        Move best = engine.pv[0][0];

        domove();
        board.domove(best.pos1, best.pos2, best.s);

        engine.print_result(board);
    }

    private void domove() {

//        """Move a piece on the board from the square numbers
//        "depart" to "arrivee" (0..63) respecting rules :
//        - prise en passant
//        - promote and under-promote
//        - castle rights
//        Returns :
//        - TRUE if the move do not let king in check
//        - FALSE otherwise and undomove is done.
//        """

//        # Debugging tests
//        #if(self.cases[depart].isEmpty()):
//        #    print("domove() ERROR : asked for an empty square move : ",depart,arrivee,promote)
//        #    return
//        #if(int(depart)<0 or int(depart)>63):
//        #    print("domove() ERROR : incorrect FROM square number : ",depart)
//        #    return
//        #if(int(arrivee)<0 or int(arrivee)>63):
//        #    print("domove() ERROR : incorrect TO square number : ",arrivee)
//        #    return
//        #if(not(promote=="" or promote=="q" or promote=="r" or promote=="n" or promote=="b")):
//        #    print("domove() ERROR : incorrect promote : ",promote)
//        #    return

        // Informations to save in the history moves
//        Piece pieceDeplacee = cases[depart]; // moved piece
//        Piece piecePrise = cases[arrivee]; // taken piece, can be null : Piece()
//        boolean isEp = false; // will be used to undo a ep move
//        int histEp = ep; // saving the actual ep square (-1 or square number TO)
//        boolean hist_roque_56 = white_can_castle_56;
//        boolean hist_roque_63 = white_can_castle_63;
//        boolean hist_roque_0 = black_can_castle_0;
//        boolean hist_roque_7 = black_can_castle_7;
//        boolean flagViderEp = true; // flag to erase ep or not : if the pawn moved is not taken directly, it can"t be taken later
//
//        // Moving piece
//        cases[arrivee] = cases[depart];
//        cases[depart] = new Piece();
//
//        ply += 1;

        arrButton.setIcon(depButton.getIcon());
        ImageIcon icon = new ImageIcon(
                new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
        depButton.setIcon(icon);

        // a PAWN has been moved -------------------------------------
        //White PAWN

        //TODO pion et ep et promote

//        switch (pieceDeplacee.nom) {
//            case "PION":
//                if (pieceDeplacee.couleur.equals("blanc")) {
//
//                    //If the move is "en passant"
//                    if (ep == arrivee) {
//                        piecePrise = cases[arrivee + 8]; //take black pawn
//                        cases[arrivee + 8] = new Piece();
//                        isEp = true;
//                    }
//                    //The white pawn moves 2 squares from starting square
//                    //then blacks can take "en passant" next move
//                    else if (ROW(depart) == 6 && ROW(arrivee) == 4) {
//                        ep = arrivee + 8;
//                        flagViderEp = false;
//                    }
//                }
//                //Black PAWN
//                else {
//
//                    if (ep == arrivee) {
//                        piecePrise = cases[arrivee - 8];
//                        cases[arrivee - 8] = new Piece();
//                        isEp = true;
//                    } else if (ROW(depart) == 1 && ROW(arrivee) == 3) {
//                        ep = arrivee - 8;
//                        flagViderEp = false;
//                    }
//                }
//                break;
//            // a ROOK has been moved--------------------------------------
//            // update castle rights
//            case "TOUR":
//
//                // White ROOK
//                if (pieceDeplacee.couleur.equals("blanc")) {
//                    if (depart == 56)
//                        white_can_castle_56 = false;
//                    else if (depart == 63)
//                        white_can_castle_63 = false;
//                }
//                // Black ROOK
//                else {
//                    if (depart == 0)
//                        black_can_castle_0 = false;
//                    else if (depart == 7)
//                        black_can_castle_7 = false;
//                }
//                // a KING has been moved-----------------------------------------
//                break;
//            case "ROI":
//
//                // White KING
//                if (pieceDeplacee.couleur.equals("blanc")) {
//
//                    // moving from starting square
//                    if (depart == 60) {
//                        // update castle rights
//                        white_can_castle_56 = false;
//                        white_can_castle_63 = false;
//
//                        // If castling, move the rook
//                        if (arrivee == 58) {
//                            cases[56] = new Piece();
//                            cases[59] = new Piece("TOUR", "blanc");
//                        } else if (arrivee == 62) {
//                            cases[63] = new Piece();
//                            cases[61] = new Piece("TOUR", "blanc");
//                        }
//                    }
//                }
//                // Black KING
//                else {
//
//                    if (depart == 4) {
//                        black_can_castle_0 = false;
//                        black_can_castle_7 = false;
//
//                        if (arrivee == 6) {
//                            cases[7] = new Piece();
//                            cases[5] = new Piece("TOUR", "noir");
//                        } else if (arrivee == 2) {
//                            cases[0] = new Piece();
//                            cases[3] = new Piece("TOUR", "noir");
//                        }
//                    }
//                }
//                break;
//        }

        // End pieces cases-----------------------------------------------

        // Any move cancels the ep move
//        if (flagViderEp)
//            ep = -1;

        // Promote : the pawn is changed to requested piece

        //TODO pion et ep et promote

//        if (!promote.equals("")) {
//            switch (promote) {
//                case "q":
//                    cases[arrivee] = new Piece("DAME", side2move);
//                    break;
//                case "r":
//                    cases[arrivee] = new Piece("TOUR", side2move);
//                    break;
//                case "n":
//                    cases[arrivee] = new Piece("CAVALIER", side2move);
//                    break;
//                case "b":
//                    cases[arrivee] = new Piece("FOU", side2move);
//                    break;
//            }
//        }

        // Change side to move
        // changeTrait();

        // Save move to the history list

//        history.add(new MoveHistory(depart, arrivee, piecePrise, isEp, histEp, promote,
//                hist_roque_56, hist_roque_63, hist_roque_0, hist_roque_7));

        // If the move lets king in check, undo it and return false
//        if (in_check(oppColor(side2move))) {
//            undomove();
//            return false;
//        }
//        return true;
    }
}
