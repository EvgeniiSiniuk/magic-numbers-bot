package org.siniuk.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserProfile {

    private Long userId;
    private String userName;
    private SubscriptionType subscriptionType;
    private Date subscriptionDate;
}
