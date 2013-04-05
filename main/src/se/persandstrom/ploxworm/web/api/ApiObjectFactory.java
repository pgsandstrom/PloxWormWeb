package se.persandstrom.ploxworm.web.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.web.api.objects.EndRound;
import se.persandstrom.ploxworm.web.api.objects.Match;
import se.persandstrom.ploxworm.web.api.objects.MatchRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ApiObjectFactory {

    public static final String TYPE = "type";
    public static final String DATA = "data";

    public static final String TYPE_PUT_IN_QUEUE = "put_in_queue";
    public static final String TYPE_FRAME = "frame";

    private final Gson gson = new Gson();

    public String getType(JsonObject jsonObject) {
        return jsonObject.get(TYPE).getAsString();
    }

    public Class getTypeClass(JsonObject jsonObject) {
        String type = jsonObject.get(TYPE).getAsString();

        if(MatchRequest.TYPE.equals(type)) {
            return MatchRequest.class;
        } else {
            //Use this when this is actually used lol...
//                throw new IllegalStateException("unknown type");
            return null;
        }
    }

    public <T> T getApiObject(JsonObject jsonObject, Class<T> objectClass) {
        JsonElement data = jsonObject.get(DATA);
        return gson.fromJson(data, objectClass);
    }

    public JsonObject createApiObject(String type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, type);
        return jsonObject;
    }

    public JsonObject createApiObject(String type, JsonElement json) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, type);
        jsonObject.add(DATA, json);
        return jsonObject;
    }

    public JsonObject createApiObject(Match match) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, Match.TYPE);
        jsonObject.add(DATA, gson.toJsonTree(match));
        return jsonObject;
    }

    public JsonObject createApiObject(EndRound endRound) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, EndRound.TYPE);
        jsonObject.add(DATA, gson.toJsonTree(endRound));
        return jsonObject;
    }
}
