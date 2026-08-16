package iwmvi.erp.shared.storage;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/uploads")
public class ImagemController {

    private final ImagemStorageService imagemStorageService;

    public ImagemController(ImagemStorageService imagemStorageService) {
        this.imagemStorageService = imagemStorageService;
    }

    @GetMapping("/{categoria}/{arquivo:.+}")
    public ResponseEntity<?> carregar(
            @PathVariable String categoria, @PathVariable String arquivo) {
        ImagemStorageService.RecursoImagem imagem = imagemStorageService.carregar(categoria, arquivo);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagem.contentType()))
                .cacheControl(CacheControl.noCache())
                .header(HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(imagem.resource());
    }
}
