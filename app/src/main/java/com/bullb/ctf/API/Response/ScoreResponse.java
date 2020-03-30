package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.RewardDetail;
import com.bullb.ctf.Model.Score;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class ScoreResponse extends BaseResponse{
    @SerializedName("user_score")
    public Score user_scores;
}
