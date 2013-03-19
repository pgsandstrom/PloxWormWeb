package se.persandstrom.ploxworm.web.api;

import com.google.gson.JsonObject;

/**
 * User: pesandst
 * Date: 2013-03-19
 * Time: 10:16
 */
public class ApiObjectFactory {

    public Object getApiObject(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();

        switch(type) {

        }
    }
}
