package sf.mifi.grechko.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sf.mifi.grechko.dto.RegisterRequest;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    public User toEntity(UserDto userDto);
    @Mapping(target = "id", ignore = true)
    public User toEntity(RegisterRequest userRequest);
}
