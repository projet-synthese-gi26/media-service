package inc.yowyob.service.media.application.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.yowyob.service.media.api.exceptions.MediaTypeNotFoundException;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum MediaType {
    PDF("pdf"),
    FILE("file"),
    AUDIO("audio"),
    VIDEO("video"),
    IMAGE("image"),
    UNKNOWN("unknown");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MediaType fromValue(String value) {
        for (MediaType type : MediaType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new MediaTypeNotFoundException(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
