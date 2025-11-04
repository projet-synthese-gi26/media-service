package inc.yowyob.service.media.api.exceptions;

public class NoExtensionException extends RuntimeException {
    public NoExtensionException() {
        super("File must have an extension.");
    }
}
