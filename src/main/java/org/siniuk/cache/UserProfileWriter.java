package org.siniuk.cache;

import com.google.gson.Gson;
import org.siniuk.model.UserProfile;

import java.io.FileWriter;
import java.io.IOException;

public class UserProfileWriter {

    public static void writeFile(UserProfile profile) {
        Gson gson = new Gson();
        String json = gson.toJson(profile);

        // Write JSON to a file
        try (FileWriter writer = new FileWriter(profile.getUserId() + ".json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
