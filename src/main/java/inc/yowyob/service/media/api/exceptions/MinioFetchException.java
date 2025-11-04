package inc.yowyob.service.media.api.exceptions;


/**
 * Runtime exception thrown when an error occur while fetching a list of objects.
 * @author Jordan LEFEBURE
 */
public class MinioFetchException extends RuntimeException{
    public MinioFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
