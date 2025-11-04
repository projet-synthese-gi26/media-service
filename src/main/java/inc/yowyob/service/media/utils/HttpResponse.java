package inc.yowyob.service.media.utils;

import org.springframework.http.HttpStatus;

public final class HttpResponse {

    private HttpResponse() {
    }

    /**
     * Crée une réponse de succès standard (HTTP 200 OK).
     * @param data La donnée à inclure dans la réponse.
     * @return Un ApiResponse configuré pour le succès.
     */
    public static <T> ApiResponse<T> createSuccessResponse(T data) {
        return ApiResponse.<T>builder()
                .httpStatusCode(HttpStatus.OK.value())
                .status("SUCCESS")
                .message("Request processed successfully")
                .data(data)
                .build();
    }

    /**
     * Crée une réponse de succès avec un message personnalisé (HTTP 200 OK).
     * @param data La donnée à inclure.
     * @param message Le message personnalisé.
     * @return Un ApiResponse configuré pour le succès.
     */
    public static <T> ApiResponse<T> createSuccessResponse(T data, String message) {
        return ApiResponse.<T>builder()
                .httpStatusCode(HttpStatus.OK.value())
                .status("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Crée une réponse d'erreur standard.
     * C'est la méthode utilisée dans votre GlobalExceptionHandler.
     * @param httpStatus Le statut HTTP de l'erreur (ex: NOT_FOUND, INTERNAL_SERVER_ERROR).
     * @param message Le message d'erreur.
     * @return Un ApiResponse configuré pour une erreur (sans données).
     */
    public static <T> ApiResponse<T> createBadResponse(HttpStatus httpStatus, String message) {
        return ApiResponse.<T>builder()
                .httpStatusCode(httpStatus.value())
                .status("ERROR")
                .message(message)
                .build();
    }
}
