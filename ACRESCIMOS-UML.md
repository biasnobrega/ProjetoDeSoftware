# Acréscimos em relação à UML (Entrega 3)

Tudo o que existe no código e não aparece nos diagramas definitivos da Entrega 3, com o motivo. Nenhuma classe, método ou relacionamento do diagrama foi renomeado ou removido, e todas as assinaturas (parâmetros e retornos) são as da E3. A `TelaAssinatura` tem exatamente os 18 estímulos públicos do diagrama e o `ControladorAssinatura`, exatamente os 18 métodos do diagrama: não há estímulo nem mensagem de controle fora do cenário.

## 1. Classes novas

| Item | Motivo |
|---|---|
| `PlanoDAO`, `CardapioDAO`, `AssinanteDAO` (pacote `persistencia`) | Persistência em CSV exigida pelo enunciado, isolada do domínio (padrão DAO, Bezerra 12.2.3) |
| `Main` | Cria os objetos que o diagrama mostra como "já existentes no início" e abre a tela |

## 2. Associações e atributos novos

| Onde | Acréscimo | Motivo |
|---|---|---|
| `Assinantes` | atributo `AssinanteDAO dao` | O diagrama diz que a coleção guarda os assinantes; a gravação no CSV acontece em `adicionar(a)` |
| `Assinatura` | construtor `Assinatura(a, p)` recebe o `Assinante` mas não o guarda | No diagrama de classes a seta sai do `Assinante`; `a` é só parâmetro (dependência) |

## 3. Métodos e construtores novos

| Classe | Acréscimo | Motivo |
|---|---|---|
| `PlanoAssinatura` | construtor com todos os campos | O `PlanoDAO` cria os planos lidos do CSV |
| `Assinante` | construtor `Assinante(id, nome, celular)` | O `AssinanteDAO` recria assinantes já gravados e mantém o contador de ids |
| `ItemCardapio` (e subclasses) | construtores com todos os campos | O `CardapioDAO` cria os itens lidos do CSV |
| `Assinatura` | método estático `continuarNumeracao(ultimoIdGravado)` | O `AssinanteDAO` informa o maior id gravado em `assinaturas.csv`, para o protocolo não se repetir entre execuções (mesmo padrão do construtor de `Assinante` lido do CSV) |
| `PratoPrincipal` | `adicionarPreferenciaCompativel(pref)` | Registra as preferências compatíveis lidas do CSV (associação "compatível com") |
| `Cardapio` | `adicionarItem(item)` | Recebe os itens criados pelo `CardapioDAO` (agregação) |
| `Assinantes` | `gravarAssinatura(a, as)` | **Acréscimo ao fluxo:** grava a assinatura no `assinaturas.csv` ao fim do pagamento aprovado e no cancelamento (FA3.6). Não existe mensagem de gravação no diagrama de sequência |
| `ControladorAssinatura` | duas chamadas a `assinantes.gravarAssinatura(...)`, em `efetuarPagamento` (aprovado) e em `cancelarAssinatura` | Disparam a gravação acima |
| `InterfaceOperadoraCartao` | método privado `autorizarPagamento(numeroCartao, validade, cvv, valor)` | É a mensagem para o ator Operadora do diagrama de sequência; como a operadora é simulada, a regra "final 0000 é recusado" fica nesse método. Não consta no diagrama de classes |
| `Cardapio` | `listarAcompanhamentos()` e `listarSobremesas()` só devolvem itens com `isDisponivel()` | O diagrama de sequência mostra o filtro de disponibilidade apenas para os pratos; sem ele, itens indisponíveis do CSV apareceriam na tela |
| `Assinatura` | `registrarPagamento` lê o valor com `pedido.getValorTotal()` | O diagrama só mostra o `new Pagamento(valor, cartao)`; o valor precisa vir de algum lugar |
| Atributos sem uso | `PreferenciaAlimentar.descricao`, `Assinante.nome` (sempre vazio), `Cardapio.dataFim`/`ativo`, `Entrega.dataRealizada`/`endereco`, ids de várias entidades | Estão no diagrama de classes, então foram mantidos mesmo sem uso no cenário |

## 4. Getters extras

| Classe | Getter | Quem usa |
|---|---|---|
| `Assinante` | `getIdAssinante()`, `getNome()`, `getCelular()` | `AssinanteDAO`, `Assinantes`, controlador (reenvio do código) |
| `PlanoAssinatura` | `getIdPlano()` | `PlanosAssinatura`, tela |
| `ItemCardapio` | `getIdItemCardapio()`, `getCalorias()`, `getDescricao()` | `Cardapio`, tela |
| `CartaoCredito` | `getNumeroCartao()` (lê o atributo `numero` da UML), `getValidade()` | `InterfaceOperadoraCartao` |
| `CartaoCredito` | `getFinalCartao()` | `AssinanteDAO`: só o final do cartão vai para o CSV |
| `Pagamento` | `getValor()` | Controlador envia o valor à operadora |
| `Pagamento` | `getStatus()`, `getCartao()` | `AssinanteDAO` |
| `Pedido` | `getValorTotal()` | `Assinatura.registrarPagamento` usa o total do pedido como valor do pagamento |
| `Assinatura` | `getIdAssinatura()`, `getProtocolo()`, `getDataContratacao()`, `getStatus()`, `getPlano()`, `getEnderecoEntrega()`, `getValorTotal()`, `getDataPrevistaEntrega()`, `getUltimoPagamento()` | `AssinanteDAO` (gravação do `assinaturas.csv`) |

## 5. Estado de interface na `TelaAssinatura`

| Atributo | Motivo |
|---|---|
| `idPlanoSelecionado`, `limitePlano` | `selecionarPlano` guarda o plano usado depois em `confirmarPlano`; o limite aparece nas mensagens de FA2 e nos contadores |
| `cep`, `logradouro`, `numero`, `complemento`, `bairro`, `cidade` | `informarEnderecoEntrega` guarda o endereço usado em `confirmarEnderecoEntrega` |
| `totalPratos`, `totalAcompanhamentos`, `totalSobremesas`, `etapaAtual` | Texto dos contadores (`atualizarContador`) |
| `idsPlanos`, `idsPratos`, `idsAcompanhamentos`, `idsSobremesas` (`List<Integer>`) | Relacionam a linha escolhida na lista ao id do item, sem guardar objetos de entidade na tela (a relação com `PlanoAssinatura` e `ItemCardapio` continua sendo dependência) |
| Caixa "Não tenho restrições" | Opção do protótipo (E1). Desmarca e bloqueia as outras e envia a lista vazia, que não filtra nenhum prato. Não vira `PreferenciaAlimentar`. A tela exige marcar ao menos uma opção |
| Campo de quantidade limitado ao plano (`SpinnerNumberModel` com máximo = `getQuantidadeRefeicoes()`) | Não deixa digitar mais que o plano de uma vez (ex.: 25 num plano de 20). O FA2 do cenário continua acontecendo quando a **soma** passa do limite (ex.: 15 e depois 10), porque quem confere o total é o `Pedido` (`totalPratos() + quantidade <= limite`) |
| Máscaras e limites de digitação (classes internas `Mascara` e `LimiteTexto`, `DocumentFilter` do Swing) | Celular `(11) 98888-7777`, código de até 6 dígitos, CEP `01310-100`, cartão `0000 0000 0000 0000`, validade `MM/AA` e CVV de até 4 dígitos já se formatam e não aceitam dígitos a mais. Rua, número, complemento, bairro, cidade e titular têm tamanho máximo e só aceitam caracteres que fazem sentido no campo; o titular sai em maiúsculas, como no cartão |
| Validação da digitação (`cliqueCelular`, `cliqueEndereco`, `cliquePagamento`, `numeroCartaoValido`, `validadeValida`, `titularValido`, `palavrasPlausiveis`) | Confere só o formato antes de chamar o estímulo: celular com DDD e 9 dígitos começando com 9; CEP com 8 dígitos; número com até 6 dígitos ou S/N; rua, bairro e cidade com palavras que tenham vogal e não repitam a mesma letra 3 vezes (cidade com pelo menos 3 letras, para não aceitar só a sigla); titular com nome e sobrenome; cartão de 16 dígitos (15 no American Express) com dígito verificador (algoritmo de Luhn); validade MM/AA não vencida; CVV de 3 dígitos (4 no American Express). O código do SMS **não** é validado na tela: qualquer código digitado vai para `validarCodigo` e conta como tentativa, como no FA1. Não confere se a rua existe (exigiria a base dos Correios). Na seleção de pratos, "Continuar" só avança com ao menos um prato no pedido (a contagem é da própria tela); abaixo do limite do plano é permitido seguir, porque o FA2 só trata a soma acima do limite |
| Rótulo "Rua" | A tela mostra "Rua", como no protótipo (E1, passo 18); o atributo continua `logradouro`, como no diagrama de classes |
| Métodos `mostrar`, `reiniciarEstado`, `moeda`, `quantidade`, `pagina`, `formulario`, `linha`, `painelSelecao`, `montarTela` e `clique...` | Montagem da janela Swing, troca de página e leitura dos campos. Não têm regra de negócio. A navegação segue só a ordem do cenário: não há botão "Voltar" (o único retorno é o do FA1, FA2.4 e FA3.4) |

## 6. Decisões de implementação (lacunas da E3)

| Ponto | Decisão |
|---|---|
| Qual código validar | `Assinante.validarCodigo` valida sempre o último código gerado |
| Contagem do FA1 | `CodigoConfirmacao.validar` conta uma tentativa a cada falha ou expiração (10 min); a 3ª falha devolve `BLOQUEADO` e o código bloqueado não valida mais, mesmo se o certo for digitado. A tela desabilita "Validar código" até o reenvio |
| Assinante existente | Se o celular já está em `assinantes.csv`, o objeto é reaproveitado e não é gravado de novo |
| Preferências de um assinante reaproveitado | Ficam em memória na execução e não são gravadas; `adicionarPreferencia` ignora nomes repetidos. Como o diagrama só acrescenta preferências, se o mesmo celular contratar duas vezes **na mesma execução**, as preferências da primeira contratação continuam valendo. Para demonstrar dois fluxos seguidos, reabra o programa ou use outro celular |
| Itens repetidos no pedido | Como no diagrama, cada seleção cria um `ItemPrato`/`ItemAcompanhamento`/`ItemSobremesa` («create»), e `gerarResumo` devolve uma linha por item. Só na exibição (`exibirResumoPedido` e `exibirConfirmacao`) a tela soma as linhas do mesmo tipo e nome (método privado `agruparItensIguais`), ex.: 6x + 6x Mousse aparece como 12x Mousse. Nenhuma mensagem ou entidade muda |
| Contador de acompanhamentos | Depois de `selecionarAcompanhamento` a tela chama o próprio `atualizarContador` (método privado já usado em pratos e sobremesas) só para mostrar quantos foram adicionados; não há limite nem mensagem nova, e o controlador continua `void` como na E3 |
| Limite do FA2 | `getQuantidadeRefeicoes()` do plano, contado separadamente para pratos e sobremesas; acompanhamentos sem limite |
| Valor do pagamento | Vem do `valorTotal` do pedido, definido em `calcularValorTotal` (valor mensal do plano) |
| Nova tentativa no FA3 | Cada tentativa cria novos `CartaoCredito` e `Pagamento`; o controlador troca `pagamentoAtual` |
| Ids | Contadores estáticos nas entidades criadas na execução; `Assinante` e `Assinatura` continuam do maior id lido dos CSV. Itens de plano e cardápio usam o id do CSV |
| Data prevista da entrega | `Entrega.calcularDataPrevista()` usa hoje + 3 dias |
| Protocolo | `MR-aaaammdd-` + id da assinatura com 4 dígitos. O id fica na primeira coluna do `assinaturas.csv`, então o protocolo não se repete entre execuções. Assinaturas canceladas não têm protocolo (coluna vazia no CSV) |
| `Cardapio` | Uma única lista de `ItemCardapio`, filtrada por tipo com `instanceof`, como no diagrama (`Cardapio` 0..* `ItemCardapio`) |
| Bandeira do cartão | Método privado `CartaoCredito.descobrirBandeira`: deduzida do primeiro dígito (4 Visa, 5 Mastercard, senão "Outra") |
| Dados de plano e cardápio | `planos.csv` e `cardapio.csv` trazem os itens do protótipo (Plano Equilíbrio com 20 refeições, Frango grelhado, Omelete de legumes, Mousse de chocolate, Frutas frescas) e mais alguns itens, inclusive indisponíveis, para mostrar os filtros |
