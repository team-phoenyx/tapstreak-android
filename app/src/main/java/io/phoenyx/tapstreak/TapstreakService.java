package io.phoenyx.tapstreak;


import java.util.List;

import io.phoenyx.tapstreak.jsonmodels.Friend;
import io.phoenyx.tapstreak.jsonmodels.Salt;
import io.phoenyx.tapstreak.jsonmodels.UserID;
import io.phoenyx.tapstreak.jsonmodels.UsernameCheck;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by terrance on 5/20/17.
 */

public interface TapstreakService {
    String endpoint = "";

    //@GET("user/{user_id}")
    //Call<User> getUser(@Path("user_id") String user_id);

    @GET("user/{user_id}/friends")
    Call<List<Friend>> getFriends(@Path("user_id") String user_id);

    @GET("user/{username}/{password_hashed}/{salt}/create")
    Call<UserID> makeUser(@Path("username") String username, @Path("password_hashed") String password_hashed, @Path("salt") String salt);

    @GET("user/{username}/get-salt")
    Call<Salt> getSalt(@Path("username") String username);

    @GET("user/{username}/{password_hashed}")
    Call<UserID> login(@Path("username") String username, @Path("password_hashed") String password_hashed);

    @GET("user/{username}/check-dupe")
    Call<UsernameCheck> checkDuplicate(@Path("username") String username);

    @GET("user/{user_id}/{friend_id}/add-friend")
    Call<ResponseBody> createFriends(@Path("user_id") String user_id, @Path("friend_id") String friend_id);

    @POST("user/{user_id}/{friend_id}/refresh-streak")
    Call<ResponseBody> refreshStreak(@Path("user_id") String user_id, @Path("friend_id") String friend_id);

    @POST("user/{user_id}/{friend_id}/remove-streak")
    Call<ResponseBody> removeStreak(@Path("user_id") String user_id, @Path("friend_id") String friend_id);
}
