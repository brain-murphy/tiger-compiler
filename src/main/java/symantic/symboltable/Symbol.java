package symantic.symboltable;

import org.w3c.dom.Attr;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brian on 10/20/2016.
 */
public class Symbol {
    private final Map<Attribute, Object> attributes;
    private final String name;

    public Symbol(String name) {
        this.attributes = new HashMap<>();
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
