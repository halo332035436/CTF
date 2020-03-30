package com.bullb.ctf.API;

import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.VersionResponse;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by oscar on 21/4/16.
 */
public interface ApiService {
    @POST("api/auth/login")
    Call<BaseResponse> loginTask(
            @Header("connection") String connection,
            @Body Map<String, String> fields);

    @POST("api/auth/logout")
    Call<BaseResponse> logout(@Body Map<String, String> fields);


    @POST("api/auth/simple_login")
    Call<BaseResponse> simpleLoginTask(
            @Header("connection") String connection,
            @Body Map<String, String> fields);


    @POST("api/auth/refresh")
    Call<BaseResponse> refreshTokenTask(@Header("Authorization") String token,
                                        @Body Map<String, String> fields );

    @GET("api/users/current")
    Call<BaseResponse> getCurrentUserTask(@Header("Authorization") String token);


    @GET("api/pages/self_management")
    Call<BaseResponse> getSelfManagementTask(@Header("Authorization") String token);

    @GET("api/users/{id}/icon")
    Call<BaseResponse> getUserProfileTask(@Header("Authorization") String token,
                                          @Path("id") String id);



    @GET("api/users/{id}")
    Call<BaseResponse> getUserTask(@Header("Authorization") String token,
                                   @Path("id") String id);

    @PUT("api/users/current/icon")
    Call<BaseResponse> uploadProfileTask(@Header("Authorization") String token,
                                         @Body Map<String, String> fields);


    @POST("api/auth/change_password")
    Call<BaseResponse> changePasswordTask(@Header("Authorization") String token,
                                          @Body Map<String, String> fields);


    @POST("api/auth/forget_password")
    Call<ResponseBody> forgetPasswordTask(@Body Map<String, String> fields);

    @GET("api/user_scores")
    Call<BaseResponse> getScoresTask(
            @Header("Authorization") String token,
            @Query("month") String month,
            @Query("user_id") String user_id,
            @Query("department_id") String department_id);

    @PATCH("api/user_scores/{id}")
    Call<BaseResponse> setScoresTask(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body Map<String, String> fields);

    @GET("api/pages/my_bonuses")
    Call<BaseResponse> getBonusesTask(@Header("Authorization") String token,
                                      @Query("month") String month);

    @GET("api/user_rewards")
    Call<BaseResponse> getUserRewardTask(@Header("Authorization") String token,
                                         @Query("user_id") String id,
                                         @Query("month") String month);

    @GET("api/user_reward_details")
    Call<BaseResponse> getUserRewardDetailTask(@Header("Authorization") String token,
                                               @Query("user_id") String id,
                                               @Query("from_date") String from_date,
                                               @Query("to_date") String to_date);

    @PATCH("api/users/{id}")
    Call<BaseResponse> setPersonalInfoTask(@Header("Authorization") String token,
                                           @Path("id") String id,
                                           @Body Map<String, String> fields);


    @GET("api/banners")
    Call<BaseResponse> getBannerListTask(@Header("Authorization") String token);


    @GET("api/banners")
    Call<BaseResponse> getBannerTask(@Header("Authorization") String token,
                                     @Query("id") int id);

    @GET("api/banners/random")
    Call<ResponseBody> getRandomBannerTask(@Header("Authorization") String token);

    @GET("api/campaigns")
    Call<BaseResponse> getCampaignTask(@Header("Authorization") String token,
                                       @Query("type") String type);

    @GET("api/campaigns")
    Call<BaseResponse> getCampaignTask(@Header("Authorization") String token,
                                       @Query("type") String type,
                                       @Query("name") String name);


    @GET("api/campaigns/{id}")
    Call<BaseResponse> getSpecificCampaignTask(@Header("Authorization") String token,
                                       @Path("id") String id);

    @GET("api/campaigns")
    Call<BaseResponse> getCampaignTask(@Header("Authorization") String token);

    @GET("api/pages/my_targets")
    Call<BaseResponse> getMyTargetPageTask(@Header("Authorization") String token,
                                           @Query("from_date") String from_date,
                                           @Query("to_date") String to_date,
                                           @Query("user_id") String user_id,
                                           @Query("rank_method") String rank_method);



    @GET("api/leaderboard/{department_id}")
    Call<BaseResponse> getLeaderboardTask(
            @Header("Authorization") String token,
            @Path("department_id") String department_id,
            @Query("from_date") String from_date,
            @Query("to_date") String to_date,
            @Query("order") String order,
            @Query("type") String type);

    @GET("api/leaderboard/{department_id}/{properties}")
    Call<BaseResponse> getLeaderboardTask(
            @Header("Authorization") String token,
            @Path("department_id") String department_id,
            @Path("properties") String properties,
            @Query("from_date") String from_date,
            @Query("to_date") String to_date,
            @Query("order") String order,
            @Query("type") String type);


    @GET("api/users/current/viewable_departments")
    Call<BaseResponse> getViewableDepartment(
            @Header("Authorization") String token,
            @Query("properties") String properties,
            @Query("with_sales") String withSales);


    @GET("api/pages/team_targets")
    Call<BaseResponse> getTeamTargetTask(@Header("Authorization") String token,
                                         @Query("from_date") String from_date,
                                         @Query("to_date") String to_date,
                                         @Query("mode") String mode,
                                         @Query("department_id") String department_id,
                                         @Query("rank_method") String rank_method);


    @GET("api/pages/team_targets")
    Call<BaseResponse> getTeamTargetTask(@Header("Authorization") String token,
                                         @Query("from_date") String from_date,
                                         @Query("to_date") String to_date,
                                         @Query("mode") String mode,
                                         @Query("department_id") String department_id,
                                         @Query("rank_method") String rank_method,
                                         @Query("salesperson") int filter);

    @PATCH("api/user_targets")
    Call<BaseResponse> updateTargetTask(@Header("Authorization") String token,@Body Map<String, String> fields);



    @GET("api/user_sale_details")
    Call<BaseResponse> getSalesDetailTask(@Header("Authorization") String token,
                                          @Query("from_date") String from_date,
                                          @Query("to_date") String to_date,
                                          @Query("type") String type,
                                          @Query("user_id") String user_id,
                                          @Query("department_id") String department_id,
                                          @Query("minimum_amount") String minimum_amount,
                                          @Query("maximum_amount") String maximum_amount);

//    @GET("api/notifications")
//    Call<BaseResponse> getNotificationListTask(@Header("Authorization") String token);


    @GET("api/user_notifications")
    Call<BaseResponse> getNotificationListTask(@Header("Authorization") String token);

    @GET("api/user_notifications/{id}")
    Call<BaseResponse> getNotificationTask(@Header("Authorization") String token,
                                                    @Path("id") String id);



    @POST("api/users/current/record_page_access")
    Call<BaseResponse> recordAccessTask(@Header("Authorization") String token,
                                        @Body Map<String, String> fields);



    @GET("api/users/{id}/reward")
    Call<BaseResponse> getRewardTask(@Header("Authorization") String token,
                                     @Path("id") String id,
                                     @Query("month") String from_date);


    @GET("storage/versions.json")
    Call<VersionResponse> getVersionTask();


    @GET("api/campaigns/newest")
    Call<BaseResponse> getNewestCampaignTask(@Header("Authorization") String token);


    @PUT("api/auth/channel")
    Call<BaseResponse> updateChannelTask(@Header("Authorization") String token,
                                         @Header("Content-Type") String cType,
                                         @Header("x-ctf-content-type") String type,
                                         @Body Map<String, String> fields);


    @GET("api/campaigns?campaign_type=banner")
    Call<BaseResponse> getCampaignBannersTask(@Header("Authorization") String token);


    @GET("api/indicators")
    Call<BaseResponse> getAllIndicator(@Header("Authorization") String token);


    @POST("api/ratings")
    Call<BaseResponse> updateRating(@Header("Authorization") String token,
                                        @Body Map<String, String> fields);

    @GET("api/ratings")
    Call<BaseResponse> getUserRating(@Header("Authorization") String token,
                                     @Query("month") String Month,
                                     @Query("user_id") String user_id);

    @GET("api/ratings")
    Call<BaseResponse> getDepartmentRating(@Header("Authorization") String token,
                                     @Query("month") String Month,
                                     @Query("department_id") String department_id);


    @PUT("api/ratings/{rating}/confirm")
    Call<BaseResponse> confirmRating(@Header("Authorization") String token,
                                         @Path("rating") String rating);


    @PUT("api/user_targets/confirm")
    Call<BaseResponse> confirmTarget(@Header("Authorization") String token,
                                     @Body Map<String, String> fields);

    @GET("api/user_targets/check_dates")
    Call<BaseResponse> getSubmitAvailable(@Header("Authorization") String token,
                                     @Query("date") String date);


}

