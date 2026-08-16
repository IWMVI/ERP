package iwmvi.erp.cliente;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.integracao.ValidacaoCadastroService;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final AuditoriaService auditoriaService;
    private final ValidacaoCadastroService validacaoCadastroService;

    public ClienteService(
            ClienteRepository repository,
            AuditoriaService auditoriaService,
            ValidacaoCadastroService validacaoCadastroService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
        this.validacaoCadastroService = validacaoCadastroService;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
    }

    @Transactional
    public Cliente criar(ClienteRequest request) {
        validarCadastro(request, null);
        Cliente cliente = repository.save(new Cliente(request));
        auditoriaService.registrar("CRIAR", "Cliente", cliente.getId(), cliente.getNome());
        return cliente;
    }

    @Transactional
    public Cliente atualizar(Long id, ClienteRequest request) {
        validarCadastro(request, id);
        Cliente cliente = buscar(id);
        cliente.atualizar(request);
        auditoriaService.registrar("ATUALIZAR", "Cliente", id, cliente.getNome());
        return cliente;
    }

    @Transactional
    public void alternarAtivo(Long id) {
        Cliente cliente = buscar(id);
        cliente.alternarAtivo();
        auditoriaService.registrar(cliente.isAtivo() ? "ATIVAR" : "INATIVAR", "Cliente", id, cliente.getNome());
    }

    private void validarCadastro(ClienteRequest request, Long id) {
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
