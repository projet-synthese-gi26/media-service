package inc.yowyob.service.media.api.exceptions;


/**
 * Wrapper exception for all Minio errors that occurs while fetching, removing, uploading an object to Minio.
 * @author Jordan LEFEBURE
 */
public class MinioException extends Exception {
    public MinioException(String message, Throwable cause) {
        super(message, cause);
    }
}
