package inc.yowyob.service.media.api.exceptions;

public class ServiceTypeNotFoundException extends RuntimeException {
    public ServiceTypeNotFoundException(String value) {
        super(String.format("Media type %s not found.", value));
    }
}
