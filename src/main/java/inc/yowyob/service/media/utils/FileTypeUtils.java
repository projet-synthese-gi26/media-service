package inc.yowyob.service.media.utils;

import inc.yowyob.service.media.application.enums.MediaType;

import java.util.Locale;
import java.util.Set;

public class FileTypeUtils {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
    private static final Set<String> AUDIO_EXTENSIONS = Set.of("mp3", "wav", "aac", "ogg", "flac", "m4a");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "avi", "mov", "mkv", "webm", "flv");
    private static final Set<String> PDF_EXTENSIONS = Set.of("pdf");

    public static boolean isImage(String filename) {
        return hasExtension(filename, IMAGE_EXTENSIONS);
    }

    public static boolean isAudio(String filename) {
        return hasExtension(filename, AUDIO_EXTENSIONS);
    }

    public static boolean isVideo(String filename) {
        return hasExtension(filename, VIDEO_EXTENSIONS);
    }

    public static boolean isPdf(String filename) {
        return hasExtension(filename, PDF_EXTENSIONS);
    }

    private static boolean hasExtension(String filename, Set<String> validExtensions) {
        if (filename == null || !filename.contains(".")) return false;
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return validExtensions.contains(ext);
    }

    public static boolean hasExtension(String filename){
        return filename != null && filename.contains(".") && !filename.endsWith(".");
    }

    public static boolean isValid(String type, String fullPath){
        return switch (type.toLowerCase()) {
            case "image", "photo", "avatar" -> FileTypeUtils.isImage(fullPath);
            case "audio" -> FileTypeUtils.isAudio(fullPath);
            case "video" -> FileTypeUtils.isVideo(fullPath);
            case "pdf"   -> FileTypeUtils.isPdf(fullPath);
            default      -> false;
        };

    }

    public static MediaType resolveMediaType(String ext) {
        ext = ext.toLowerCase(Locale.ROOT);

        if (IMAGE_EXTENSIONS.contains(ext)) return MediaType.IMAGE;
        if (AUDIO_EXTENSIONS.contains(ext)) return MediaType.AUDIO;
        if (VIDEO_EXTENSIONS.contains(ext)) return MediaType.VIDEO;
        if (PDF_EXTENSIONS.contains(ext)) return MediaType.PDF;

        return MediaType.FILE;
    }
}
