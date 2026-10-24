package io.github.raphaelmun1z.gestao_financeira;

import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.PapelDoUsuarioEnum;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FluxoFinanceiroTests {
    @Value("${local.server.port}")
    private int port;
    @Autowired
    private UsuarioRepository usuarios;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper mapper;

    private final HttpClient client = HttpClient.newHttpClient();
    private String token;
    private String conta;
    private String entrada;
    private String saida;
    private static final String PERIODO = "?dataInicial=2024-01-01&dataFinal=2030-12-31";

    @BeforeEach
    void preparar() throws Exception {
        token = criarUsuarioEAutenticar();
        conta = post("/contas-bancarias", Map.of("nomeDoBanco", "Conta principal", "saldoCorrente", 1000, "creditoTotal", 1000)).get("id").asText();
        entrada = categoria("Receita", "ENTRADA");
        saida = categoria("Alimentação", "SAIDA");
    }

    @Test
    void executarFluxoCompletoComSaldosParcelasPagamentoEConsultas() throws Exception {
        String debito = cartao("DEBITO", null);
        String voucher = cartao("VOUCHER", Map.of("tipoVoucher", "VA_VR", "valorRecarga", 100, "diaRecarga", 31, "saldoCorrente", 100));
        String credito = cartao("CREDITO", Map.of("limiteCredito", 1000, "diaFechamento", 10, "diaVencimento", 17));
        movimentar("PIX", entrada, "500.00", null, 1, "2024-01-05");
        movimentar("DINHEIRO", saida, "100.00", null, 1, "2024-01-05");
        movimentar("CARTAO_DEBITO", saida, "50.00", debito, 1, "2024-01-05");
        movimentar("VOUCHER", saida, "25.00", voucher, 1, "2024-01-05");
        JsonNode compra = movimentar("CARTAO_CREDITO", saida, "100.00", credito, 3, "2024-01-05");

        verificarConta("1350.00", "900.00");
        JsonNode cartoes = get("/cartoes/" + conta);
        for (JsonNode item : cartoes) {
            if (item.get("id").asText().equals(voucher)) {
                assertThat(item.get("saldoCorrente").decimalValue()).isEqualByComparingTo("75.00");
            }
        }
        JsonNode faturas = get("/cartoes/" + credito + "/faturas" + PERIODO);
        assertThat(faturas.size()).isEqualTo(3);
        BigDecimal total = BigDecimal.ZERO;
        String primeiraFatura = null;
        for (JsonNode fatura : faturas) {
            total = total.add(fatura.get("valorTotal").decimalValue());
            assertThat(fatura.get("status").asText()).isEqualTo("VENCIDA");
            assertThat(fatura.get("lancamentos").size()).isEqualTo(1);
            if (fatura.get("dataReferencia").asText().equals("2024-01")) {
                primeiraFatura = fatura.get("id").asText();
                assertThat(fatura.get("valorTotal").decimalValue()).isEqualByComparingTo("33.34");
                assertThat(fatura.get("lancamentos").get(0).get("dataLancamento").asText()).isEqualTo("2024-01-17");
            } else {
                assertThat(fatura.get("valorTotal").decimalValue()).isEqualByComparingTo("33.33");
            }
        }
        assertThat(total).isEqualByComparingTo("100.00");
        assertThat(primeiraFatura).isNotNull();
        assertThat(get("/contas-bancarias/" + conta + "/lancamentos" + PERIODO).size()).isEqualTo(7);
        assertThat(get("/contas-bancarias/" + conta + "/registros-de-movimentacao" + PERIODO).size()).isEqualTo(5);
        assertThat(post("/cartoes/faturas/" + primeiraFatura + "/pagamento", Map.of()).get("status").asText()).isEqualTo("PAGA");
        post("/cartoes/faturas/" + primeiraFatura + "/pagamento", Map.of());
        verificarConta("1316.66", "933.34");
        request("DELETE", "/contas-bancarias/registros-de-movimentacao/" + compra.get("id").asText(), null, token, 400);
        verificarConta("1316.66", "933.34");
    }

    @Test
    void cancelarMovimentacaoRecalculaTodasAsFaturasERestauraLimite() throws Exception {
        String credito = cartao("CREDITO", Map.of("limiteCredito", 1000, "diaFechamento", 31, "diaVencimento", 5));
        JsonNode compra = movimentar("CARTAO_CREDITO", saida, "100.00", credito, 3, "2024-01-31");
        JsonNode faturas = get("/cartoes/" + credito + "/faturas" + PERIODO);
        List<String> referencias = new ArrayList<>();
        for (JsonNode fatura : faturas) {
            referencias.add(fatura.get("dataReferencia").asText());
            assertThat(fatura.get("lancamentos").get(0).get("dataLancamento").asText()).endsWith("-05");
        }
        assertThat(referencias).containsExactlyInAnyOrder("2024-03", "2024-04", "2024-05");
        request("DELETE", "/contas-bancarias/registros-de-movimentacao/" + compra.get("id").asText(), null, token, 204);
        verificarConta("1000.00", "1000.00");
        assertThat(get("/contas-bancarias/" + conta + "/lancamentos" + PERIODO).size()).isZero();
        for (JsonNode fatura : get("/cartoes/" + credito + "/faturas" + PERIODO)) {
            assertThat(fatura.get("valorTotal").decimalValue()).isEqualByComparingTo("0");
            assertThat(fatura.get("lancamentos").size()).isZero();
        }
    }

    @Test
    void cancelarSaidaERecargaEstornaSaldosSemAfetarOutrosRecursos() throws Exception {
        JsonNode movimento = movimentar("PIX", saida, "50.00", null, 1, "2024-01-05");
        request("DELETE", "/contas-bancarias/registros-de-movimentacao/" + movimento.get("id").asText(), null, token, 204);
        verificarConta("1000.00", "1000.00");
        String voucher = cartao("VOUCHER", Map.of("tipoVoucher", "VA_VR", "valorRecarga", 100, "diaRecarga", 10));
        JsonNode recarga = movimentar("VOUCHER", entrada, "100.00", voucher, 1, "2024-01-05");
        movimentar("VOUCHER", saida, "80.00", voucher, 1, "2024-01-05");
        request("DELETE", "/contas-bancarias/registros-de-movimentacao/" + recarga.get("id").asText(), null, token, 400);
        verificarConta("1000.00", "1000.00");
    }

    @Test
    void rejeitarSaldoLimiteTipoContaEParcelasInvalidosSemPersistenciaParcial() throws Exception {
        String credito = cartao("CREDITO", Map.of("limiteCredito", 100, "diaFechamento", 10, "diaVencimento", 17));
        String debito = cartao("DEBITO", null);
        String voucher = cartao("VOUCHER", Map.of("tipoVoucher", "VA_VR", "valorRecarga", 100, "diaRecarga", 10));
        rejeitarMovimentacao("PIX", saida, "1000.01", null, 1);
        rejeitarMovimentacao("CARTAO_CREDITO", saida, "100.01", credito, 1);
        rejeitarMovimentacao("CARTAO_CREDITO", entrada, "1.00", credito, 1);
        rejeitarMovimentacao("CARTAO_CREDITO", saida, "0.01", credito, 2);
        rejeitarMovimentacao("CARTAO_CREDITO", saida, "1.00", credito, null);
        rejeitarMovimentacao("CARTAO_CREDITO", saida, "1.00", debito, 1);
        rejeitarMovimentacao("CARTAO_DEBITO", saida, "1.00", null, 1);
        rejeitarMovimentacao("VOUCHER", saida, "1.00", voucher, 1);
        rejeitarMovimentacao("PIX", saida, "1.00", credito, 1);
        rejeitarMovimentacao("PIX", saida, "1.00", null, 2);
        for (String valor : List.of("0", "-1", "1.001")) {
            rejeitarMovimentacao("PIX", saida, valor, null, 1);
        }
        for (int parcelas : List.of(0, -1, 121)) {
            rejeitarMovimentacao("CARTAO_CREDITO", saida, "1.00", credito, parcelas);
        }
        verificarConta("1000.00", "1000.00");
        assertThat(get("/contas-bancarias/" + conta + "/lancamentos" + PERIODO).size()).isZero();
        assertThat(get("/cartoes/" + credito + "/faturas" + PERIODO).size()).isZero();
    }

    @Test
    void reverterOperacaoInteiraQuandoUmaParcelaEncontrarFaturaPaga() throws Exception {
        String credito = cartao("CREDITO", Map.of("limiteCredito", 1000, "diaFechamento", 10, "diaVencimento", 17));
        movimentar("CARTAO_CREDITO", saida, "30.00", credito, 1, "2024-02-05");
        String fatura = get("/cartoes/" + credito + "/faturas" + PERIODO).get(0).get("id").asText();
        post("/cartoes/faturas/" + fatura + "/pagamento", Map.of());
        rejeitarMovimentacao("CARTAO_CREDITO", saida, "90.00", credito, 3);
        verificarConta("970.00", "1000.00");
        assertThat(get("/contas-bancarias/" + conta + "/registros-de-movimentacao" + PERIODO).size()).isEqualTo(1);
        assertThat(get("/cartoes/" + credito + "/faturas" + PERIODO).size()).isEqualTo(1);
    }

    @Test
    void isolarConsultasEReferenciasEntreUsuarios() throws Exception {
        String credito = cartao("CREDITO", Map.of("limiteCredito", 1000, "diaFechamento", 10, "diaVencimento", 17));
        String registro = movimentar("CARTAO_CREDITO", saida, "30", credito, 1, "2024-01-05").get("id").asText();
        String fatura = get("/cartoes/" + credito + "/faturas" + PERIODO).get(0).get("id").asText();
        post("/investimentos", Map.of("categoria", "Reserva", "jurosAoMes", 1));
        post("/metas-financeiras", Map.of("objetivo", "Reserva", "valorAlvo", 100, "dataConclusaoPrevista", "2030-01"));
        token = criarUsuarioEAutenticar();
        for (String path : List.of("/contas-bancarias", "/categorias-movimentacao", "/investimentos", "/metas-financeiras")) {
            assertThat(get(path).size()).isZero();
        }
        for (String path : List.of(
            "/cartoes/" + conta, "/categorias-movimentacao/" + saida,
            "/categorias-movimentacao/" + saida + "/orcamentos",
            "/contas-bancarias/" + conta + "/lancamentos" + PERIODO,
            "/contas-bancarias/" + conta + "/registros-de-movimentacao" + PERIODO,
            "/contas-bancarias/registros-de-movimentacao/" + registro,
            "/cartoes/" + credito + "/faturas" + PERIODO, "/cartoes/faturas/" + fatura
        )) {
            request("GET", path, null, token, 404);
        }
        request("POST", "/cartoes/faturas/" + fatura + "/pagamento", Map.of(), token, 404);
        request("DELETE", "/contas-bancarias/registros-de-movimentacao/" + registro, null, token, 404);
        request("POST", "/contas-bancarias/" + conta + "/registros-de-movimentacao",
            payload("PIX", saida, "1", null, 1, "2024-01-05"), token, 404);
        categoria("Alimentação", "SAIDA");
        String outraConta = post("/contas-bancarias", Map.of("nomeDoBanco", "Outra", "creditoTotal", 1000)).get("id").asText();
        request("POST", "/contas-bancarias/" + outraConta + "/registros-de-movimentacao",
            payload("PIX", saida, "1", null, 1, "2024-01-05"), token, 404);
    }

    @Test
    void impedirPerdaDeAtualizacaoComSaidasConcorrentes() throws Exception {
        List<CompletableFuture<HttpResponse<String>>> respostas = new ArrayList<>();
        String body = mapper.writeValueAsString(payload("PIX", saida, "10.00", null, 1, "2024-01-05"));
        for (int i = 0; i < 12; i++) {
            respostas.add(client.sendAsync(builder("/contas-bancarias/" + conta + "/registros-de-movimentacao", token)
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString()));
        }
        for (CompletableFuture<HttpResponse<String>> resposta : respostas) {
            assertThat(resposta.join().statusCode()).isEqualTo(200);
        }
        verificarConta("880.00", "1000.00");
        assertThat(get("/contas-bancarias/" + conta + "/lancamentos" + PERIODO).size()).isEqualTo(12);
    }

    @Test
    void pagarFaturaConcorrentementeUmaUnicaVez() throws Exception {
        String credito = cartao("CREDITO", Map.of("limiteCredito", 1000, "diaFechamento", 10, "diaVencimento", 17));
        movimentar("CARTAO_CREDITO", saida, "100.00", credito, 1, "2024-01-05");
        String fatura = get("/cartoes/" + credito + "/faturas" + PERIODO).get(0).get("id").asText();
        List<CompletableFuture<HttpResponse<String>>> respostas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            respostas.add(client.sendAsync(builder("/cartoes/faturas/" + fatura + "/pagamento", token)
                .POST(HttpRequest.BodyPublishers.ofString("{}")).build(), HttpResponse.BodyHandlers.ofString()));
        }
        for (CompletableFuture<HttpResponse<String>> resposta : respostas) {
            assertThat(resposta.join().statusCode()).isEqualTo(200);
        }
        verificarConta("900.00", "1000.00");
    }

    @Test
    void validarDatasCartoesPeriodoEAutenticacao() throws Exception {
        Map<String, Object> data = payload("PIX", saida, "1", null, 1, LocalDate.now().plusDays(1).toString());
        request("POST", "/contas-bancarias/" + conta + "/registros-de-movimentacao", data, token, 400);
        data.remove("dataLancamento");
        request("POST", "/contas-bancarias/" + conta + "/registros-de-movimentacao", data, token, 400);
        request("GET", "/contas-bancarias/" + conta + "/lancamentos?dataInicial=2025-02-01&dataFinal=2025-01-01", null, token, 400);
        request("GET", "/contas-bancarias/" + conta + "/lancamentos?dataInicial=invalida", null, token, 400);
        request("POST", "/cartoes", Map.of("tipoCartao", "CREDITO", "contaBancariaId", conta, "apelido", "Inválido",
            "cartaoCredito", Map.of("limiteCredito", 10, "diaFechamento", 32, "diaVencimento", 0)), token, 400);
        request("POST", "/contas-bancarias", Map.of("nomeDoBanco", "Inválida", "creditoTotal", 10, "creditoRestante", 11), token, 400);
        request("GET", "/auth/me", null, null, 401);
        request("GET", "/auth/me", null, "token-invalido", 401);
        request("POST", "/auth/acessar", Map.of("email", "inexistente@example.com", "senha", "invalida"), null, 401);
        HttpResponse<String> preflight = client.send(builder("/contas-bancarias", null)
            .header("Origin", "http://localhost:3000").header("Access-Control-Request-Method", "POST")
            .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
        assertThat(preflight.statusCode()).isEqualTo(200);
        assertThat(preflight.headers().firstValue("Access-Control-Allow-Origin")).contains("http://localhost:3000");
    }

    private String criarUsuarioEAutenticar() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        usuarios.save(new Usuario("Usuário do teste", email, passwordEncoder.encode("senha-dos-testes"), PapelDoUsuarioEnum.PADRAO));
        return request("POST", "/auth/acessar", Map.of("email", email, "senha", "senha-dos-testes"), null, 200)
            .get("token").get("accessToken").asText();
    }

    private String categoria(String nome, String tipo) throws Exception {
        return post("/categorias-movimentacao", Map.of("nome", nome, "tipo", tipo, "cor", "#112233", "icone", "wallet")).get("id").asText();
    }

    private String cartao(String tipo, Map<String, Object> detalhes) throws Exception {
        Map<String, Object> body = new java.util.HashMap<>(Map.of("tipoCartao", tipo, "contaBancariaId", conta, "apelido", tipo));
        if (detalhes != null) {
            body.put(tipo.equals("CREDITO") ? "cartaoCredito" : "cartaoVoucher", detalhes);
        }
        return post("/cartoes", body).get("id").asText();
    }

    private Map<String, Object> payload(String metodo, String categoria, String valor, String cartao, Integer parcelas, String data) {
        Map<String, Object> body = new java.util.HashMap<>(Map.of(
            "metodoPagamento", metodo, "categoriaId", categoria, "valorTotal", new BigDecimal(valor),
            "dataLancamento", data, "descricao", "Movimentação de validação"));
        if (cartao != null) body.put("cartaoId", cartao);
        if (parcelas != null) body.put("quantidadeParcelas", parcelas);
        return body;
    }

    private JsonNode movimentar(String metodo, String categoria, String valor, String cartao, Integer parcelas, String data) throws Exception {
        return post("/contas-bancarias/" + conta + "/registros-de-movimentacao", payload(metodo, categoria, valor, cartao, parcelas, data));
    }

    private void rejeitarMovimentacao(String metodo, String categoria, String valor, String cartao, Integer parcelas) throws Exception {
        request("POST", "/contas-bancarias/" + conta + "/registros-de-movimentacao",
            payload(metodo, categoria, valor, cartao, parcelas, "2024-01-05"), token, 400);
    }

    private void verificarConta(String saldo, String limite) throws Exception {
        JsonNode resultado = get("/contas-bancarias").get(0);
        assertThat(resultado.get("saldoCorrente").decimalValue()).isEqualByComparingTo(saldo);
        assertThat(resultado.get("creditoRestante").decimalValue()).isEqualByComparingTo(limite);
    }

    private JsonNode get(String path) throws Exception {
        return request("GET", path, null, token, 200);
    }

    private JsonNode post(String path, Object body) throws Exception {
        return request("POST", path, body, token, 200);
    }

    private HttpRequest.Builder builder(String path, String accessToken) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api" + path))
            .header("Content-Type", "application/json").timeout(java.time.Duration.ofSeconds(30));
        if (accessToken != null) builder.header("Authorization", "Bearer " + accessToken);
        return builder;
    }

    private JsonNode request(String method, String path, Object body, String accessToken, int status) throws Exception {
        HttpRequest.BodyPublisher publisher = body == null ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body));
        HttpResponse<String> response = client.send(builder(path, accessToken).method(method, publisher).build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).as("%s %s: %s", method, path, response.body()).isEqualTo(status);
        return response.body().isBlank() ? mapper.nullNode() : mapper.readTree(response.body());
    }
}
