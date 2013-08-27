package wpn.hdri.web.data;

import net.jcip.annotations.Immutable;

/**
 * Represents user instance
 * <p/>
 * Created by khokhria
 * on 29.12.11
 */
//TODO remove
@Immutable
public class User {
    private final String name;

    User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
