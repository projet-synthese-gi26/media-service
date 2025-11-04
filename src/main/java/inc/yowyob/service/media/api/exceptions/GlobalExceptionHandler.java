package inc.yowyob.service.media.api.exceptions;


//import io.swagger.v3.oas.annotations.Hidden;
import inc.yowyob.service.media.utils.ApiResponse;
import inc.yowyob.service.media.utils.HttpResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ApiResponse<Object> handleFileNotFoundException(FileNotFoundException e) {
        log.error("FileNotFoundException occurred", e);
        return HttpResponse.createBadResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MinioException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleDfsServerException(MinioException e) {
        log.error("MinioException occurred", e);
        return HttpResponse.createBadResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(NoExtensionException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleNoExtensionException(NoExtensionException e) {
        log.error("MinioException occurred", e);
        return HttpResponse.createBadResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(NoNameException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleNoNameException(NoNameException e) {
        log.error("MinioException occurred", e);
        return HttpResponse.createBadResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
