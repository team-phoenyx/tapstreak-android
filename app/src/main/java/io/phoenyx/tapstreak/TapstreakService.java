package io.phoenyx.tapstreak;


import java.util.List;

import io.phoenyx.tapstreak.jsonmodels.Friend;
import io.phoenyx.tapstreak.jsonmodels.Salt;
import io.phoenyx.tapstreak.jsonmodels.UserID;
import io.phoenyx.tapstreak.jsonmodels.UsernameCheck;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by terrance on 5/20/17.
 */

public interface TapstreakService {
    @FormUrlEncoded
    @POST("/user/internal")
    Call<User> getUserInternal(@Field("user_id") String userID, @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/user/external")
    Call<User> getUserExternal(@Field("user_id") String userID);

    @FormUrlEncoded
    @POST("/user/create")
    Call<UserID> registerUser(@Field("username") String username, @Field("pass_hashed") String passwordHashed, @Field("salt") String salt);

    @FormUrlEncoded
    @POST("/user/get-salt")
    Call<Salt> getSalt(@Field("username") String username);

    @FormUrlEncoded
    @POST("/user/login")
    Call<UserID> loginUser(@Field("username") String username, @Field("pass_hashed") String passwordHashed);

    @FormUrlEncoded
    @POST("/user/delete")
    Call<ResponseCode> deleteUser(@Field("user_id") String userID, @Field("access_token") String access_token, @Field("pass_hashed") String passwordHashed);

    @FormUrlEncoded
    @POST("/user/change-pw")
    Call<ResponseCode> changePassword(@Field("user_id") String userID, @Field("access_token") String access_token); //TODO: LEFT OFF WORK HERE

    @FormUrlEncoded
    @POST("/user/internal")
    Call<User> getUserInternal(@Field("user_id") String userID, @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/user/internal")
    Call<User> getUserInternal(@Field("user_id") String userID, @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/user/internal")
    Call<User> getUserInternal(@Field("user_id") String userID, @Field("access_token") String access_token);
}
