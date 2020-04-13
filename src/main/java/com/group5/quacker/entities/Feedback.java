package com.group5.quacker.entities;

import com.group5.quacker.enums.FeedbackCategory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User sender;

    @Getter
    @Setter
    private String header;

    @Lob
    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private FeedbackCategory category;

    @Getter
    @Setter
    private Date date;
}
