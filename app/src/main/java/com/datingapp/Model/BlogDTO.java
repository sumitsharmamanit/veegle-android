package com.datingapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlogDTO {
    @SerializedName("blogimage")
    @Expose
    private String blogimage;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("blog_user_id")
    @Expose
    private String blogUserId;
    @SerializedName("hobbies_id")
    @Expose
    private String hobbiesId;
    @SerializedName("hobbies")
    @Expose
    private String hobbies;
    @SerializedName("hobbies_image")
    @Expose
    private String hobbiesImage;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("question_id")
    @Expose
    private String questionId;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("quesimage")
    @Expose
    private String quesimage;

    @SerializedName("ethnicity")
    @Expose
    private String ethnicity;
    @SerializedName("Orientation")
    @Expose
    private String Orientation;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("answer")
    @Expose
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getOrientation() {
        return Orientation;
    }

    public void setOrientation(String orientation) {
        Orientation = orientation;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuesimage() {
        return quesimage;
    }

    public void setQuesimage(String quesimage) {
        this.quesimage = quesimage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHobbiesId() {
        return hobbiesId;
    }

    public void setHobbiesId(String hobbiesId) {
        this.hobbiesId = hobbiesId;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getHobbiesImage() {
        return hobbiesImage;
    }

    public void setHobbiesImage(String hobbiesImage) {
        this.hobbiesImage = hobbiesImage;
    }

    public String getBlogimage() {
        return blogimage;
    }

    public void setBlogimage(String blogimage) {
        this.blogimage = blogimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBlogUserId() {
        return blogUserId;
    }

    public void setBlogUserId(String blogUserId) {
        this.blogUserId = blogUserId;
    }

    @Override
    public String toString() {
        return name;
    }
}