package com.datingapp.util;

public interface Constant {

    String APPTOKEN = "datingapp12345";
    String DEVICE_TYPE = "Android";
    String BASE = "http://veegleapp.com/DatingApp/";
    //        String BASE_URL = "http://freebizoffer.com/apptech/DatingApp/index.php/";
    String BASE_URL = "http://veegleapp.com/DatingApp/index.php/";

    //        String CHAT_SERVER_URL = "http://192.168.0.102:3000/";
    String CHAT_SERVER_URL = "http://veegleapp.com:3000/";
    //  String BASE_URL = "http://192.168.1.14/datingapp/";
    //    String BASE_URL = "http://spaculus.info/projects/12/veegle/";
    //  String CHAT_SERVER_URL = "http://192.168.1.29:3000/";
    //  String CHAT_SERVER_URL = "http://spaculus.info:5000";
    //        String IMAGE_SERVER_PATH = "http://freebizoffer.com//apptech//DatingApp//admin_assets//images//";
    // String IMAGE_SERVER_PATH = "http://veegleapp.com//DatingApp//admin_assets//images//";
    String IMAGE_SERVER_PATH = BASE + "admin_assets/images/";

    String CHAT_FILES_PATH = BASE + "admin_assets/upload_files/";

    String LOGIN_URL = BASE_URL + "DatingController/userLogin";
    String REGISTER_URL = BASE_URL + "DatingController/userRegister";
    String USER_APPROVED_URL = BASE_URL + "DatingController/userApproved";
    String UPLOAD_PROFILE_URL = BASE_URL + "DatingController/userImage";
    String FORGOT_PASSWORD_URL = BASE_URL + "DatingController/forgetuserpassword";
    String SOCIAL_LOGIN = BASE_URL + "DatingController/social_login";
    String NEAR_BY_USER = BASE_URL + "DatingController/nearByUsers";
    String LIKE_LIST = BASE_URL + "DatingController/likeList";
    String PROFILE_VISIT_LIST = BASE_URL + "DatingController/showprofilevisitedbyUser";
    String BLOCK_LIST = BASE_URL + "DatingController/showblockUser";
    String SHOW_BLOG = BASE_URL + "DatingController/showblogUser";
    String LIKE_USER = BASE_URL + "DatingController/like";
    String MULTIPLE_IMAGE = BASE_URL + "DatingController/uploadmultipleImage";
    String EDIT_MULTIPLE_IMAGE = BASE_URL + "DatingController/updatemultileImage";
    String GET_ETHNICITY = BASE_URL + "DatingController/ethnicity";
    String UPDATE_USER_PROFILE = BASE_URL + "DatingController/updateUserProfile";
    String SHOW_HOBBIES = BASE_URL + "DatingController/showHobbies";
    String ADD_BLOG = BASE_URL + "DatingController/addBlog";
    String UPDATE_BLOG = BASE_URL + "DatingController/updateBlog";
    String MATCH = BASE_URL + "DatingController/match";
    String AUTO_MATCH = BASE_URL + "DatingController/autmatchlist";
    String SHOW_QUESTION_LIST = BASE_URL + "DatingController/showQuestionlist";
    String ADD_QUESTION_ANSWER = BASE_URL + "DatingController/addQuestionsanswer";
    String INVITATION = BASE_URL + "DatingController/invition";
    String INVITATION_LIST = BASE_URL + "DatingController/invitionList";
    String USER_DETAILS = BASE_URL + "DatingController/userDetails";
    String BLOCK_USER = BASE_URL + "DatingController/blockUser";
    String REPORT_ABUSE = BASE_URL + "DatingController/reportAbuse";
    String ADD_TIMELINE = BASE_URL + "DatingController/addTimeline";
    String SHOW_TIMELINE = BASE_URL + "DatingController/showTimeline";
    String PROFILE_VISITED = BASE_URL + "DatingController/profilevisitedbyUser";
    String PARTNER_PREFERENCE = BASE_URL + "DatingController/showpartnerQuestionlist";
    String ADD_PARTNER_PREFERENCE_QUESTION = BASE_URL + "DatingController/addpartnerQuestionsanswer";
    String LOGOUT = BASE_URL + "DatingController/logout";
    String PREFERENCE = BASE_URL + "DatingController/preference";
    String NOTIFY_PREFERENCE = BASE_URL + "DatingController/notifpreference";
    String ADD_FEEDBACK = BASE_URL + "DatingController/addappFeedback";
    String ABOUT_APP = BASE_URL + "DatingController/aboutApp";
    String PERSONALITY_QUIZ_REPEAT = BASE_URL + "DatingController/answerDetailsquestionId";
    String PARTNER_PREFERENCE_REPEAT = BASE_URL + "DatingController/partneranswerDetails";
    String TERMS_CONDITION = BASE_URL + "DatingController/termsCondition";
    String REMOVE_INVITATION = BASE_URL + "DatingController/removeinvition";

//     String PAYPAL_CLIENT_ID = "AUmP-EU8KP-_OX1YWMkdcPttzSnPYtNdaVvCpBMzjEvaGhTqQPS0nY48MLZ_iSQN9xXUohhyKWcrTd4n";
//     String PAYPAL_CLIENT_ID = "AZGb8UgrECF0Z81tXiC9IDOStoCrpJ3KutDPHJ90nH1ZOB4OJERY4TuwMaIrs7V4z1REqLzncKlr_b4J";

    String PAYPAL_CLIENT_ID = "access_token$sandbox$dbgxc8s6n4ky7n2j$e07ad31b11d879f9a50d8a33dd432d2f";

    String SOCIALIMAGE = "";
    String IMAGETYPE = "1";

    String CLIENT_ID = "628b20a7011145c8b0c900bb202470f9";
    String CLIENT_SECRET = "a1150d37fc3b4c2aaf83cae74c212f89";
    String CALLBACK_URL = "http://veegleapp.com/DatingApp/";
//    String CALLBACK_URL = "http://apptechmobile.com/";


    //server
//      String CHAT_SERVER_URL = "http://ec2-54-193-77-74.us-west-1.compute.amazonaws.com:3000";


    int ACTIVITY_RESULT = 1001, ACTIVITY_FINISH = 1002,
            GALLERY = 111, CAMERA = 112, CROP = 113, GIF_GALLERY = 333;
    int VIDEO_GALLERY = 222;
    /**
     * Image Storage Path
     */
    String IMAGE_DIRECTORY = "/DCIM/PICTURES";
    String IMAGE_DIRECTORY_CROP = "/DCIM/CROP_PICTURES";


    //Chat Constants
    String msg_type = "msg_type";
    String file_name = "file_name";
    String origName = "orig_name";
    String imagetype = "IMAGE";
    String text = "TEXT";
    String image = "IMAGE";
    String video = "VIDEO";
    String audio = "AUDIO";
    String keep_orig = "keep_orig";
    String GIF = "GIF";
    String download = "download";
    String upload = "upload";
    String play = "play";
    String playGif = "playGif";
    String pause = "pause";
    String pauseGIF = "pauseGIF";
    String stop = "Stop";
    int RequestTakeVideo = 100;
    int RequestUploadVideo = 200;
    int RequestAudio = 300;
    int REQUEST_RECORD_AUDIO = 180;
    String error = "error";
    String success = "Success";
    String msg_id = "msg_id";
    String msg_id_prefix = "android";
}