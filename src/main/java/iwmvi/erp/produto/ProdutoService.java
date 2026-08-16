package iwmvi.erp.produto;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.shared.exception.CodigoProdutoJaCadastradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final AuditoriaService auditoriaService;

    public ProdutoService(ProdutoRepository repository, AuditoriaService auditoriaService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<Produto> listar(String termo) {
        if (termo == null || termo.isBlank()) {
            return repository.findAllByOrderByNomeAsc();
        }
        return repository.findByNomeContainingIgnoreCaseOrCodigoContainingIgnoreCaseOrderByNomeAsc(
                termo, termo);
    }

    @Transactional(readOnly = true)
    public Produto buscar(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }

    @Transactional
    public Produto criar(ProdutoRequest request) {
        validarCodigo(request.codigo(), null);
        Produto produto = repository.save(new Produto(request));
        auditoriaService.registrar(
                "CRIAR", "Produto", produto.getId(), produto.getCodigo() + " - " + produto.getNome());
        return produto;
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoRequest request) {
        validarCodigo(request.codigo(), id);
        Produto produto = buscar(id);
        produto.atualizar(request);
        auditoriaService.registrar(
                "ATUALIZAR", "Produto", id, produto.getCodigo() + " - " + produto.getNome());
        return produto;
    }

    @Transactional
    public void alternarAtivo(Long id) {
        Produto produto = buscar(id);
        produto.alternarAtivo();
        auditoriaService.registrar(
                produto.isAtivo() ? "ATIVAR" : "INATIVAR",
                "Produto",
                id,
                produto.getNome());
    }

    private void validarCodigo(String codigo, Long id) {
        boolean duplicado =
                id == null
                        ? repository.existsByCodigo(codigo)
                        : repository.existsByCodigoAndIdNot(codigo, id);
        if (duplicado) {
            throw new CodigoProdutoJaCadastradoException(codigo);
        }
    }
}
