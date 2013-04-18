package se.persandstrom.ploxworm.web;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named("test")
@ApplicationScoped
public class Test implements Serializable {

    public String getTest() {
        return "alpha";
    }
}
