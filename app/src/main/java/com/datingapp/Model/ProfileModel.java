package com.datingapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abhisheik on 5/3/18.
 */

public class ProfileModel implements Serializable {

    @SerializedName("useremail")
    @Expose
    private String useremail;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("userfullname")
    @Expose
    private String userfullname;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("children")
    @Expose
    private String children;

    @SerializedName("ethnicity")
    @Expose
    private String ethnicity;

    @SerializedName("bodytype")
    @Expose
    private String bodytype;
    @SerializedName("profilevisit")
    @Expose
    private String profilevisit;

    @SerializedName("oreintation")
    @Expose
    private String oreintation;

    @SerializedName("aboutme")
    @Expose
    private String aboutme;


    @SerializedName("work")
    @Expose
    private String work;

    @SerializedName("hobbies")
    @Expose
    private String hobbies;
    @SerializedName("generalroutine")
    @Expose
    private String generalroutine;
    @SerializedName("weekendroutine")
    @Expose
    private String weekendroutine;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("height")
    @Expose
    private String height;

    @SerializedName("weight")
    @Expose
    private String weight;

    @SerializedName("activestatus")
    @Expose
    private String activestatus;
    @SerializedName("is_online")
    @Expose
    private String isOnline;
    @SerializedName("intrested_in")
    @Expose
    private String intrestedIn;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("userlat")
    @Expose
    private String userlat;
    @SerializedName("education")
    @Expose
    private String education;
    @SerializedName("userlong")
    @Expose
    private String userlong;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("interest")
    @Expose
    private String interest;
    @SerializedName("social_type")
    @Expose
    private String socialType;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("likeStatus")
    @Expose
    private String likeStatus;


    @SerializedName("maternitystatus")
    @Expose
    private String maternitystatus;
    @SerializedName("lookingfor")
    @Expose
    private String lookingfor;

    @SerializedName("UserPics")
    @Expose
    private List<UserPic> userPics = null;

    @SerializedName("crosspath")
    @Expose
    private List<CrossPath> crossPath = null;

    @SerializedName("friends")
    @Expose
    private List<Friends> friends = null;

    @SerializedName("percentage")
    @Expose
    private String percentage;

    @SerializedName("created_date")
    @Expose
    private String createdDate;

    @SerializedName("social_id")
    @Expose
    private String socialId;
    @SerializedName("blockStatus")
    @Expose
    private String blockStatus;

    @SerializedName("block_user_id")
    @Expose
    private String blockUserId;

    @SerializedName("men")
    @Expose
    private String men;
    @SerializedName("women")
    @Expose
    private String women;
    @SerializedName("toage")
    @Expose
    private String toage;
    @SerializedName("fromage")
    @Expose
    private String fromage;
    @SerializedName("todistance")
    @Expose
    private String todistance;
    @SerializedName("hi")
    @Expose
    private String hi;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("crushes")
    @Expose
    private String crushes;
    @SerializedName("automatch")
    @Expose
    private String automatch;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("instagram")
    @Expose
    private String instagram;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("deactiveaccount")
    @Expose
    private String deactiveAccount;
    @SerializedName("pauseeighthours")
    @Expose
    private String pauseeighthours;

    @SerializedName("speedometer")
    @Expose
    private String speedometer;

    @SerializedName("kmormiles")
    @Expose
    private String kmormiles;

    @SerializedName("distancebetweenusers")
    @Expose
    private String distancebetweenusers;
    @SerializedName("updatedOn")
    @Expose
    private String updatedOn;

    @SerializedName("populerity")
    @Expose
    private String populerity;

    @SerializedName("block")
    @Expose
    private String block;
    @SerializedName("visituser")
    @Expose
    private String visituser;
    @SerializedName("invite")
    @Expose
    private String invite;
    @SerializedName("likeuser")
    @Expose
    private String likeuser;
    @SerializedName("notificationoff")
    @Expose
    private String notificationoff;
    @SerializedName("match")
    @Expose
    private String match;
    @SerializedName("inviteunique")
    @Expose
    private String inviteunique;

    @SerializedName("likeStatusnearby")
    @Expose
    private String likeStatusnearby;

    @SerializedName("invition_status")
    @Expose
    private String invitionStatus;

    @SerializedName("online_half_hour")
    @Expose
    private String onlineHalfHour;

    @SerializedName("visituseremail")
    @Expose
    private String visitUserEmail;
    @SerializedName("matchemail")
    @Expose
    private String matchEmail;
    @SerializedName("blockemail")
    @Expose
    private String blockEmail;
    @SerializedName("inviteemail")
    @Expose
    private String inviteEmail;
    @SerializedName("likeuseremail")
    @Expose
    private String likeUserEmail;
    @SerializedName("country")
    @Expose
    private String country;

    public String getProfilevisit() {
        return profilevisit;
    }

    public void setProfilevisit(String profilevisit) {
        this.profilevisit = profilevisit;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getVisitUserEmail() {
        return visitUserEmail;
    }

    public void setVisitUserEmail(String visitUserEmail) {
        this.visitUserEmail = visitUserEmail;
    }

    public String getMatchEmail() {
        return matchEmail;
    }

    public void setMatchEmail(String matchEmail) {
        this.matchEmail = matchEmail;
    }

    public String getBlockEmail() {
        return blockEmail;
    }

    public void setBlockEmail(String blockEmail) {
        this.blockEmail = blockEmail;
    }

    public String getInviteEmail() {
        return inviteEmail;
    }

    public void setInviteEmail(String inviteEmail) {
        this.inviteEmail = inviteEmail;
    }

    public String getLikeUserEmail() {
        return likeUserEmail;
    }

    public void setLikeUserEmail(String likeUserEmail) {
        this.likeUserEmail = likeUserEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOnlineHalfHour() {
        return onlineHalfHour;
    }

    public void setOnlineHalfHour(String onlineHalfHour) {
        this.onlineHalfHour = onlineHalfHour;
    }

    public String getInvitionStatus() {
        return invitionStatus;
    }

    public void setInvitionStatus(String invitionStatus) {
        this.invitionStatus = invitionStatus;
    }

    public String getLikeStatusnearby() {
        return likeStatusnearby;
    }

    public void setLikeStatusnearby(String likeStatusnearby) {
        this.likeStatusnearby = likeStatusnearby;
    }

    public String getInviteunique() {
        return inviteunique;
    }

    public void setInviteunique(String inviteunique) {
        this.inviteunique = inviteunique;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getVisituser() {
        return visituser;
    }

    public void setVisituser(String visituser) {
        this.visituser = visituser;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public String getLikeuser() {
        return likeuser;
    }

    public void setLikeuser(String likeuser) {
        this.likeuser = likeuser;
    }

    public String getNotificationoff() {
        return notificationoff;
    }

    public void setNotificationoff(String notificationoff) {
        this.notificationoff = notificationoff;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getPopulerity() {
        return populerity;
    }

    public void setPopulerity(String populerity) {
        this.populerity = populerity;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getDistancebetweenusers() {
        return distancebetweenusers;
    }

    public void setDistancebetweenusers(String distancebetweenusers) {
        this.distancebetweenusers = distancebetweenusers;
    }

    public String getKmormiles() {
        return kmormiles;
    }

    public void setKmormiles(String kmormiles) {
        this.kmormiles = kmormiles;
    }

    public String getSpeedometer() {
        return speedometer;
    }

    public void setSpeedometer(String speedometer) {
        this.speedometer = speedometer;
    }

    public String getPauseeighthours() {
        return pauseeighthours;
    }

    public void setPauseeighthours(String pauseeighthours) {
        this.pauseeighthours = pauseeighthours;
    }

    public String getDeactiveAccount() {
        return deactiveAccount;
    }

    public void setDeactiveAccount(String deactiveAccount) {
        this.deactiveAccount = deactiveAccount;
    }

    public String getMen() {
        return men;
    }

    public void setMen(String men) {
        this.men = men;
    }

    public String getWomen() {
        return women;
    }

    public void setWomen(String women) {
        this.women = women;
    }

    public String getToage() {
        return toage;
    }

    public void setToage(String toage) {
        this.toage = toage;
    }

    public String getFromage() {
        return fromage;
    }

    public void setFromage(String fromage) {
        this.fromage = fromage;
    }

    public String getTodistance() {
        return todistance;
    }

    public void setTodistance(String todistance) {
        this.todistance = todistance;
    }

    public String getHi() {
        return hi;
    }

    public void setHi(String hi) {
        this.hi = hi;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCrushes() {
        return crushes;
    }

    public void setCrushes(String crushes) {
        this.crushes = crushes;
    }

    public String getAutomatch() {
        return automatch;
    }

    public void setAutomatch(String automatch) {
        this.automatch = automatch;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getBlockUserId() {
        return blockUserId;
    }

    public void setBlockUserId(String blockUserId) {
        this.blockUserId = blockUserId;
    }

    public String getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(String blockStatus) {
        this.blockStatus = blockStatus;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getMaternitystatus() {
        return maternitystatus;
    }

    public void setMaternitystatus(String maternitystatus) {
        this.maternitystatus = maternitystatus;
    }

    public String getLookingfor() {
        return lookingfor;
    }

    public void setLookingfor(String lookingfor) {
        this.lookingfor = lookingfor;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getIntrestedIn() {
        return intrestedIn;
    }

    public void setIntrestedIn(String intrestedIn) {
        this.intrestedIn = intrestedIn;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserlat() {
        return userlat;
    }

    public void setUserlat(String userlat) {
        this.userlat = userlat;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getUserlong() {
        return userlong;
    }

    public void setUserlong(String userlong) {
        this.userlong = userlong;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUserfullname() {
        return userfullname;
    }

    public void setUserfullname(String userfullname) {
        this.userfullname = userfullname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getBodytype() {
        return bodytype;
    }

    public void setBodytype(String bodytype) {
        this.bodytype = bodytype;
    }

    public String getOreintation() {
        return oreintation;
    }

    public void setOreintation(String oreintation) {
        this.oreintation = oreintation;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getGeneralroutine() {
        return generalroutine;
    }

    public void setGeneralroutine(String generalroutine) {
        this.generalroutine = generalroutine;
    }

    public String getWeekendroutine() {
        return weekendroutine;
    }

    public void setWeekendroutine(String weekendroutine) {
        this.weekendroutine = weekendroutine;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getActivestatus() {
        return activestatus;
    }

    public void setActivestatus(String activestatus) {
        this.activestatus = activestatus;
    }


    public List<UserPic> getUserPics() {
        return userPics;
    }

    public void setUserPics(List<UserPic> userPics) {
        this.userPics = userPics;
    }

    public List<Friends> getFriends() {
        return friends;
    }

    public void setFriends(List<Friends> friends) {
        this.friends = friends;
    }

    public List<CrossPath> getCrossPath() {
        return crossPath;
    }

    public void setCrossPath(List<CrossPath> crossPath) {
        this.crossPath = crossPath;
    }

    public class CrossPath implements Serializable{

        @SerializedName("userlat")
        @Expose
        private String userLat;
        @SerializedName("userlong")
        @Expose
        private String userLong;
        @SerializedName("profile_image")
        @Expose
        private String profileImage;
        @SerializedName("social_type")
        @Expose
        private String socialType;

        public String getSocialType() {
            return socialType;
        }

        public void setSocialType(String socialType) {
            this.socialType = socialType;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getUserLat() {
            return userLat;
        }

        public void setUserLat(String userLat) {
            this.userLat = userLat;
        }

        public String getUserLong() {
            return userLong;
        }

        public void setUserLong(String userLong) {
            this.userLong = userLong;
        }
    }

    public class UserPic implements Serializable{

        @SerializedName("picId")
        @Expose
        private String picId;
        @SerializedName("pic_url")
        @Expose
        private String picUrl;

        public String getPicId() {
            return picId;
        }

        public void setPicId(String picId) {
            this.picId = picId;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

    }

    public class Friends implements Serializable{

        @SerializedName("profile_image")
        @Expose
        private String profileImage;
        @SerializedName("userfullname")
        @Expose
        private String userfullname;
        @SerializedName("user_id")
        @Expose
        private String userId;

        @SerializedName("timelineimage")
        @Expose
        private String timelineimage;

        public String getTimelineimage() {
            return timelineimage;
        }

        public void setTimelineimage(String timelineimage) {
            this.timelineimage = timelineimage;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getUserfullname() {
            return userfullname;
        }

        public void setUserfullname(String userfullname) {
            this.userfullname = userfullname;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    @Override
    public String toString() {
        return "ProfileModel{" +
                "useremail='" + useremail + '\'' +
                ", user_id='" + user_id + '\'' +
                ", userfullname='" + userfullname + '\'' +
                ", dob='" + dob + '\'' +
                ", address='" + address + '\'' +
                ", children='" + children + '\'' +
                ", intrested_in='" + intrestedIn + '\'' +
                ", ethnicity='" + ethnicity + '\'' +
                ", bodytype='" + bodytype + '\'' +
                ", oreintation='" + oreintation + '\'' +
                ", aboutme='" + aboutme + '\'' +
                ", work='" + work + '\'' +
                ", hobbies='" + hobbies + '\'' +
                ", generalroutine='" + generalroutine + '\'' +
                ", weekendroutine='" + weekendroutine + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", activestatus='" + activestatus + '\'' +
                ", is_online='" + isOnline + '\'' +
                ", profile_image='" + profileImage + '\'' +
                ", social_type='" + socialType + '\'' +
                '}';
    }
}
