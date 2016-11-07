package parser.semantic.symboltable;

import java.util.HashMap;
import java.util.Map;

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
        attributes.put(attribute, value);
    }

    public Object getAttribute(Attribute attribute) {
        return attributes.get(attribute);
    }
}
