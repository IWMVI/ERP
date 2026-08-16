package iwmvi.erp.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import iwmvi.erp.shared.exception.EmailJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

  @Mock private UsuarioRepository usuarioRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private UsuarioService usuarioService;

  @BeforeEach
  void setUp() {
    usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
  }

  @Test
  void deveCadastrarUsuarioComSenhaCodificada() {
    UsuarioRequest request =
        new UsuarioRequest("Wallace", "wallace@gmail.com", "senha-segura-123", PerfilUsuario.ADMIN);

    when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
    when(passwordEncoder.encode("senha-segura-123")).thenReturn("senha-codificada");
    when(usuarioRepository.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Usuario usuario = usuarioService.criar(request);

    assertEquals("Wallace", usuario.getNome());
    assertEquals("wallace@gmail.com", usuario.getEmail());
    assertEquals("senha-codificada", usuario.getSenha());
    assertEquals(PerfilUsuario.ADMIN, usuario.getPerfil());
    assertTrue(usuario.isAtivo());
    verify(passwordEncoder).encode("senha-segura-123");
  }

  @Test
  void deveLancarExcecaoAoCadastrarUsuarioComEmailJaCadastrado() {
    UsuarioRequest request =
        new UsuarioRequest(
            "Wallace", "wallace@gmail.com", "senha-segura-123", PerfilUsuario.USUARIO);
    when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

    assertThrows(EmailJaCadastradoException.class, () -> usuarioService.criar(request));
    verify(passwordEncoder, never()).encode(any());
  }

  @Test
  void deveReconhecerUsuarioExistenteAntesDeValidarSenhaDoInicializador() {
    UsuarioRequest request =
        new UsuarioRequest("Administrador", "admin@erp.local", "antiga", PerfilUsuario.ADMIN);
    when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

    assertThrows(EmailJaCadastradoException.class, () -> usuarioService.criar(request));
    verify(passwordEncoder, never()).encode(any());
  }

  @Test
  void deveRejeitarSenhaCurtaMesmoSemValidacaoDoController() {
    UsuarioRequest request =
        new UsuarioRequest("Wallace", "wallace@gmail.com", "curta", PerfilUsuario.USUARIO);

    assertThrows(IllegalArgumentException.class, () -> usuarioService.criar(request));
    verify(usuarioRepository, never()).save(any());
  }

  @Test
  void deveRevogarAcessoDeOutroUsuario() {
    Usuario usuario =
        new Usuario(
            "Colaborador", "colaborador@erp.local", "senha-codificada", PerfilUsuario.USUARIO);
    when(usuarioRepository.findById(10L)).thenReturn(java.util.Optional.of(usuario));

    Usuario atualizado = usuarioService.alternarAtivo(10L, "admin@erp.local");

    assertTrue(!atualizado.isAtivo());
  }

  @Test
  void deveImpedirRevogacaoDoProprioAcesso() {
    Usuario usuario =
        new Usuario("Administrador", "admin@erp.local", "senha-codificada", PerfilUsuario.ADMIN);
    when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(usuario));

    assertThrows(
        IllegalStateException.class, () -> usuarioService.alternarAtivo(1L, "admin@erp.local"));
  }
}
