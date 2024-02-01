package com.ashcollege.responses;

import com.ashcollege.BetService.Bet;

public class BetResponse extends BasicResponse{
    private Bet bet;

    public BetResponse(boolean success, Integer errorCode, Bet bet) {
        super(success, errorCode);
        this.bet = bet;
    }

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }
}
