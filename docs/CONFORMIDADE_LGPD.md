# Conformidade com a LGPD

Este documento registra os controles suportados pelo ERP e as providências organizacionais que o controlador deve cumprir antes da entrada em produção. Ele não substitui a avaliação jurídica do contexto da empresa.

## Responsabilidades antes da produção

1. Definir, para cada finalidade e categoria de titular, a hipótese legal aplicável dos artigos 7º e 11 da LGPD. Consentimento não deve ser usado quando outra hipótese legal for a adequada.
2. Preencher `APP_PRIVACIDADE_CONTROLADOR`, `APP_PRIVACIDADE_CONTATO` e `APP_PRIVACIDADE_ENCARREGADO`. A aplicação de produção não inicia sem esses valores.
3. Manter inventário das operações de tratamento, incluindo origem, finalidade, dados, titulares, acesso, compartilhamentos, localização, retenção e descarte.
4. Formalizar contratos com operadores e fornecedores, incluindo hospedagem, backup e serviços de consulta cadastral.
5. Definir perfis de acesso conforme as funções reais. O perfil `USUARIO` deve ser concedido somente a trabalhadores autorizados.
6. Definir uma matriz de retenção aprovada pelas áreas jurídica, fiscal, contábil e trabalhista. Não se deve apagar automaticamente um registro sujeito a obrigação legal.
7. Documentar backups criptografados, restauração, continuidade, descarte seguro, revisão periódica de acessos e treinamento.

## Atendimento aos titulares

O canal exibido em `/privacidade` deve registrar e acompanhar cada solicitação. O procedimento mínimo é:

1. registrar data, solicitante, escopo e responsável pelo atendimento;
2. validar a identidade usando apenas os dados necessários, sem solicitar documentos excessivos;
3. localizar os dados nos cadastros, auditoria, arquivos, backups e operadores pertinentes;
4. responder imediatamente em formato simplificado ou fornecer declaração completa no prazo legal aplicável;
5. corrigir, bloquear, anonimizar ou eliminar quando cabível, preservando somente o que possuir fundamento legal documentado;
6. comunicar a providência aos agentes com quem os dados foram compartilhados, quando aplicável;
7. registrar a decisão e seu fundamento sem incluir dados pessoais desnecessários no histórico.

## Resposta a incidentes

Todo evento que comprometa confidencialidade, integridade, disponibilidade ou autenticidade de dados pessoais deve ser registrado e avaliado. O controlador deve:

1. conter o incidente, preservar evidências e identificar dados e titulares afetados;
2. avaliar risco ou dano relevante;
3. acionar o responsável jurídico e o encarregado;
4. quando aplicável, comunicar a ANPD e os titulares em até três dias úteis do conhecimento;
5. complementar as informações e registrar medidas de reversão ou mitigação;
6. realizar análise de causa e revisar os controles.

## Controles presentes no sistema

- cadastro de usuários restrito a administradores;
- senhas de 12 a 72 caracteres armazenadas com BCrypt;
- CSRF, encerramento de sessão e cookies `HttpOnly` e `SameSite=Strict`;
- cookie `Secure`, HSTS e suporte a proxy reverso no perfil de produção;
- política de segurança de conteúdo, bloqueio de enquadramento e política de referência;
- fotografias de funcionários restritas a administradores;
- validação e recodificação de imagens enviadas;
- registro de operações administrativas e comerciais;
- aviso de privacidade público e canal configurável do controlador.

## Limitações conhecidas

- Os perfis existentes são amplos (`ADMIN` e `USUARIO`). Operações com equipes diferentes exigem perfis mais granulares.
- A aplicação não decide automaticamente pedidos de eliminação ou prazos de retenção, pois dependem de fundamento e obrigação externos ao código.
- Criptografia de banco, backups, TLS, monitoramento, rotação de segredos e descarte são controles da infraestrutura de implantação.
- O aviso de privacidade deve ser revisado após a definição real dos fluxos e fornecedores.

## Referências oficiais

- Lei nº 13.709/2018 (LGPD): https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm
- Direitos dos titulares: https://www.gov.br/anpd/pt-br/assuntos/titular-de-dados-1/direito-dos-titulares
- Comunicação de incidente: https://www.gov.br/anpd/pt-br/canais_atendimento/agente-de-tratamento/comunicado-de-incidente-de-seguranca-cis
- Guia de segurança para agentes de pequeno porte: https://www.gov.br/anpd/pt-br/centrais-de-conteudo/materiais-educativos-e-publicacoes/guia-vf.pdf
