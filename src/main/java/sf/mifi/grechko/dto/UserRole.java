package sf.mifi.grechko.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

class CaseInsensitiveEnumDeserializer extends StdDeserializer<UserRole> {

    protected CaseInsensitiveEnumDeserializer() {
        super(UserRole.class);
    }

    @Override
    public UserRole deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = p.readValueAs(String.class).trim().toUpperCase();
        return UserRole.valueOf(value);
    }
}

@JsonDeserialize(using = CaseInsensitiveEnumDeserializer.class)
public enum UserRole {
    ADMIN, USER
}
