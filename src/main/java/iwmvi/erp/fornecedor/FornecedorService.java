package iwmvi.erp.fornecedor;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.integracao.ValidacaoCadastroService;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;
    private final AuditoriaService auditoriaService;
    private final ValidacaoCadastroService validacaoCadastroService;

    public FornecedorService(
            FornecedorRepository repository,
            AuditoriaService auditoriaService,
            ValidacaoCadastroService validacaoCadastroService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
        this.validacaoCadastroService = validacaoCadastroService;
    }

    @Transactional(readOnly = true)
    public List<Fornecedor> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Fornecedor buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado."));
    }

    @Transactional
    public Fornecedor criar(FornecedorRequest request) {
        validarCadastro(request, null);
        Fornecedor fornecedor = repository.save(new Fornecedor(request));
        auditoriaService.registrar("CRIAR", "Fornecedor", fornecedor.getId(), fornecedor.getNome());
        return fornecedor;
    }

    @Transactional
    public Fornecedor atualizar(Long id, FornecedorRequest request) {
        validarCadastro(request, id);
        Fornecedor fornecedor = buscar(id);
        fornecedor.atualizar(request);
        auditoriaService.registrar("ATUALIZAR", "Fornecedor", id, fornecedor.getNome());
        return fornecedor;
    }

    @Transactional
    public void alternarAtivo(Long id) {
        Fornecedor fornecedor = buscar(id);
        fornecedor.alternarAtivo();
        auditoriaService.registrar(fornecedor.isAtivo() ? "ATIVAR" : "INATIVAR", "Fornecedor", id, fornecedor.getNome());
    }

    private void validarCadastro(FornecedorRequest request, Long id) {
        validarDocumentoDuplicado(request.documento(), id);
        validacaoCadastroService.validarDocumento(request.documento());
        validacaoCadastroService.validarCep(request.cep());
    }

    private void validarDocumentoDuplicado(String documento, Long id) {
        boolean duplicado = id == null
                ? repository.existsByDocumento(documento)
                : repository.existsByDocumentoAndIdNot(documento, id);
        if (duplicado) {
            throw new DocumentoJaCadastradoException(documento);
        }
    }
}
