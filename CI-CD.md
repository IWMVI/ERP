# CI/CD

O projeto usa GitHub Actions para validar o código e publicar uma imagem de contêiner pronta para implantação.

## Integração contínua

O workflow `CI` é executado em todo `push` e `pull_request`. Ele usa Java 21, reutiliza o cache do Maven e executa:

```bash
./mvnw --batch-mode --no-transfer-progress verify
```

Assim, testes e a verificação de formatação do Spotless precisam passar antes da integração.

## Entrega contínua

O workflow `Continuous delivery` é executado em pushes para `main`, tags iniciadas por `v` e acionamentos manuais. Depois de repetir o `verify`, ele:

1. constrói a imagem definida no `Dockerfile`;
2. publica a imagem em `ghcr.io/<proprietario>/<repositorio>`;
3. cria as tags `latest`, `sha-<commit>` e, para releases, a tag Git correspondente;
4. publica uma atestação de proveniência da imagem.

A autenticação usa apenas o `GITHUB_TOKEN` fornecido pelo GitHub Actions. Nenhuma credencial adicional é necessária para publicar no GitHub Container Registry.

## Implantação

A imagem executa como usuário sem privilégios, expõe a porta `8080` e espera a configuração de produção descrita em `application-prod.properties`, incluindo as variáveis de PostgreSQL. O ambiente de hospedagem deve definir pelo menos:

- `SPRING_PROFILES_ACTIVE=prod`;
- `POSTGRES_HOST`, `POSTGRES_PORT` e `POSTGRES_DB`;
- `POSTGRES_USER` e `POSTGRES_PASSWORD`;
- as variáveis opcionais de administrador e armazenamento aplicáveis ao ambiente.

O deploy no servidor não está automatizado porque o repositório ainda não define um provedor ou destino de execução. Quando esse destino for escolhido, deve-se adicionar um job posterior a `publish-image` que consuma o digest publicado, sem reconstruir a imagem.

