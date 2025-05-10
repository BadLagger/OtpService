package sf.mifi.grechko.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import sf.mifi.grechko.dto.UserRole;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<UserRole, String> {
    public String convertToDatabaseColumn(UserRole attribute) {
        return attribute.name().toUpperCase();
    }

    public UserRole convertToEntityAttribute(String dbData)  {
        return UserRole.valueOf(dbData.toUpperCase());
    }
}
