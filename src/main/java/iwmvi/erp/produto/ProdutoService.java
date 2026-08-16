package iwmvi.erp.produto;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.integracao.ValidacaoCadastroService;
import iwmvi.erp.shared.exception.CodigoProdutoJaCadastradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final AuditoriaService auditoriaService;
    private final ValidacaoCadastroService validacaoCadastroService;

    public ProdutoService(
        ProdutoRepository repository,
        AuditoriaService auditoriaService,
        ValidacaoCadastroService validacaoCadastroService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
        this.validacaoCadastroService = validacaoCadastroService;
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
        validarCadastro(request, null);
        Produto produto = repository.save(new Produto(request));
        auditoriaService.registrar(
            "CRIAR", "Produto", produto.getId(), produto.getCodigo() + " - " + produto.getNome());
        return produto;
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoRequest request) {
        validarCadastro(request, id);
        Produto produto = buscar(id);
        produto.atualizar(request);
        auditoriaService.registrar(
            "ATUALIZAR", "Produto", id, produto.getCodigo() + " - " + produto.getNome());
        return produto;
    }

    @Transactional
    public void atualizarFoto(Long id, String fotoArquivo) {
        Produto produto = buscar(id);
        produto.definirFotoArquivo(fotoArquivo);
        auditoriaService.registrar("ATUALIZAR_FOTO", "Produto", id, produto.getNome());
    }

    @Transactional
    public void alternarAtivo(Long id) {
        Produto produto = buscar(id);
        produto.alternarAtivo();
        auditoriaService.registrar(
            produto.isAtivo() ? "ATIVAR" : "INATIVAR", "Produto", id, produto.getNome());
    }

    private void validarCadastro(ProdutoRequest request, Long id) {
        validarCodigo(request.codigo(), id);
        validacaoCadastroService.validarGtin(request.gtin());
        if (request.estoqueMaximo() != null
            && request.estoqueMaximo().signum() > 0
            && request.estoqueMaximo().compareTo(request.estoqueMinimo()) < 0) {
            throw new IllegalArgumentException(
                "O estoque máximo não pode ser menor que o estoque mínimo.");
        }
        if (request.pesoLiquido() != null
            && request.pesoBruto() != null
            && request.pesoBruto().compareTo(request.pesoLiquido()) < 0) {
            throw new IllegalArgumentException("O peso bruto não pode ser menor que o peso líquido.");
        }
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
