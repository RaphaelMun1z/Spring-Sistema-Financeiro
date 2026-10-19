# Checklist essencial do MVP

## Ja feito

- [x] Autenticacao com JWT.
- [x] Login e consulta do usuario autenticado.
- [x] Cadastro e consulta de contas bancarias.
- [x] Cadastro e consulta de cartoes.
- [x] Cadastro e consulta de categorias.
- [x] Cadastro e consulta de orcamentos.
- [x] Cadastro e consulta de movimentacoes.
- [x] Consulta de lancamentos por periodo.
- [x] Cadastro e consulta de faturas.
- [x] Cadastro e consulta de investimentos.
- [x] Cadastro e consulta de metas financeiras.
- [x] Validacao basica dos dados.
- [x] Tratamento global de excecoes.
- [x] Documentacao inicial com Swagger/OpenAPI.
- [x] Mock de dados para desenvolvimento.

## Precisa ajustar

- [ ] Garantir que cada usuario acesse somente os proprios dados.
- [ ] Remover o `Thread.sleep(2000)` do `CartaoController`.
- [ ] Corrigir o relacionamento do criador em `MetaFinanceira`.
- [ ] Remover o uso de reflexao no mock.
- [ ] Corrigir o preenchimento do tipo de voucher.
- [ ] Atualizar corretamente o saldo da conta apos cada movimentacao.
- [ ] Atualizar corretamente o valor total e o status da fatura.
- [ ] Garantir o funcionamento correto de parcelamentos.
- [ ] Revisar validacoes de valores, datas, parcelas e limites.
- [ ] Corrigir problemas de codificacao das mensagens.
- [ ] Remover credenciais e segredo JWT padrao de ambientes reais.

## Pendente essencial

### Banco e configuracao

- [ ] Remover `ddl-auto: create-drop` do ambiente de execucao.
- [ ] Separar configuracoes de desenvolvimento e producao.
- [ ] Documentar as variaveis de ambiente.
- [ ] Garantir inicializacao em um banco vazio.

### Documentacao e entrega

- [ ] Atualizar o README com instalacao e execucao.
- [ ] Documentar login, credenciais de desenvolvimento e endpoints principais.
- [ ] Configurar banco e aplicacao para execucao via Docker.
- [ ] Validar manualmente o fluxo:
  login -> conta -> categoria -> movimentacao -> lancamento/fatura -> consulta.

