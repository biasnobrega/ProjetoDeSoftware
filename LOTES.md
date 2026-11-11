# Lotes de commit

Caminhos relativos ao repositório (pasta `MinhaRefeicao/`). Cada lote compila junto com os anteriores:
`javac -encoding UTF-8 -d out <arquivos dos lotes 1..N>`.
Cada um começa com `git pull` para receber o lote anterior.

## Lote 1 – Eduardo
Mensagem: `Estrutura do projeto, dados iniciais e enumerações`

- `MinhaRefeicao/.gitignore`
- `MinhaRefeicao/dados/planos.csv`
- `MinhaRefeicao/dados/cardapio.csv`
- `MinhaRefeicao/src/minharefeicao/entidade/StatusAssinatura.java`
- `MinhaRefeicao/src/minharefeicao/entidade/StatusPedido.java`
- `MinhaRefeicao/src/minharefeicao/entidade/StatusPagamento.java`
- `MinhaRefeicao/src/minharefeicao/entidade/StatusEntrega.java`
- `MinhaRefeicao/src/minharefeicao/entidade/ResultadoValidacao.java`

## Lote 2 – Eduardo
Mensagem: `Entidades do plano, cardápio e pagamento`

Todos em `MinhaRefeicao/src/minharefeicao/entidade/`:
- `PlanoAssinatura.java`
- `PreferenciaAlimentar.java`
- `CodigoConfirmacao.java`
- `EnderecoEntrega.java`
- `CartaoCredito.java`
- `Pagamento.java`
- `Entrega.java`
- `ItemCardapio.java`
- `PratoPrincipal.java`
- `Acompanhamento.java`
- `Sobremesa.java`
- `Cardapio.java`

## Lote 3 – Eduardo
Mensagem: `Entidades do pedido, assinatura e assinante`

Todos em `MinhaRefeicao/src/minharefeicao/entidade/`:
- `ItemPedido.java`
- `ItemPrato.java`
- `ItemAcompanhamento.java`
- `ItemSobremesa.java`
- `Pedido.java`
- `Assinatura.java`
- `Assinante.java`

## Lote 4 – Beatriz
Mensagem: `Camada de persistência em CSV e coleções`

- `MinhaRefeicao/src/minharefeicao/persistencia/PlanoDAO.java`
- `MinhaRefeicao/src/minharefeicao/persistencia/CardapioDAO.java`
- `MinhaRefeicao/src/minharefeicao/persistencia/AssinanteDAO.java`
- `MinhaRefeicao/src/minharefeicao/colecao/Assinantes.java`
- `MinhaRefeicao/src/minharefeicao/colecao/PlanosAssinatura.java`

## Lote 5 – Beatriz
Mensagem: `Fronteiras de SMS e operadora de cartão`

- `MinhaRefeicao/src/minharefeicao/fronteira/InterfaceSMS.java`
- `MinhaRefeicao/src/minharefeicao/fronteira/InterfaceOperadoraCartao.java`

## Lote 6 – Eduardo
Mensagem: `Controlador do caso de uso Assinar Plano`

- `MinhaRefeicao/src/minharefeicao/controle/ControladorAssinatura.java`

## Lote 7 – Beatriz
Mensagem: `Tela Swing e inicialização do sistema`

- `MinhaRefeicao/src/minharefeicao/fronteira/TelaAssinatura.java`
- `MinhaRefeicao/src/minharefeicao/Main.java`

## Lote 8 – Eduardo
Mensagem: `README e documentação da implementação`

- `MinhaRefeicao/README.md`
- `MinhaRefeicao/ACRESCIMOS-UML.md`
- `MinhaRefeicao/LOTES.md`

## Lote 9 – Beatriz
Mensagem: `Scripts para compilar e executar`

- `MinhaRefeicao/compilar.sh`
- `MinhaRefeicao/compilar.bat`
- `MinhaRefeicao/executar.sh`
- `MinhaRefeicao/executar.bat`

## Observações

- A pasta `out/` não entra no commit (está no `.gitignore` do lote 1).
- Os arquivos `dados/assinantes.csv` e `dados/assinaturas.csv` são gerados na execução e estão no `.gitignore`: mesmo que sejam copiados, o `git status` não os mostra.
- `TelaAssinatura` fica no pacote `fronteira` (como o plano prevê) mas só entra no lote 7, porque depende do controlador do lote 6. Os lotes 5 e 6 compilam sem ela.
