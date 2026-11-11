# Minha Refeição – Entrega 4

Implementação em Java do caso de uso **Assinar Plano de Refeições** (fluxo principal e fluxos alternativos FA1, FA2 e FA3), a partir do diagrama de sequência e do diagrama de classes de projeto da Entrega 3.

**Disciplina:** Projeto de Software · **Professora:** Ana Claudia Rossi
**Grupo:** Beatriz Silva Nóbrega e Eduardo Kenji Hernandes Ikematu

## Requisitos

- JDK 17 ou superior (`java -version` e `javac -version` precisam responder).
- Nenhuma biblioteca externa, Maven ou Gradle.

## Compilar e executar

Sempre a partir da pasta `MinhaRefeicao/`, porque o programa lê e grava a pasta `dados/` em relação ao diretório atual.

**Linux, macOS ou Git Bash:**

```sh
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out minharefeicao.Main
```

ou `./compilar.sh` e depois `./executar.sh`.

**Windows (cmd):** `compilar.bat` e depois `executar.bat`.

**NetBeans, IntelliJ ou VS Code:** abrir a pasta `MinhaRefeicao/` como projeto Java simples, marcar `src/` como pasta de código-fonte e executar `minharefeicao.Main` com o diretório de trabalho em `MinhaRefeicao/`.

## Como usar (fluxo principal, igual ao protótipo)

1. Clique em **Começar**.
2. Informe um celular com DDD, por exemplo `(11) 98888-7777`, e clique em **Continuar**.
3. O SMS é simulado: o código de 6 dígitos aparece numa janela (e no console). Digite-o e clique em **Validar código**.
4. Escolha o **Plano Equilíbrio** (20 refeições), confira os dados e clique em **Continuar**.
5. Marque **Não tenho restrições** e clique em **Continuar**.
6. Escolha **Frango grelhado**, quantidade 10, **Adicionar prato**; depois **Omelete de legumes**, quantidade 10, **Adicionar prato** (20 de 20). Clique em **Continuar**.
7. Escolha três acompanhamentos (um de cada vez, com **Adicionar acompanhamento**) e clique em **Continuar**.
8. Escolha **Mousse de chocolate** (10) e **Frutas frescas** (10) e clique em **Continuar**.
9. Confira o resumo e clique em **Confirmar pedido**.
10. Informe o endereço (ex.: CEP `01310-100`, rua Av. Paulista, número 1000, bairro Bela Vista, cidade São Paulo), clique em **Continuar** e depois em **Confirmar endereço**.
11. Informe o cartão: titular `EDUARDO KENJI`, número `4111 1111 1111 1111`, validade `12/30`, CVV `123`, e clique em **Confirmar pagamento**.
12. A tela final mostra o protocolo, o plano, o pedido, o endereço e a data prevista da primeira entrega.

## Como testar cada fluxo

| Fluxo | O que fazer |
|---|---|
| Principal | Siga o passo a passo da seção anterior |
| FA1 | Digitar 3 códigos errados: o botão "Validar código" é bloqueado → "Reenviar código" → digitar o novo código |
| FA2 | Nos pratos ou nas sobremesas, adicionar 15 de um item e depois 10 de outro (o limite é a quantidade de refeições do plano, 20 no Equilíbrio): aparece a mensagem de limite e a seleção continua. O campo de quantidade não aceita mais que 20 de uma vez |
| FA3, nova tentativa | Cartão **4111 1111 1117 0000** (válido, mas termina em 0000) é recusado → informar outro cartão |
| FA3, desistência | Cartão terminado em 0000 → "Desistir da assinatura" → a assinatura fica CANCELADA no `assinaturas.csv` |
| Dados inválidos | Os campos de celular, CEP, cartão, validade e CVV já se formatam e não aceitam dígitos a mais. Cidade só com a sigla ("SP"), rua ou nome sem vogal ("wkdkns"), cartão com número errado, validade vencida ou CVV curto: aparece uma mensagem e o campo pode ser corrigido |
| Preferências | Marcar, por exemplo, "Vegetariano": a lista de pratos mostra só os compatíveis. Para repetir o fluxo com outras preferências, reabra o programa ou use outro celular |
| Persistência | Fechar e abrir de novo e usar o mesmo celular: o assinante é reaproveitado de `assinantes.csv` e a numeração do protocolo continua de onde parou em `assinaturas.csv` |

Para começar limpo (por exemplo antes de gravar o vídeo), apague `dados/assinantes.csv` e `dados/assinaturas.csv`. Eles são criados de novo na primeira gravação e não vão para o GitHub (estão no `.gitignore`).

## Wiki e vídeo

- Página da Wiki com o mapeamento UML → código: *(link a incluir)*
- Vídeo da apresentação: *(link a incluir)*

## Estrutura de pacotes

| Pacote | Conteúdo | Estereótipo no diagrama |
|---|---|---|
| `minharefeicao.fronteira` | `TelaAssinatura`, `InterfaceSMS`, `InterfaceOperadoraCartao` | «boundary» |
| `minharefeicao.controle` | `ControladorAssinatura` | «control» |
| `minharefeicao.colecao` | `Assinantes`, `PlanosAssinatura` | «collection» |
| `minharefeicao.entidade` | 19 classes de domínio e os 5 enums | «entity», abstratas e «enumeration» |
| `minharefeicao.persistencia` | `PlanoDAO`, `CardapioDAO`, `AssinanteDAO` | acréscimo técnico (fora da UML) |
| `minharefeicao` | `Main` | monta os objetos que já existem no início do diagrama |

## Dados (pasta `dados/`)

Separador `;`, codificação UTF-8, primeira linha com o cabeçalho.

| Arquivo | Colunas | Quando |
|---|---|---|
| `planos.csv` | id, nome, quantidadeRefeicoes, periodicidadeEntrega, valorMensal, ativo | Lido na inicialização |
| `cardapio.csv` | tipo (PRATO, ACOMPANHAMENTO, SOBREMESA), id, nome, descricao, calorias, disponivel, preferenciasCompativeis (separadas por `\|`) | Lido na inicialização |
| `assinantes.csv` | id, nome, celular | Lido na inicialização; gravado em `Assinantes.adicionar(a)` |
| `assinaturas.csv` | idAssinatura, protocolo, celular, plano, status, data, valorTotal, endereco, finalCartao, statusPagamento, dataPrevistaEntrega, itensDoPedido | Gravado quando a assinatura é ativada ou cancelada |

O CVV nunca é gravado e do cartão só se grava o final.

## Simulações

- **SMS:** o código aparece numa caixa de diálogo e no console.
- **Operadora de cartão:** cartão terminado em `0000` é recusado (use `4111 1111 1117 0000`, que passa na validação do número); os demais são autorizados com um código `AUT-…`.

## Mapeamento UML → código

Os nomes de classes, atributos, métodos e parâmetros seguem o diagrama de classes de projeto. Cada seta do diagrama de sequência é uma chamada no método do objeto de onde ela sai, e os comentários `// Passo N` e `// FA…` marcam os pontos correspondentes. Tudo o que foi acrescentado além da UML está em [ACRESCIMOS-UML.md](ACRESCIMOS-UML.md). A divisão dos arquivos por commit está em [LOTES.md](LOTES.md).

Os botões da tela usam lambdas de uma linha (`e -> metodo()`) só para ligar o clique ao método.
