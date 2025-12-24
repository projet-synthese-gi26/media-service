package inc.yowyob.service.media.application.services;

import inc.yowyob.service.media.api.exceptions.MinioException;
import io.minio.ObjectWriteResponse;
import io.minio.StatObjectResponse;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Item;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface MinioService {

    public Mono<Boolean> isBucketExist(String bucket);

    public Mono<Void> createBucket(String bucket) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException;

    /**
     * List all objects at root of the bucket
     *
     * @return List of items
     */
    public Flux<Item> list();

    /**
     * List all objects at root of the bucket
     *
     * @return List of items
     */
    public Flux<Item> list(String bucket);

    /**
     * List all objects at root of the bucket
     *
     * @return List of items
     */
    public Flux<Item> fullList();

    /**
     * List all objects at root of the bucket
     *
     * @return List of items
     */
    public Flux<Item> fullList(String bucket);

    /**
     * List all objects with the prefix given in parameter for the bucket.
     * Simulate a folder hierarchy. Objects within folders (i.e. all objects which match the pattern {@code {prefix}/{objectName}/...}) are not returned
     *
     * @param path Prefix of seeked list of object
     * @return List of items
     */
    public Flux<Item> list(Path path);

    /**
     * List all objects with the prefix given in parameter for the bucket.
     * Simulate a folder hierarchy. Objects within folders (i.e. all objects which match the pattern {@code {prefix}/{objectName}/...}) are not returned
     *
     * @param path Prefix of seeked list of object
     * @return List of items
     */
    public Flux<Item> list(Path path, String bucket);

    /**
     * List all objects with the prefix given in parameter for the bucket
     * <p>
     * All objects, even those which are in a folder are returned.
     *
     * @param path Prefix of seeked list of object
     * @return List of items
     */
    public Flux<Item> getFullList(Path path);

    /**
     * List all objects with the prefix given in parameter for the bucket
     * <p>
     * All objects, even those which are in a folder are returned.
     *
     * @param path Prefix of seeked list of object
     * @return List of items
     */
    public Flux<Item> getFullList(Path path, String bucket);

    /**
     * Get an object from Minio
     *
     * @param path Path with prefix to the object. Object name must be included.
     * @return The object as an InputStream
     * @throws MinioException if an error occur while fetch object
     */
    public Mono<InputStream> get(Path path) throws MinioException;

    /**
     * Get an object from Minio
     *
     * @param path Path with prefix to the object. Object name must be included.
     * @return The object as an InputStream
     */
    public Mono<InputStream> get(Path path, String bucket);

    /**
     * Get metadata of an object from Minio
     *
     * @param path Path with prefix to the object. Object name must be included.
     * @return Metadata of the  object
     * @throws MinioException if an error occur while fetching object metadatas
     */
    public Mono<StatObjectResponse> getMetadata(Path path) throws MinioException;

    /**
     * Get metadata of an object from Minio
     *
     * @param path Path with prefix to the object. Object name must be included.
     * @return Metadata of the  object
     * @throws MinioException if an error occur while fetching object metadatas
     */
    public Mono<StatObjectResponse> getMetadata(Path path, String bucket) throws MinioException;


    /**
     * Get metadata for multiples objects from Minio
     *
     * @param paths Paths of all objects with prefix. Objects names must be included.
     * @return A map where all paths are keys and metadatas are values
     */
    public Mono<Map<Path, StatObjectResponse>> getMetadata(Iterable<Path> paths);

    /**
     * Get metadata for multiples objects from Minio
     *
     * @param paths Paths of all objects with prefix. Objects names must be included.
     * @return A map where all paths are keys and metadatas are values
     */
    public Mono<Map<Path, StatObjectResponse>> getMetadata(Iterable<Path> paths, String bucket);


    /**
     * Get a file from Minio, and save it in the {@code fileName} file
     *
     * @param source   Path with prefix to the object. Object name must be included.
     * @param fileName Filename
     */
    public Mono<Void> getAndSave(Path source, String fileName);

    /**
     * Get a file from Minio, and save it in the {@code fileName} file
     *
     * @param source   Path with prefix to the object. Object name must be included.
     * @param fileName Filename
     */
    public Mono<Void> getAndSave(Path source, String fileName, String bucket);

    /**
     * Upload a file to Minio
     *
     * @param source  Path with prefix to the object. Object name must be included.
     * @param file    File as an inputstream
     * @param headers Additional headers to put on the file. The map MUST be mutable. All custom headers will start with 'x-amz-meta-' prefix when fetched with {@code getMetadata()} method.
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Path> upload(Path source, InputStream file, Map<String, String> headers) throws MinioException;

    /**
     * Upload a file to Minio
     *
     * @param source  Path with prefix to the object. Object name must be included.
     * @param file    File as an inputstream
     * @param headers Additional headers to put on the file. The map MUST be mutable. All custom headers will start with 'x-amz-meta-' prefix when fetched with {@code getMetadata()} method.
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Path> upload(Path source, String bucket, InputStream file, Map<String, String> headers) throws MinioException;

    public Mono<Path> upload(Path source, String bucket, FilePart filePart);

    /**
     * Upload a file to Minio
     *
     * @param source Path with prefix to the object. Object name must be included.
     * @param file   File as an inputstream
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Void> upload(Path source, InputStream file) throws MinioException;

    /**
     * Upload a file to Minio
     *
     * @param source Path with prefix to the object. Object name must be included.
     * @param file   File as an inputstream
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Void> upload(Path source, String bucket, InputStream file) throws MinioException;


    /**
     * Upload a file to Minio
     *
     * @param source      Path with prefix to the object. Object name must be included.
     * @param file        File as an inputstream
     * @param contentType MIME type for the object
     * @param headers     Additional headers to put on the file. The map MUST be mutable
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Void> upload(Path source, InputStream file, String contentType, Map<String, String> headers) throws MinioException;

    /**
     * Upload a file to Minio
     *
     * @param source      Path with prefix to the object. Object name must be included.
     * @param file        File as an inputstream
     * @param contentType MIME type for the object
     * @param headers     Additional headers to put on the file. The map MUST be mutable
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Void> upload(Path source, String bucket, InputStream file, String contentType, Map<String, String> headers) throws MinioException;

    /**
     * Upload a file to Minio
     *
     * @param source      Path with prefix to the object. Object name must be included.
     * @param file        File as an inputstream
     * @param contentType MIME type for the object
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Void> upload(Path source, InputStream file, String contentType) throws MinioException;

    /**
     * Upload a file to Minio
     *
     * @param source      Path with prefix to the object. Object name must be included.
     * @param file        File as an inputstream
     * @param contentType MIME type for the object
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Void> upload(Path source, String bucket, InputStream file, String contentType) throws MinioException;


    /**
     * Upload a file to Minio
     * upload file bigger than Xmx size
     *
     * @param source Path with prefix to the object. Object name must be included.
     * @param file   File as an Filename
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Void> upload(Path source, File file) throws MinioException;

    /**
     * Upload a file to Minio
     * upload file bigger than Xmx size
     *
     * @param source Path with prefix to the object. Object name must be included.
     * @param file   File as an Filename
     * @throws MinioException if an error occur while uploading object
     */
    public Mono<Void> upload(Path source, String bucket, File file) throws MinioException;


    /**
     * Remove a file to Minio
     *
     * @param source Path with prefix to the object. Object name must be included.
     */
    public Mono<Void> remove(Path source);

    /**
     * Remove a file to Minio
     *
     * @param source Path with prefix to the object. Object name must be included.
     */
    public Mono<Void> remove(Path source, String bucket);

    public Mono<String> generatePresignedUrl(String service, String objectKey, int expiryInSeconds);
}
