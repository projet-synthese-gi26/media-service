package inc.yowyob.service.media.api.exceptions;

public class NoNameException extends RuntimeException {
    public NoNameException() {
        super("File must have a name.");
    }
}
