package inc.yowyob.service.media.api.exceptions;

public class MediaTypeNotFoundException extends RuntimeException {
    public MediaTypeNotFoundException(String value) {
        super(String.format("Media type %s not found.", value));
    }
}
