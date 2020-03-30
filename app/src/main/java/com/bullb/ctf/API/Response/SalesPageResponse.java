package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.BonusRate;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.UserSale;
import com.bullb.ctf.Model.UserTarget;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class SalesPageResponse extends BaseResponse{
    @SerializedName("user_sales")
    public ArrayList<UserSale> user_sales;

    @SerializedName("ranks")
    public Ranks ranks;


    public Ranks getRanks() {
        if (ranks == null)
            return new Ranks();
        return ranks;
    }

}
