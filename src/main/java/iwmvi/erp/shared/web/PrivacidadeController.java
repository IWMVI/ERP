package iwmvi.erp.shared.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrivacidadeController {

  private final String controlador;
  private final String contato;
  private final String encarregado;

  public PrivacidadeController(
            @Value("${app.privacy.controller-name:Controlador não configurado}") String controlador,
            @Value("${app.privacy.contact:privacidade@empresa.local}") String contato,
            @Value("${app.privacy.officer:Não designado}") String encarregado) {
    this.controlador = controlador;
    this.contato = contato;
    this.encarregado = encarregado;
  }

  @GetMapping("/privacidade")
  public String privacidade(Model model) {
    model.addAttribute("controladorPrivacidade", controlador);
    model.addAttribute("contatoPrivacidade", contato);
    model.addAttribute("encarregadoPrivacidade", encarregado);
    return "privacidade";
  }
}
