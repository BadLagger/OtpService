package sf.mifi.grechko.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sf.mifi.grechko.dto.OtpConfigDto;
import sf.mifi.grechko.entity.OtpConfig;

@Mapper(componentModel = "spring")
public interface OtpConfigMapper {
    OtpConfigDto toDto(OtpConfig entity);

    @Mapping(target = "id", ignore = true)
    OtpConfig toEntity(OtpConfigDto dto);
}
