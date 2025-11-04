package inc.yowyob.service.media.application.services;

import inc.yowyob.service.media.infrastructure.persistence.entities.Media;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MediaService {

    /**
     * Creates a new Media.
     *
     * @param filePart the file
     * @param service  the Media object to be created
     * @return the created Media object
     */
    public Mono<Media> uploadMedia(FilePart filePart, String service, String location);

    /**
     * Upload multiple media.
     *
     * @param fileParts the file
     * @param service   the Media object to be created
     * @return the created Media object
     */
    public Flux<Media> uploadMultipleMedia(Flux<FilePart> fileParts, String service, String location);

    /**
     * Replace media
     *
     * @param id       the media id
     * @param filePart the new media file
     * @return the created media
     */
    public Mono<Media> replaceMedia(UUID id, FilePart filePart, String location);

    /**
     * Download media
     *
     * @param id the media id
     * @return the media
     */
    public Mono<byte[]> downloadMedia(UUID id);

    /**
     * Get media url for a given ID
     *
     * @param id              the media id
     * @param expiryInSeconds the expiry period
     * @return the url
     */
    public Mono<String> getMediaUrl(UUID id, int expiryInSeconds);

    /**
     * Retrieves a Media by its ID.
     *
     * @param id the ID of the Media to retrieve
     * @return the found Media object, or null if not found
     */
    public Mono<Media> getMediaMetadata(UUID id);

    /**
     * Retrieves all Medias.
     *
     * @return a list of all Media objects
     */
    public Flux<Media> listMedia(String service);

    /**
     * Deletes a Media by its ID.
     *
     * @param id the ID of the Media to delete
     */
    public Mono<Void> deleteMedia(UUID id);

    /**
     * Delete media by path
     *
     * @param path the media path
     * @return void
     */
    public Mono<Void> deleteMediaByPath(String path);

    public Flux<Media> searchMediaByName(String service, String name);

    public Mono<Media> findByPath(String service, String path);

}
