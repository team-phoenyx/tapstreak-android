package io.phoenyx.tapstreak;


import io.phoenyx.tapstreak.jsonmodels.ResponseCode;
import io.phoenyx.tapstreak.jsonmodels.Salt;
import io.phoenyx.tapstreak.jsonmodels.User;
import io.phoenyx.tapstreak.jsonmodels.Authentication;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by terrance on 5/20/17.
 */

public interface TapstreakService {
    @FormUrlEncoded
    @POST("/user/internal")
    Call<User> getUserInternal(@Field("user_id") String userID,
                               @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/user/external")
    Call<User> getUserExternal(@Field("user_id") String userID);

    @FormUrlEncoded
    @POST("/user/create")
    Call<Authentication> registerUser(@Field("username") String username,
                                      @Field("pass_hashed") String passwordHashed,
                                      @Field("salt") String salt);

    @FormUrlEncoded
    @POST("/user/get-salt")
    Call<Salt> getSalt(@Field("username") String username);

    @FormUrlEncoded
    @POST("/user/login")
    Call<Authentication> loginUser(@Field("username") String username,
                                   @Field("pass_hashed") String passwordHashed);

    @FormUrlEncoded
    @POST("/user/delete")
    Call<ResponseCode> deleteUser(@Field("user_id") String userID,
                                  @Field("access_token") String access_token,
                                  @Field("pass_hashed") String passwordHashed);

    @FormUrlEncoded
    @POST("/user/change-pw")
    Call<ResponseCode> changePassword(@Field("user_id") String userID,
                                      @Field("access_token") String access_token,
                                      @Field("pass_hashed") String passwordHashed,
                                      @Field("new_pass_hashed") String newPasswordHashed,
                                      @Field("new_salt") String newSalt);

    @FormUrlEncoded
    @POST("/user/add-friend")
    Call<ResponseCode> addFriend(@Field("user_id") String userID,
                                 @Field("access_token") String access_token,
                                 @Field("friend_id") String friendID);

    @FormUrlEncoded
    @POST("/user/remove-friend")
    Call<ResponseCode> removeFriend(@Field("user_id") String userID,
                                 @Field("access_token") String access_token,
                                 @Field("friend_id") String friendID);

    @FormUrlEncoded
    @POST("/user/refresh-streak")
    Call<ResponseCode> refreshStreak(@Field("user_id") String userID,
                                 @Field("access_token") String access_token,
                                 @Field("friend_id") String friendID);
}
