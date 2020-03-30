package com.bullb.ctf.Model;


import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class BonusesData extends BaseModel{


    @SerializedName("bonuses")
    private ArrayList<Bonus> bonuses;

    //Bonus
    private double commissionPrize;
    private double targetPrize;
    private double profitPrize;
    private double totalBonus;

    public ArrayList<Bonus> getBonuses() {
        return bonuses;
    }

    public double getProfitPrize() {
        return profitPrize;
    }

    public double getTotalBonus(String user_type) {
        if (user_type.equals(User.USER_TYPE_C)){
            return profitPrize;
        }
        return totalBonus;
    }

    public double getTargetPrize() {
        return targetPrize;
    }

    public double getCommissionPrize() {
        return commissionPrize;
    }

    public void calculateBonus() {
        if (bonuses != null) {
            for (Bonus bonus : bonuses) {
                if (bonus.type.equals(Bonus.COMMISSION_PRIZE)) {
                    commissionPrize = Double.valueOf(bonus.amount);
                } else if (bonus.type.equals(Bonus.TARGET_COMPLETE_PRIZE)) {
                    targetPrize = Double.valueOf(bonus.amount);
                } else if (bonus.type.equals(Bonus.PROFIT_PRIZE)) {
                    profitPrize = Double.valueOf(bonus.amount);
                }
            }
            totalBonus = commissionPrize + profitPrize + targetPrize;
        }
    }

    public BonusesData(ArrayList<Bonus> bonuses){
        this.bonuses = bonuses;
        calculateBonus();
    }
}
