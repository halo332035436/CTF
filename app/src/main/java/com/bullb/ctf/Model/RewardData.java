package com.bullb.ctf.Model;


import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class RewardData extends BaseModel{


    @SerializedName("user_rewards")
    private ArrayList<Reward> rewards;

    @SerializedName("rewards")
    private double rewardRemain;

    private boolean needCal;

    public ArrayList<Reward> getRewards() {
        return rewards;
    }

    public double getRewardRemain() {
        return rewardRemain;
    }

    public double getRewardFromArray(String date) {
        try {
            if (rewards != null && rewards.size()>0) {
                for (Reward reward : rewards) {
                    Log.d("debug", date + " " + reward.month.substring(0, 10));
                    if (reward.month.substring(0, 10).equals(date)){
                        return reward.amount;
                    }
                }
            }
        }catch (Exception e){
        }
        return rewardRemain;
    }

    public RewardData(ArrayList<Reward> rewards, double rewardRemain) {
        this.rewards = rewards;
        this.rewardRemain = rewardRemain;
    }


}
