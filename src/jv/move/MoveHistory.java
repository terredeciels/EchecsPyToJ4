package jv.move;

import jv.Piece;

public class MoveHistory {
    private final int depart;
    private final int arrivee;
    private final String promote;
    private final int histEp;
    private final Piece piecePrise;
    private final boolean hist_roque_56;
    private final boolean hist_roque_63;
    private final boolean isEp;
    private final boolean hist_roque_0;
    private final boolean hist_roque_7;
    // ?? private final Piece pieceDeplacee;

    public MoveHistory(int depart, int arrivee, Piece piecePrise,
                       boolean isEp, int histEp, String promote,
                       boolean hist_roque_56, boolean hist_roque_63, boolean hist_roque_0, boolean hist_roque_7) {
        this.depart = depart;
        this.arrivee = arrivee;
        // ?? this.pieceDeplacee = pieceDeplacee;
        this.piecePrise = piecePrise;
        this.isEp = isEp;
        this.histEp = histEp;
        this.promote = promote;
        this.hist_roque_56 = hist_roque_56;
        this.hist_roque_63 = hist_roque_63;
        this.hist_roque_0 = hist_roque_0;
        this.hist_roque_7 = hist_roque_7;
    }

    public int getDepart() {
        return depart;
    }


    public int getArrivee() {
        return arrivee;
    }

    public String getPromote() {
        return promote;
    }

    public int getHistEp() {
        return histEp;
    }

    public Piece getPiecePrise() {
        return piecePrise;
    }

    public boolean isHist_roque_56() {
        return hist_roque_56;
    }

    public boolean isHist_roque_63() {
        return hist_roque_63;
    }

    public boolean isEp() {
        return isEp;
    }

    public boolean isHist_roque_0() {
        return hist_roque_0;
    }

    public boolean isHist_roque_7() {
        return hist_roque_7;
    }
}
