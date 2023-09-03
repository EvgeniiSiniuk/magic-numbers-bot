package org.siniuk.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class UserProfile {

    private Long userId;
    private String userName;
    private SubscriptionType subscriptionType;
    private Date subscriptionDate;
    private Date subscriptionValidTill;
    private int requestsLeft;
}
