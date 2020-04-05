package com.group5.quacker.models;

import com.group5.quacker.entities.Feedback;
import com.group5.quacker.entities.User;
import com.group5.quacker.enums.FeedbackCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class FeedbackForm {
    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private FeedbackCategory category;

    @Getter
    @Setter
    private String header;

    public Feedback getFeedback(User sender) {
       Feedback feedback = new Feedback();
       feedback.setCategory(this.category);
       feedback.setDate(new Date());
       feedback.setHeader(this.header);
       feedback.setMessage(this.message);
       feedback.setSender(sender);
       return feedback;
    }
}
