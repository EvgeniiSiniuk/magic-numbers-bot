package org.siniuk.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache.
 * usersBotStates: user_id and user's bot state
 * usersProfileData: user_id  and user's profile data.
 */

public class UserDataCache {
    private Map<Long, BotState> usersBotStates = new HashMap<>();
    private Map<Long, String> usersProfileData = new HashMap<>();

    public void setUsersCurrentBotState(Long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.INIT_STATE;
        }

        return botState;
    }

    public String getBirthDate(int userId) {
        return usersProfileData.get(userId);
    }

    public void saveUserBirthDate(Long userId, String date) {
        usersProfileData.put(userId, date);
    }
}
