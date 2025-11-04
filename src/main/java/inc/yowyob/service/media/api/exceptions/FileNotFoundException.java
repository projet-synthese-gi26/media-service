package inc.yowyob.service.media.api.exceptions;


/**
 * Runtime exception thrown when an error occur while fetching a list of objects.
 * @author Jordan LEFEBURE
 */
public class FileNotFoundException extends RuntimeException{
    public FileNotFoundException(String name) {
        super(String.format("o file was found with name %s.", name));
    }
}
