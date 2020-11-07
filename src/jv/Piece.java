package jv;

import jv.move.Move;

import java.util.ArrayList;

public class Piece {
    private final String VIDE = "."; // empty piece name (=empty square '.' in console)
    String nom;
    String couleur;
    int valeur;
    private int[] tab120 =
            {
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, 0, 1, 2, 3, 4, 5, 6, 7, -1,
                    -1, 8, 9, 10, 11, 12, 13, 14, 15, -1,
                    -1, 16, 17, 18, 19, 20, 21, 22, 23, -1,
                    -1, 24, 25, 26, 27, 28, 29, 30, 31, -1,
                    -1, 32, 33, 34, 35, 36, 37, 38, 39, -1,
                    -1, 40, 41, 42, 43, 44, 45, 46, 47, -1,
                    -1, 48, 49, 50, 51, 52, 53, 54, 55, -1,
                    -1, 56, 57, 58, 59, 60, 61, 62, 63, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            };
    private int[] tab64 = {
            21, 22, 23, 24, 25, 26, 27, 28,
            31, 32, 33, 34, 35, 36, 37, 38,
            41, 42, 43, 44, 45, 46, 47, 48,
            51, 52, 53, 54, 55, 56, 57, 58,
            61, 62, 63, 64, 65, 66, 67, 68,
            71, 72, 73, 74, 75, 76, 77, 78,
            81, 82, 83, 84, 85, 86, 87, 88,
            91, 92, 93, 94, 95, 96, 97, 98
    };
    // Moving vectors according to the 'tab64',
    private int[] deplacements_tour = {-10, 10, -1, 1};
    // KING and QUEEN = BISHOP + ROOK !
    private int[] deplacements_fou = {-11, -9, 11, 9};
    private int[] deplacements_cavalier = {-12, -21, -19, -8, 12, 21, 19, 8};
    private int[] deplacements_roi_dame = {-10, 10, -1, 1, -11, -9, 11, 9};

    Piece(String nomStr, String couleurStr) {
        nom = nomStr;
        couleur = couleurStr;
        switch (nomStr) {
            case VIDE:
            case "ROI":
                valeur = 0;
                break;
            case "DAME":
                valeur = 9;
                break;
            case "TOUR":
                valeur = 5;
                break;
            case "CAVALIER":
            case "FOU":
                valeur = 3;
                break;
            case "PION":
                valeur = 1;
                break;
        }


    }

    Piece() {
        nom = VIDE;
        couleur = "";
    }

    ArrayList<Move> pos2_roi(int pos1, String cAd, Board echiquier, boolean dontCallIsAttacked) {
        ArrayList<Move> liste = new ArrayList<>();

        for (int i : deplacements_roi_dame) {
            int n = tab120[tab64[pos1] + i];
            if (n != -1)
                if (echiquier.cases[n].isEmpty() || echiquier.cases[n].couleur.equals(cAd))
                    liste.add(new Move(pos1, n, ""));
        }
        if (dontCallIsAttacked) return liste; // we just wanted moves that can attack

        // The current side to move is the opposite of cAd
        String c = echiquier.oppColor(cAd);

        // Castle moves
        if (c.equals("blanc")) {
            if (echiquier.white_can_castle_63) {
                if (echiquier.cases[63].nom.equals("TOUR") &&
                        echiquier.cases[63].couleur.equals("blanc") &&
                        echiquier.cases[61].isEmpty() &&
                        echiquier.cases[62].isEmpty() &&
                        !echiquier.is_attacked(61, "noir") &&
                        !echiquier.is_attacked(62, "noir") &&
                        !echiquier.is_attacked(pos1, "noir"))
                    liste.add(new Move(pos1, 62, ""));
            }
            if (echiquier.white_can_castle_56) {
                // S'il y a une tour en 56, etc...
                if (echiquier.cases[56].nom.equals("TOUR") &&
                        echiquier.cases[56].couleur.equals("blanc") &&
                        echiquier.cases[57].isEmpty() &&
                        echiquier.cases[58].isEmpty() &&
                        echiquier.cases[59].isEmpty() &&
                        !echiquier.is_attacked(58, cAd) &&
                        !echiquier.is_attacked(59, cAd) &&
                        !echiquier.is_attacked(pos1, cAd))
                    liste.add(new Move(pos1, 58, ""));
            }

        } else if (c.equals("noir")) {
            if (echiquier.black_can_castle_7) {
                if (echiquier.cases[7].nom.equals("TOUR") &&
                        echiquier.cases[7].couleur.equals("noir") &&
                        echiquier.cases[5].isEmpty() &&
                        echiquier.cases[6].isEmpty() &&
                        !echiquier.is_attacked(5, cAd) &&
                        !echiquier.is_attacked(6, cAd) &&
                        !echiquier.is_attacked(pos1, cAd))
                    liste.add(new Move(pos1, 6, ""));
                if (echiquier.black_can_castle_0) {
                    if (echiquier.cases[0].nom.equals("TOUR") &&
                            echiquier.cases[0].couleur.equals("noir") &&
                            echiquier.cases[1].isEmpty() &&
                            echiquier.cases[2].isEmpty() &&
                            echiquier.cases[3].isEmpty() &&
                            !echiquier.is_attacked(2, cAd) &&
                            !echiquier.is_attacked(3, cAd) &&
                            !echiquier.is_attacked(pos1, cAd))
                        liste.add(new Move(pos1, 2, ""));
                }
            }
        }
        return liste;
    }

    ArrayList<Move> pos2_tour(int pos1, String cAd, Board echiquier) {
        ArrayList<Move> liste = new ArrayList<>();

        for (int k : deplacements_tour) {
            int j = 1;
            while (true) {
                int n = tab120[tab64[pos1] + (k * j)];
                if (n != -1) { // #as we are not out of the board
                    if (echiquier.cases[n].isEmpty() || echiquier.cases[n].couleur.equals(cAd))
                        liste.add(new Move(pos1, n, "")); // append the move if square is empty of opponent color
                } else break; // stop if outside of the board
                if (!echiquier.cases[n].isEmpty())
                    break;//destination square is not empty(opponent or not) then the rook won 't pass through
                j = j + 1;
            }
        }
        return liste;
    }

    ArrayList<Move> pos2_cavalier(int pos1, String cAd, Board echiquier) {
        ArrayList<Move> liste = new ArrayList<>();

        for (int i : deplacements_cavalier) {
            int n = tab120[tab64[pos1] + i];
            if (n != -1) {
                if (echiquier.cases[n].isEmpty() || echiquier.cases[n].couleur.equals(cAd))
                    liste.add(new Move(pos1, n, ""));
            }
        }
        return liste;
    }

    ArrayList<Move> pos2_fou(int pos1, String cAd, Board echiquier) {
        ArrayList<Move> liste = new ArrayList<>();

        for (int k : deplacements_fou) {
            int j = 1;
            while (true) {
                int n = tab120[tab64[pos1] + (k * j)];
                if (n != -1) { // as we are not out of the board
                    if (echiquier.cases[n].isEmpty() || echiquier.cases[n].couleur.equals(cAd))
                        liste.add(new Move(pos1, n, "")); // append the move if square is empty of opponent color
                } else break; // stop if outside of the board
                if (!echiquier.cases[n].isEmpty())
                    break; // destination square is not empty(opponent or not) then the bishop won 't pass through
                j = j + 1;
            }
        }
        return liste;
    }


    ArrayList<Move> pos2_pion(int pos1, String couleur, Board echiquier) {
        ArrayList<Move> liste = new ArrayList<>();

        // White PAWN ---------------------------------------------------
        if (couleur.equals("blanc")) {

            // Upper square
            int n = tab120[tab64[pos1] - 10];
            if (n != -1) {
                if (echiquier.cases[n].isEmpty()) {
                    // If the PAWN has arrived to rank 8 (square 0 to 7),
                    if (n < 8) {
                        // it will be promoted
                        liste.add(new Move(pos1, n, "q"));
                        liste.add(new Move(pos1, n, "r"));
                        liste.add(new Move(pos1, n, "n"));
                        liste.add(new Move(pos1, n, "b"));
                    } else
                        liste.add(new Move(pos1, n, ""));
                }
            }
            // 2nd square if PAWN is at starting square
            if (echiquier.ROW(pos1) == 6) {
                // If the 2 upper squares are empty
                if (echiquier.cases[pos1 - 8].isEmpty() && echiquier.cases[pos1 - 16].isEmpty())
                    liste.add(new Move(pos1, pos1 - 16, ""));
            }
            // Capture upper left
            n = tab120[tab64[pos1] - 11];
            if (n != -1) {
                if (echiquier.cases[n].couleur.equals("noir") || echiquier.ep == n) {
                    if (n < 8) { // Capture + promote
                        liste.add(new Move(pos1, n, "q"));
                        liste.add(new Move(pos1, n, "r"));
                        liste.add(new Move(pos1, n, "n"));
                        liste.add(new Move(pos1, n, "b"));
                    } else
                        liste.add(new Move(pos1, n, ""));
                }
            }
            // Capture upper right
            n = tab120[tab64[pos1] - 9];
            if (n != -1) {
                if (echiquier.cases[n].couleur.equals("noir") || echiquier.ep == n) {
                    if (n < 8) {
                        liste.add(new Move(pos1, n, "q"));
                        liste.add(new Move(pos1, n, "r"));
                        liste.add(new Move(pos1, n, "n"));
                        liste.add(new Move(pos1, n, "b"));
                    } else
                        liste.add(new Move(pos1, n, ""));
                }
            }
        }
        // Black PAWN ---------------------------------------------------
        else {

            // Upper square
            int n = tab120[tab64[pos1] + 10];
            if (n != -1) {
                if (echiquier.cases[n].isEmpty()) {
                    // PAWN has arrived to 8 th rank (square 56 to 63),
                    // it will be promoted
                    if (n > 55) {
                        liste.add(new Move(pos1, n, "q"));
                        liste.add(new Move(pos1, n, "r"));
                        liste.add(new Move(pos1, n, "n"));
                        liste.add(new Move(pos1, n, "b"));
                    } else
                        liste.add(new Move(pos1, n, ""));
                }
            }
            // 2nd square if PAWN is at starting square
            if (echiquier.ROW(pos1) == 1) {
                // If the 2 upper squares are empty
                if (echiquier.cases[pos1 + 8].isEmpty() && echiquier.cases[pos1 + 16].isEmpty())
                    liste.add(new Move(pos1, pos1 + 16, ""));
            }
            // Capture bottom left
            n = tab120[tab64[pos1] + 9];
            if (n != -1) {
                if (echiquier.cases[n].couleur.equals("blanc") || echiquier.ep == n) {
                    if (n > 55) {
                        liste.add(new Move(pos1, n, "q"));
                        liste.add(new Move(pos1, n, "r"));
                        liste.add(new Move(pos1, n, "n"));
                        liste.add(new Move(pos1, n, "b"));
                    } else
                        liste.add(new Move(pos1, n, ""));
                }
            }
            // Capture bottom right
            n = tab120[tab64[pos1] + 11];
            if (n != -1) {
                if (echiquier.cases[n].couleur.equals("blanc") || echiquier.ep == n) {
                    if (n > 55) {
                        liste.add(new Move(pos1, n, "q"));
                        liste.add(new Move(pos1, n, "r"));
                        liste.add(new Move(pos1, n, "n"));
                        liste.add(new Move(pos1, n, "b"));
                    } else
                        liste.add(new Move(pos1, n, ""));
                }
            }

        }
        return liste;
    }

    private boolean isEmpty() {
        return nom.equals(VIDE);
    }


}
