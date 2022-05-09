package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.Map;

public class ValidatedMap extends HashMap<String, Object> {
    @Override
    public Object put(final String key, final Object value) {
        validate(value);
        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends String, ?> m) {
        m.values().forEach(v -> validate(v));
        super.putAll(m);
    }

    private void validate(final Object value) {
        if (value instanceof String || value instanceof Integer ) {
            // OK
        } else {
            throw new RuntimeException("Illegal value type");
        }
    }
}
