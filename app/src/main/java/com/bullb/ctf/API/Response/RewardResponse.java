package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.Reward;
import com.bullb.ctf.Model.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class RewardResponse extends BaseResponse{
    @SerializedName("rewards")
    public ArrayList<Reward> rewards;
}
