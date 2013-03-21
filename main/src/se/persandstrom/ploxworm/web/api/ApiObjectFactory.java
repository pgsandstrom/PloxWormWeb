package se.persandstrom.ploxworm.web.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.web.api.objects.MatchRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ApiObjectFactory {

    private final Gson gson = new Gson();

    public String getType(JsonObject jsonObject) {
        return jsonObject.get("type").getAsString();
    }

    public Class getTypeClass(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();

        switch(type) {
            case MatchRequest.REQUEST_TYPE:
                return MatchRequest.class;
            default:
                throw new IllegalStateException("unknown type");
        }
    }

    public <T> T getApiObject(JsonObject jsonObject, Class<T> objectClass) {
        JsonElement data = jsonObject.get("data");
        return gson.fromJson(data, objectClass);
    }
}
