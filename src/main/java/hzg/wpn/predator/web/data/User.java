package hzg.wpn.predator.web.data;

import javax.annotation.concurrent.Immutable;

/**
 * Represents user instance
 * <p/>
 * Created by khokhria
 * on 29.12.11
 */
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
