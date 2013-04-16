package se.persandstrom.ploxworm.web.api.objects;

/**
 * User: Per Sandstrom
 * Date: 2013-04-16 13:36
 */
public class Title implements AbstractApiObject{

    public static final String TYPE = "show_title";

    final String message;

    public Title(String message) {
        this.message = message;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
