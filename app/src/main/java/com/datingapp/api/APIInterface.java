package com.datingapp.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Suraj Shakya on 01/06/2018.
 */

public interface APIInterface {

    @Multipart
    @POST("DatingController/userImage")
    Call<ResponseBody> userImage(@Part("user_id") RequestBody user_id,
                                 @Part("app_token") RequestBody app_token,
                                 @Part MultipartBody.Part profile_image,
                                 @Part("userlat") RequestBody userlat,
                                 @Part("userlong") RequestBody userlong,
                                 @Part("address") RequestBody address,
//                                     @Part("image") RequestBody image,
                                 @Part("type") RequestBody type);

    @Multipart
    @POST("DatingController/uploadmultipleImage")
    Call<ResponseBody> imageUpload(@Part("user_id") RequestBody user_id,
                                   @Part("app_token") RequestBody app_token,
                                   @Part MultipartBody.Part profile_image,
                                   @Part("type") RequestBody type);

    @Multipart
    @POST("DatingController/updatemultileImage")
    Call<ResponseBody> imageUpdate(@Part("user_id") RequestBody user_id,
                                   @Part("app_token") RequestBody app_token,
                                   @Part MultipartBody.Part profile_image,
                                   @Part("type") RequestBody type);

    @Multipart
    @POST("DatingController/addBlog")
    Call<ResponseBody> addBlog(@Part("user_id") RequestBody user_id,
                               @Part("app_token") RequestBody app_token,
                               @Part MultipartBody.Part blogimage,
                               @Part("description") RequestBody description);

    @Multipart
    @POST("DatingController/updateBlog")
    Call<ResponseBody> updateBlog(@Part("user_id") RequestBody user_id,
                                  @Part("app_token") RequestBody app_token,
                                  @Part MultipartBody.Part blogimage,
                                  @Part("description") RequestBody description);

    @Multipart
    @POST("DatingController/addTimeline")
    Call<ResponseBody> addTimeLine(@Part("user_id") RequestBody user_id,
                                   @Part("app_token") RequestBody app_token,
                                   @Part MultipartBody.Part timeLineImage,
                                   @Part("timelinetext") RequestBody description);

    @Multipart
    @POST("DatingController/addappFeedback")
    Call<ResponseBody> addFeedback(@Part("user_id") RequestBody user_id,
                                   @Part("app_token") RequestBody app_token,
                                   @Part MultipartBody.Part timeLineImage,
                                   @Part("message") RequestBody description);

    @POST("DatingController/addappFeedback")
    Call<ResponseBody> addFeedbackWithOutImage(@Field("user_id") RequestBody user_id,
                                               @Field("app_token") RequestBody app_token,
                                               @Field("message") RequestBody description);

    @FormUrlEncoded
    @POST("DatingController/addQuestionsanswer")
    Call<ResponseBody> putAnswer(@Field("app_token") String token, @Field("userid") String userId, @Field("questionid") String questionId,
                                 @Field("quesanswers") String quesAnswers);

    @FormUrlEncoded
    @POST("DatingController/addpartnerQuestionsanswer")
    Call<ResponseBody> putAnswerPartner(@Field("app_token") String token, @Field("userid") String userId, @Field("questionid") String questionId,
                                        @Field("quesanswers") String quesAnswers);

    @FormUrlEncoded
    @POST("DatingController/removeinvition")
    Call<ResponseBody> removeInvite(@Field("app_token") String token, @Field("user_id") String userId, @Field("inviteid") String inviteId,
                                    @Field("inviteunique") String inviteToken, @Field("type") String type);


    @FormUrlEncoded
    @POST("DatingController/userLogin")
    Call<ResponseBody> loginApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/like")
    Call<ResponseBody> setLikeApi(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("DatingController/userRegister")
    Call<ResponseBody> registerApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/userApproved")
    Call<ResponseBody> userApprovedApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/userImage")
    Call<ResponseBody> socialImageApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/forgetuserpassword")
    Call<ResponseBody> forgotApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/social_login")
    Call<ResponseBody> socialLoginApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/nearByUsers")
    Call<ResponseBody> nearByApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/likeList")
    Call<ResponseBody> getLikeList(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/showprofilevisitedbyUser")
    Call<ResponseBody> profileVisitApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/showblockUser")
    Call<ResponseBody> getBlockUserApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/showblogUser")
    Call<ResponseBody> getBlogApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/ethnicity")
    Call<ResponseBody> getEthnicityApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/updateUserProfile")
    Call<ResponseBody> updateProfileApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/showHobbies")
    Call<ResponseBody> showHobbiesApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/match")
    Call<ResponseBody> matchListApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/showQuestionlist")
    Call<ResponseBody> questionListApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/invitionList")
    Call<ResponseBody> getInviteList(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/invition")
    Call<ResponseBody> inviteUserApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/userDetails")
    Call<ResponseBody> userDetailsApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/blockUser")
    Call<ResponseBody> blockUserApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/reportAbuse")
    Call<ResponseBody> reportAbuseApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/showTimeline")
    Call<ResponseBody> showTimelineApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/profilevisitedbyUser")
    Call<ResponseBody> profilevisitedbyUserApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/showpartnerQuestionlist")
    Call<ResponseBody> partnerListApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/logout")
    Call<ResponseBody> logoutApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/preference")
    Call<ResponseBody> preferenceApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/notifpreference")
    Call<ResponseBody> notifpreferenceApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/aboutApp")
    Call<ResponseBody> aboutAppApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/answerDetailsquestionId")
    Call<ResponseBody> answerDetailsquestionIdApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/partneranswerDetails")
    Call<ResponseBody> partneranswerDetailsApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/termsCondition")
    Call<ResponseBody> termsConditionApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/addappFeedback")
    Call<ResponseBody> addappFeedbackWithOutImage(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/quizStatus")
    Call<ResponseBody> getQuizStatus(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("DatingController/blindStatus")
    Call<ResponseBody> updateBlindStatus(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("DatingController/blindDates")
    Call<ResponseBody> getBlindDates(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("DatingController/autmatchlist")
    Call<ResponseBody> getAutoMatchList(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("DatingController/automatch")
    Call<ResponseBody> callAutoMatch(@FieldMap Map<String, String> map);

    @Multipart
    @POST("api/send_attachment_web")
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part timeLineImage,
            @Part("lang") RequestBody description,
            @Part("keep_orig") RequestBody keepOrig
    );

    @FormUrlEncoded
    @POST("DatingController/register_device")
    Call<ResponseBody> registerDevice(@FieldMap Map<String, String> map);
}
