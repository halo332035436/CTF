package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.Version;
import com.google.gson.annotations.SerializedName;

/**
 * Created by oscar on 7/1/16.
 */
public class VersionResponse extends BaseResponse{
    @SerializedName("ctf-smart-talent")
    private SmartTalentVersion smartTalentVersion;

    public Version getVersion() {
        return smartTalentVersion.getVersion();
    }


    private class SmartTalentVersion{
        @SerializedName("android")
        private Version version;

        public Version getVersion() {
            return version;
        }
    }
}
