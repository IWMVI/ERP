# Contribuição

## Formatação

O projeto usa `.editorconfig` para manter regras básicas de indentação, charset, fim de linha e espaços em branco entre editores.

Para formatar o código Java e normalizar os arquivos de texto suportados pelo Spotless:

```bash
./mvnw spotless:apply
```

No Windows:

```powershell
.\mvnw.cmd spotless:apply
```

Antes de concluir uma alteração, execute:

```bash
./mvnw verify
```

No Windows:

```powershell
.\mvnw.cmd verify
```

O `verify` executa a validação do Spotless e falha quando o código Java não está de acordo com o formatter configurado.

## Convenções

- Java: 4 espaços, sem imports não utilizados e formatação automática pelo Google Java Format via Spotless.
- HTML, CSS e XML: 4 espaços e regras básicas definidas pelo `.editorconfig`.
- Properties, SQL e YAML: 4 espaços, UTF-8 e newline no final do arquivo.
- Novos arquivos devem respeitar a organização por domínio já utilizada no projeto.
