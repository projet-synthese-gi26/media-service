package inc.yowyob.service.media.api.dto.media;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class MediaDto extends EntityDto {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("service")
    private String service;

    @JsonProperty("name")
    private String name;

    @JsonProperty("real_name")
    private String realName;

    @JsonProperty("size")
    private Long size;

    @JsonProperty("mime")
    private String mime;

    @JsonProperty("extension")
    private String extension;

    @JsonProperty("path")
    private String path;

    @JsonProperty("uri")
    private String uri;


}
