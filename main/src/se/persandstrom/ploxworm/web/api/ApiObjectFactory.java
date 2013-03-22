package se.persandstrom.ploxworm.web.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.web.api.objects.Match;
import se.persandstrom.ploxworm.web.api.objects.MatchRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ApiObjectFactory {

    public static final String TYPE = "type";
    public static final String DaTa = "data";

    public static final String TYPE_PUT_IN_UEUE = "put_in_ueue";//TODO renme
    public static final String TYPE_FRME = "frame";//TODO renme

    private final Gson gson = new Gson();

    public String getType(JsonObject jsonObject) {
        return jsonObject.get(TYPE).getAsString();
    }

    public Class getTypeClass(JsonObject jsonObject) {
        String type = jsonObject.get(TYPE).getAsString();

        switch (type) {
            case MatchRequest.TYPE:
                return MatchRequest.class;
            default:
                throw new IllegalStateException("unknown type");
        }
    }

    public <T> T getApiObject(JsonObject jsonObject, Class<T> objectClass) {
        JsonElement data = jsonObject.get(DaTa);
        return gson.fromJson(data, objectClass);
    }

    public JsonObject createApiObject(String type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, type);
        return jsonObject;
    }

    public JsonObject createApiObject(Match match) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(TYPE, Match.TYPE);
        jsonObject.add(DaTa, gson.toJsonTree(match));//TODO renme dt du vet tngentordet är sönder
        return jsonObject;
    }
}
