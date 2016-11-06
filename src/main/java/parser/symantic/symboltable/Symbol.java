package parser.symantic.symboltable;

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

    public void putAttribute(Attribute attribute, Object value) {

    }
}
