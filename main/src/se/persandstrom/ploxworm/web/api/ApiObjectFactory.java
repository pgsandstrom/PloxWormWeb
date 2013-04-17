package se.persandstrom.ploxworm.web.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.web.api.objects.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ApiObjectFactory {

    public static final String TYPE = "type";
    public static final String DATA = "data";

    //types that dont have a corresponding class is placed here:
    public static final String TYPE_PUT_IN_QUEUE = "put_in_queue";
    public static final String TYPE_FRAME = "frame";
    public static final String HIDE_TITLE = "hide_title";
    public static final String HIDE_MESSAGE = "hide_message";

    private final Gson gson;
    private final Gson gsonExcludeNonExposed;

    public ApiObjectFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonExcludeNonExposed = gsonBuilder.create();

        gson = new Gson();
    }

    public String getType(JsonObject jsonObject) {
        return jsonObject.get(TYPE).getAsString();
    }

    public Class getTypeClass(JsonObject jsonObject) {
        String type = jsonObject.get(TYPE).getAsString();

        if (MatchRequest.TYPE.equals(type)) {
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

    public JsonObject createApiObject(AbstractApiObject apiObject) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, apiObject.getType());
        jsonObject.add(DATA, gson.toJsonTree(apiObject));
        return jsonObject;
    }
}
