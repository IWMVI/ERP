package iwmvi.erp.shared.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImagemStorageService {

    private static final long TAMANHO_MAXIMO = 5L * 1024 * 1024;
    private static final int DIMENSAO_MAXIMA = 8000;
    private static final long PIXELS_MAXIMOS = 25_000_000L;
    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png");
    private static final Set<String> CATEGORIAS_PERMITIDAS = Set.of("produtos", "funcionarios");

    private final Path diretorioBase;

    public ImagemStorageService(@Value("${erp.upload-dir:uploads}") String uploadDir) {
        this.diretorioBase = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public String salvar(MultipartFile arquivo, String categoria) {
        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        validarCategoria(categoria);
        validarMetadadosBasicos(arquivo);

        byte[] bytes = lerBytes(arquivo);
        validarAssinatura(bytes);
        ImagemValidada imagemValidada = decodificarEValidar(bytes);

        String extensao = imagemValidada.formato().equals("png") ? ".png" : ".jpg";
        String nomeArquivo = UUID.randomUUID() + extensao;
        Path diretorio = diretorioSeguro(categoria);
        Path destino = diretorio.resolve(nomeArquivo).normalize();

        try {
            Files.createDirectories(diretorio);
            Path temporario = Files.createTempFile(diretorio, ".upload-", ".tmp");
            try {
                boolean gravou =
                    ImageIO.write(
                        prepararParaGravacao(imagemValidada.imagem(), imagemValidada.formato()),
                        imagemValidada.formato(),
                        temporario.toFile());
                if (!gravou) {
                    throw new IllegalArgumentException("Formato de imagem não suportado pelo servidor.");
                }

                moverComSeguranca(temporario, destino);
            } finally {
                Files.deleteIfExists(temporario);
            }
            return categoria + "/" + nomeArquivo;
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível salvar a imagem.", exception);
        }
    }

    public RecursoImagem carregar(String categoria, String arquivo) {
        validarCategoria(categoria);
        if (arquivo == null || !arquivo.matches("^[0-9a-fA-F-]{36}\\.(jpg|png)$")) {
            throw new IllegalArgumentException("Nome de imagem inválido.");
        }

        Path destino = diretorioSeguro(categoria).resolve(arquivo).normalize();
        if (!destino.startsWith(diretorioBase) || !Files.isRegularFile(destino)) {
            throw new IllegalArgumentException("Imagem não encontrada.");
        }

        try {
            Resource resource = new UrlResource(destino.toUri());
            String contentType = arquivo.endsWith(".png") ? "image/png" : "image/jpeg";
            return new RecursoImagem(resource, contentType);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível carregar a imagem.", exception);
        }
    }

    public void remover(String arquivo) {
        if (arquivo == null || arquivo.isBlank()) {
            return;
        }

        String[] partes = arquivo.split("/", 2);
        if (partes.length != 2 || !CATEGORIAS_PERMITIDAS.contains(partes[0])) {
            return;
        }

        Path destino = diretorioSeguro(partes[0]).resolve(partes[1]).normalize();
        if (!destino.startsWith(diretorioBase)) {
            return;
        }

        try {
            Files.deleteIfExists(destino);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível remover a imagem anterior.", exception);
        }
    }

    private void validarCategoria(String categoria) {
        if (!CATEGORIAS_PERMITIDAS.contains(categoria)) {
            throw new IllegalArgumentException("Categoria de upload inválida.");
        }
    }

    private void validarMetadadosBasicos(MultipartFile arquivo) {
        if (arquivo.getSize() <= 0 || arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("A imagem deve ter no máximo 5 MB.");
        }

        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal == null || nomeOriginal.length() > 200 || nomeOriginal.contains("\0")) {
            throw new IllegalArgumentException("Nome de arquivo inválido.");
        }

        String nomeSeguro = Path.of(nomeOriginal).getFileName().toString();
        if (!nomeSeguro.equals(nomeOriginal)) {
            throw new IllegalArgumentException("Nome de arquivo inválido.");
        }

        int ponto = nomeSeguro.lastIndexOf('.');
        if (ponto <= 0 || ponto == nomeSeguro.length() - 1) {
            throw new IllegalArgumentException("A imagem deve possuir extensão JPG ou PNG.");
        }

        String extensao = nomeSeguro.substring(ponto + 1).toLowerCase(Locale.ROOT);
        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw new IllegalArgumentException("Envie uma imagem JPG ou PNG.");
        }

        String contentType = arquivo.getContentType();
        if (contentType != null && !TIPOS_PERMITIDOS.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("O tipo informado para o arquivo não é permitido.");
        }
    }

    private byte[] lerBytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível ler o arquivo enviado.", exception);
        }
    }

    private void validarAssinatura(byte[] bytes) {
        boolean jpeg =
            bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF;

        boolean png =
            bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47
                && bytes[4] == 0x0D
                && bytes[5] == 0x0A
                && bytes[6] == 0x1A
                && bytes[7] == 0x0A;

        if (!jpeg && !png) {
            throw new IllegalArgumentException(
                "O conteúdo enviado não corresponde a uma imagem JPG ou PNG válida.");
        }
    }

    private ImagemValidada decodificarEValidar(byte[] bytes) {
        try (ImageInputStream stream =
                 ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            if (stream == null) {
                throw new IllegalArgumentException("Imagem inválida.");
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                throw new IllegalArgumentException("O arquivo enviado não é uma imagem suportada.");
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                String formato = normalizarFormato(reader.getFormatName());
                if (!Set.of("jpg", "png").contains(formato)) {
                    throw new IllegalArgumentException("Apenas imagens JPG e PNG são permitidas.");
                }

                int largura = reader.getWidth(0);
                int altura = reader.getHeight(0);
                validarDimensoes(largura, altura);

                BufferedImage imagem = reader.read(0);
                if (imagem == null) {
                    throw new IllegalArgumentException("Não foi possível decodificar a imagem.");
                }

                return new ImagemValidada(imagem, formato);
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Imagem corrompida ou inválida.", exception);
        }
    }

    private void validarDimensoes(int largura, int altura) {
        if (largura <= 0
            || altura <= 0
            || largura > DIMENSAO_MAXIMA
            || altura > DIMENSAO_MAXIMA
            || (long) largura * altura > PIXELS_MAXIMOS) {
            throw new IllegalArgumentException("As dimensões da imagem excedem o limite permitido.");
        }
    }

    private BufferedImage prepararParaGravacao(BufferedImage original, String formato) {
        if (!formato.equals("jpg") || original.getType() == BufferedImage.TYPE_INT_RGB) {
            return original;
        }

        BufferedImage rgb =
            new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rgb.createGraphics();
        try {
            graphics.drawImage(original, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return rgb;
    }

    private String normalizarFormato(String formato) {
        String normalizado = formato.toLowerCase(Locale.ROOT);
        return normalizado.equals("jpeg") ? "jpg" : normalizado;
    }

    private Path diretorioSeguro(String categoria) {
        Path diretorio = diretorioBase.resolve(categoria).normalize();
        if (!diretorio.startsWith(diretorioBase)) {
            throw new IllegalArgumentException("Caminho de upload inválido.");
        }
        return diretorio;
    }

    private void moverComSeguranca(Path origem, Path destino) throws IOException {
        try {
            Files.move(origem, destino, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(origem, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private record ImagemValidada(BufferedImage imagem, String formato) {
    }

    public record RecursoImagem(Resource resource, String contentType) {
    }
}
