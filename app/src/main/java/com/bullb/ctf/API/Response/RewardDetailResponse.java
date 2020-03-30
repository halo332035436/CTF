package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.Reward;
import com.bullb.ctf.Model.RewardDetail;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class RewardDetailResponse extends BaseResponse{
    @SerializedName("reward_detail")
    public ArrayList<RewardDetail> reward_detail;
}
