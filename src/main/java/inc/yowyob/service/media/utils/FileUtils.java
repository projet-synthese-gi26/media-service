package inc.yowyob.service.media.utils;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;

public class FileUtils {

    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    public static String getBasename(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(0, filename.lastIndexOf("."));
    }

    public static String getContentType(FilePart filePart){
        MediaType mediaType = filePart.headers().getContentType();
        if(mediaType != null){
            return mediaType.toString();
        }
        return null;
    }

    public static String generateHashedFileName(String originalName) {
        String extension = getExtension(originalName);
        String baseName = getBasename(originalName);

        String timestamp = String.valueOf(System.currentTimeMillis());
        String toHash = baseName + "_" + timestamp;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(toHash.getBytes(StandardCharsets.UTF_8));
            String hashed = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
            return hashed + "." + extension;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    public static Mono<byte[]> convert(FilePart filePart) {
        return filePart.content().reduce(new byte[0], (acc, dataBuffer) -> {
            byte[] newAcc = new byte[acc.length + dataBuffer.readableByteCount()];
            System.arraycopy(acc, 0, newAcc, 0, acc.length);
            dataBuffer.read(newAcc);
            return newAcc;
        });
    }

    public static String sanitizeService(String service) {
        if (service == null || service.trim().isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be empty");
        }
        return service.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    public static String sanitizeLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return "";
        }
        return location.replaceAll("^/+|/+$|[^a-zA-Z0-9/_-]", "");
    }

}
