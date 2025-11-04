package inc.yowyob.service.media.api.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FaviconController {

    @RequestMapping("favicon.ico")
    public ResponseEntity<?> favicon() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ClassPathResource("static/favicon.ico"));
    }
}
