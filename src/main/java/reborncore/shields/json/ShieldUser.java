package reborncore.shields.json;

import javax.annotation.Nullable;

/**
 * Created by Mark on 25/03/2016.
 */
public class ShieldUser {

    public String username;
    public
    @Nullable
    String textureName;

    public ShieldUser(String username) {
        this.username = username;
    }

    public ShieldUser() {
    }

}
