package iwmvi.erp.funcionario;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.integracao.ValidacaoCadastroService;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import iwmvi.erp.shared.validation.DocumentoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final AuditoriaService auditoriaService;
    private final ValidacaoCadastroService validacaoCadastroService;

    public FuncionarioService(
        FuncionarioRepository repository,
        AuditoriaService auditoriaService,
        ValidacaoCadastroService validacaoCadastroService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
        this.validacaoCadastroService = validacaoCadastroService;
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Funcionario buscar(Long id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));
    }

    @Transactional
    public Funcionario criar(FuncionarioRequest request) {
        validar(request, null);
        Funcionario funcionario = repository.save(FuncionarioMapper.toEntity(request));
        auditoriaService.registrar("CRIAR", "Funcionario", funcionario.getId(), funcionario.getNome());
        return funcionario;
    }

    @Transactional
    public Funcionario atualizar(Long id, FuncionarioRequest request) {
        validar(request, id);
        Funcionario funcionario = buscar(id);
        FuncionarioMapper.atualizar(funcionario, request);
        auditoriaService.registrar("ATUALIZAR", "Funcionario", id, funcionario.getNome());
        return funcionario;
    }

    @Transactional
    public void atualizarFoto(Long id, String fotoArquivo) {
        Funcionario funcionario = buscar(id);
        funcionario.definirFotoArquivo(fotoArquivo);
        auditoriaService.registrar("ATUALIZAR_FOTO", "Funcionario", id, funcionario.getNome());
    }

    @Transactional
    public void alternarAtivo(Long id) {
        Funcionario funcionario = buscar(id);
        funcionario.alternarAtivo();
        auditoriaService.registrar(
            funcionario.isAtivo() ? "ATIVAR" : "INATIVAR", "Funcionario", id, funcionario.getNome());
    }

    private void validar(FuncionarioRequest request, Long id) {
        String cpf = DocumentoValidator.somenteDigitos(request.cpf());
        if (!DocumentoValidator.cpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        boolean duplicado =
            id == null ? repository.existsByCpf(cpf) : repository.existsByCpfAndIdNot(cpf, id);
        if (duplicado) {
            throw new DocumentoJaCadastradoException(request.cpf());
        }

        validacaoCadastroService.validarCep(request.cep());
    }
}
