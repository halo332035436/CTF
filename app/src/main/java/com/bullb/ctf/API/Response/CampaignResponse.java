package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.Model.Reward;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class CampaignResponse extends BaseResponse{
    @SerializedName("campaigns")
    public ArrayList<Campaign> campaigns;
}
