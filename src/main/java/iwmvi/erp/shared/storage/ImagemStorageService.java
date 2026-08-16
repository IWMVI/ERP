package iwmvi.erp.shared.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagemStorageService {

    private static final long TAMANHO_MAXIMO = 5L * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("image/jpeg", "image/png", "image/webp");

    private final Path diretorioBase;

    public ImagemStorageService(@Value("${erp.upload-dir:uploads}") String uploadDir) {
        this.diretorioBase = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public String salvar(MultipartFile arquivo, String categoria) {
        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        validar(arquivo);

        String extensao = extensao(arquivo.getOriginalFilename(), arquivo.getContentType());
        String nomeArquivo = UUID.randomUUID() + extensao;
        Path diretorio = diretorioBase.resolve(categoria).normalize();
        Path destino = diretorio.resolve(nomeArquivo).normalize();

        if (!destino.startsWith(diretorioBase)) {
            throw new IllegalArgumentException("Caminho de arquivo inválido.");
        }

        try {
            Files.createDirectories(diretorio);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return categoria + "/" + nomeArquivo;
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível salvar a imagem.", exception);
        }
    }

    public void remover(String arquivo) {
        if (arquivo == null || arquivo.isBlank()) {
            return;
        }

        Path destino = diretorioBase.resolve(arquivo).normalize();
        if (!destino.startsWith(diretorioBase)) {
            return;
        }

        try {
            Files.deleteIfExists(destino);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível remover a imagem anterior.", exception);
        }
    }

    private void validar(MultipartFile arquivo) {
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("A imagem deve ter no máximo 5 MB.");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Envie uma imagem JPG, PNG ou WEBP.");
        }
    }

    private String extensao(String nomeOriginal, String contentType) {
        if (nomeOriginal != null) {
            int indice = nomeOriginal.lastIndexOf('.');
            if (indice >= 0) {
                String extensao = nomeOriginal.substring(indice).toLowerCase(Locale.ROOT);
                if (Set.of(".jpg", ".jpeg", ".png", ".webp").contains(extensao)) {
                    return extensao;
                }
            }
        }

        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
