package inc.yowyob.service.media.api.exceptions;


/**
 * Runtime exception thrown when an error occur while fetching a list of objects.
 * @author Jordan LEFEBURE
 */
public class BucketNotFoundException extends RuntimeException{
    public BucketNotFoundException(String name) {
        super(String.format("Storage %s not found.", name));
    }
}
