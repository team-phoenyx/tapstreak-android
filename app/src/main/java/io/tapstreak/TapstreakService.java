package io.tapstreak;


import io.tapstreak.json_models.ResponseCode;
import io.tapstreak.json_models.Salt;
import io.tapstreak.json_models.User;
import io.tapstreak.json_models.Authentication;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by terrance on 5/20/17.
 */

public interface TapstreakService {
    @FormUrlEncoded
    @POST("/api/user/personal")
    Call<User> getUserInternal(@Field("user_id") String userID,
                               @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/api/user/public")
    Call<User> getUserExternal(@Field("user_id") String userID);

    @FormUrlEncoded
    @POST("/api/user/unexists")
    Call<ResponseCode> checkUsernameExists(@Field("username") String username);

    @FormUrlEncoded
    @POST("/api/user/create")
    Call<Authentication> registerUser(@Field("username") String username,
                                      @Field("pass_hashed") String passwordHashed,
                                      @Field("salt") String salt);

    @FormUrlEncoded
    @POST("/api/user/salt")
    Call<Salt> getSalt(@Field("username") String username);

    @FormUrlEncoded
    @POST("/api/user/login")
    Call<Authentication> loginUser(@Field("username") String username,
                                   @Field("pass_hashed") String passwordHashed);

    @FormUrlEncoded
    @POST("/api/user/delete")
    Call<ResponseCode> deleteUser(@Field("user_id") String userID,
                                  @Field("access_token") String access_token,
                                  @Field("pass_hashed") String passwordHashed);

    @FormUrlEncoded
    @POST("/api/user/cpw")
    Call<ResponseCode> changePassword(@Field("user_id") String userID,
                                      @Field("access_token") String access_token,
                                      @Field("pass_hashed") String passwordHashed,
                                      @Field("new_pass_hashed") String newPasswordHashed,
                                      @Field("new_salt") String newSalt);

    @FormUrlEncoded
    @POST("/api/user/cun")
    Call<ResponseCode> changeUsername(@Field("user_id") String userID,
                                      @Field("access_token") String access_token,
                                      @Field("new_username") String newUsername);

    @FormUrlEncoded
    @POST("/api/user/setloc")
    Call<ResponseCode> setLocation(@Field("user_id") String userID,
                                      @Field("access_token") String access_token,
                                      @Field("time") String timestamp, @Field("lat") String latitude, @Field("lon") String longitude);

    @FormUrlEncoded
    @POST("/api/user/afriend")
    Call<ResponseCode> addFriend(@Field("user_id") String userID,
                                 @Field("access_token") String access_token,
                                 @Field("friend_id") String friendID);

    @FormUrlEncoded
    @POST("/api/user/rfriend")
    Call<ResponseCode> removeFriend(@Field("user_id") String userID,
                                 @Field("access_token") String access_token,
                                 @Field("friend_id") String friendID);

    @FormUrlEncoded
    @POST("/api/user/rfstreak")
    Call<ResponseCode> refreshStreak(@Field("user_id") String userID,
                                 @Field("access_token") String access_token,
                                 @Field("friend_id") String friendID);
}
