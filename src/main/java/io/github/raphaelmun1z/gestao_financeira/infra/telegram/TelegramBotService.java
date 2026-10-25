package io.github.raphaelmun1z.gestao_financeira.infra.telegram;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.RegistroMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.repositories.CategoriaMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.ContaBancariaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import io.github.raphaelmun1z.gestao_financeira.services.RegistroDeMovimentacaoService;
import io.github.raphaelmun1z.gestao_financeira.services.TelegramVinculoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@ConditionalOnProperty(prefix = "telegram.bot", name = "enabled", havingValue = "true")
public class TelegramBotService {
    private static final Pattern UPDATE = Pattern.compile(
        "\\\"update_id\\\":(\\d+).*?\\\"chat\\\":\\{\\\"id\\\":(-?\\d+).*?\\\"text\\\":\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"",
        Pattern.DOTALL
    );
    private static final Pattern CALLBACK = Pattern.compile(
        "\\\"update_id\\\":(\\d+).*?\\\"callback_query\\\":\\{.*?\\\"id\\\":\\\"([^\\\"]+)\\\".*?\\\"chat\\\":\\{\\\"id\\\":(-?\\d+).*?\\\"data\\\":\\\"([^\\\"]+)\\\"",
        Pattern.DOTALL
    );
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final UsuarioRepository usuarioRepository;
    private final TelegramVinculoService vinculoService;
    private final ContaBancariaRepository contaRepository;
    private final CategoriaMovimentacaoRepository categoriaRepository;
    private final CartaoRepository cartaoRepository;
    private final FaturaRepository faturaRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;
    private final RegistroDeMovimentacaoService movimentacaoService;
    private final String token;
    private final Map<Long, PendingMovement> pending = new ConcurrentHashMap<>();
    private final Map<Long, Conversation> conversations = new ConcurrentHashMap<>();
    private long offset;

    public TelegramBotService(
        UsuarioRepository usuarioRepository,
        TelegramVinculoService vinculoService,
        ContaBancariaRepository contaRepository,
        CategoriaMovimentacaoRepository categoriaRepository,
        CartaoRepository cartaoRepository,
        FaturaRepository faturaRepository,
        RegistroDeMovimentacaoRepository registroRepository,
        RegistroDeMovimentacaoService movimentacaoService,
        @Value("${telegram.bot.token}") String token
    ) {
        this.usuarioRepository = usuarioRepository;
        this.vinculoService = vinculoService;
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
        this.cartaoRepository = cartaoRepository;
        this.faturaRepository = faturaRepository;
        this.registroRepository = registroRepository;
        this.movimentacaoService = movimentacaoService;
        this.token = token;
    }

    @Scheduled(fixedDelayString = "${telegram.bot.poll-interval-ms:1500}")
    public void poll() {
        try {
            String response = call("getUpdates?offset=" + offset + "&timeout=1");
            Set<Long> callbackUpdates = new HashSet<>();
            Matcher callback = CALLBACK.matcher(response);
            while (callback.find()) {
                long updateId = Long.parseLong(callback.group(1));
                callbackUpdates.add(updateId);
                offset = Math.max(offset, updateId + 1);
                processCallback(callback.group(2), Long.parseLong(callback.group(3)), callback.group(4));
            }
            Matcher matcher = UPDATE.matcher(response);
            while (matcher.find()) {
                long updateId = Long.parseLong(matcher.group(1));
                if (!callbackUpdates.contains(updateId)) {
                    offset = Math.max(offset, updateId + 1);
                    process(Long.parseLong(matcher.group(2)), unescape(matcher.group(3)));
                }
            }
        } catch (Exception ex) {
            // A falha temporária no Telegram não deve derrubar a aplicação.
        }
    }

    private void process(Long chatId, String text) {
        if (text.startsWith("/start")) {
            Usuario usuarioVinculado = usuarioRepository.findByTelegramChatId(chatId).orElse(null);
            if (usuarioVinculado == null) {
                send(chatId, "Gere um codigo em POST /api/telegram/vinculo e envie /vincular CODIGO aqui.");
            } else {
                iniciarSessao(chatId, usuarioVinculado);
            }
            return;
        }
        if (text.startsWith("/vincular ")) {
            try {
                Usuario usuario = vinculoService.vincular(chatId, text.substring(10).trim());
                iniciarSessao(chatId, usuario);
            } catch (RuntimeException ex) {
                send(chatId, ex.getMessage());
            }
            return;
        }
        Usuario usuario = usuarioRepository.findByTelegramChatId(chatId).orElse(null);
        if (usuario == null) {
            send(chatId, "Telegram não vinculado. Use /vincular CODIGO.");
            return;
        }
        if (text.equalsIgnoreCase("/menu")) {
            menu(chatId);
            return;
        }
        if (text.equalsIgnoreCase("/cancelar")) {
            pending.remove(chatId);
            conversations.remove(chatId);
            send(chatId, "Operação cancelada.");
            return;
        }
        if (text.equalsIgnoreCase("/registrar")) {
            iniciarConversa(chatId, usuario);
            return;
        }
        String data = text;
        if (data.startsWith("menu:")) {
            Usuario usuarioMenu = usuarioRepository.findByTelegramChatId(chatId).orElse(null);
            if (usuario == null) { send(chatId, "Telegram nao vinculado."); return; }
            switch (data.substring(5)) {
                case "registrar" -> iniciarConversa(chatId, usuarioMenu);
                case "resumo" -> mostrarResumo(chatId, usuarioMenu);
                case "contas" -> mostrarContas(chatId, usuarioMenu);
                case "movimentacoes" -> mostrarMovimentacoes(chatId, usuarioMenu);
                case "faturas" -> mostrarFaturas(chatId, usuarioMenu);
                default -> menu(chatId);
            }
            return;
        }
        Conversation conversation = conversations.get(chatId);
        if (conversation != null) {
            processConversationText(chatId, conversation, text);
            return;
        }
        if (text.equalsIgnoreCase("/confirmar")) {
            confirmar(chatId, usuario);
            return;
        }
        if (text.startsWith("/registrar ")) {
            preparar(chatId, usuario, text.substring(11).trim());
            return;
        }
        send(chatId, "Formato: /registrar SAIDA|45,00|Categoria|Conta|Descrição");
    }

    private void iniciarConversa(Long chatId, Usuario usuario) {
        Conversation conversation = new Conversation(usuario);
        conversations.put(chatId, conversation);
        sendKeyboard(chatId, "Escolha o tipo de movimentação:", keyboard(
            button("Entrada", "mov:type:ENTRADA"), button("Saída", "mov:type:SAIDA")
        ));
    }

    private void menu(Long chatId) {
        sendKeyboard(chatId, "Escolha uma opcao:", keyboard(
            button("Registrar movimentacao", "menu:registrar"),
            button("Resumo financeiro", "menu:resumo"),
            button("Listar contas", "menu:contas"),
            button("Ultimas movimentacoes", "menu:movimentacoes"),
            button("Faturas", "menu:faturas")
        ));
    }

    private void iniciarSessao(Long chatId, Usuario usuario) {
        send(chatId, "Ola, " + usuario.getNomeCompleto() + "! Seu Telegram foi vinculado com sucesso.");
        send(chatId, "\"" + fraseMotivacional() + "\"");
        menu(chatId);
    }

    private void mostrarContas(Long chatId, Usuario usuario) {
        List<ContaBancaria> contas = contaRepository.findByTitularId(usuario.getId());
        String texto = contas.isEmpty() ? "Nenhuma conta cadastrada." : contas.stream()
            .map(conta -> "• " + conta.getNomeDoBanco() + ": R$ " + conta.getSaldoCorrente())
            .reduce((a, b) -> a + "\n" + b).orElse("");
        sendKeyboard(chatId, texto, keyboard(button("Voltar ao menu", "menu:root")));
    }

    private void mostrarResumo(Long chatId, Usuario usuario) {
        List<ContaBancaria> contas = contaRepository.findByTitularId(usuario.getId());
        List<Fatura> faturas = faturaRepository.findByTitularId(usuario.getId());
        Map<String, BigDecimal> faturasPorConta = new HashMap<>();
        for (Fatura fatura : faturas) {
            if (fatura.getStatus() != io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum.PAGA) {
                String contaId = fatura.getCartaoDeCredito().getContaBancariaId();
                faturasPorConta.merge(contaId, fatura.getValorTotal(), BigDecimal::add);
            }
        }

        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        NumberFormat percentual = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
        percentual.setMinimumFractionDigits(2);
        percentual.setMaximumFractionDigits(2);
        StringBuilder texto = new StringBuilder();
        for (int index = 0; index < contas.size(); index++) {
            ContaBancaria conta = contas.get(index);
            BigDecimal usado = conta.getCreditoTotal().subtract(conta.getCreditoRestante());
            BigDecimal percentualUsado = conta.getCreditoTotal().signum() == 0
                ? BigDecimal.ZERO
                : usado.multiply(BigDecimal.valueOf(100)).divide(conta.getCreditoTotal(), 2, java.math.RoundingMode.HALF_UP);
            if (index > 0) texto.append("\n\n");
            texto.append("Conta ").append(index + 1).append("\n")
                .append("Nome: ").append(conta.getNomeDoBanco()).append("\n")
                .append("Saldo: ").append(moeda.format(conta.getSaldoCorrente())).append("\n")
                .append("Credito restante: ").append(moeda.format(conta.getCreditoRestante())).append("\n")
                .append("Credito usado: ").append(percentual.format(percentualUsado)).append("%\n")
                .append("Faturas pendentes: ").append(moeda.format(
                    faturasPorConta.getOrDefault(conta.getId(), BigDecimal.ZERO)
                ));
        }
        sendKeyboard(chatId, contas.isEmpty() ? "Nenhuma conta cadastrada." : texto.toString(),
            keyboard(button("Voltar ao menu", "menu:root")));
    }

    private String fraseMotivacional() {
        List<String> frases = List.of(
            "Cada pequena escolha hoje constroi um futuro mais tranquilo.",
            "Organizar suas financas e um passo de cada vez.",
            "Seu planejamento de hoje e a liberdade de amanha.",
            "Clareza financeira transforma metas em conquistas.",
            "Voce esta no controle: acompanhe, planeje e avance."
        );
        return frases.get(ThreadLocalRandom.current().nextInt(frases.size()));
    }

    private void mostrarCategorias(Long chatId, Usuario usuario) {
        List<CategoriaDeMovimentacao> categorias = categoriaRepository.findByAutorId(usuario.getId());
        String texto = categorias.isEmpty() ? "Nenhuma categoria cadastrada." : categorias.stream()
            .map(item -> "• " + item.getNome() + " (" + item.getTipo() + ")")
            .reduce((a, b) -> a + "\n" + b).orElse("");
        sendKeyboard(chatId, texto, keyboard(button("Voltar ao menu", "menu:root")));
    }

    private void mostrarMovimentacoes(Long chatId, Usuario usuario) {
        List<RegistroDeMovimentacao> registros = registroRepository
            .findTop10ByContaBancaria_TitularIdOrderByDataLancamentoDesc(usuario.getId());
        String texto = registros.isEmpty() ? "Nenhuma movimentacao cadastrada." : formatarMovimentacoes(registros);
        sendKeyboard(chatId, texto, keyboard(button("Voltar ao menu", "menu:root")));
    }

    private String formatarMovimentacoes(List<RegistroDeMovimentacao> registros) {
        DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        StringBuilder texto = new StringBuilder();
        for (int index = 0; index < registros.size(); index++) {
            RegistroDeMovimentacao registro = registros.get(index);
            if (index > 0) texto.append("\n\n");
            texto.append("Movimentacao ").append(index + 1).append("\n")
                .append("Data: ").append(registro.getDataLancamento().format(dataFormatada)).append("\n")
                .append("Valor: ").append(moeda.format(registro.getValorTotal())).append("\n")
                .append("Tipo: ").append(registro.getCategoria().getTipo()).append("\n")
                .append("Categoria: ").append(registro.getCategoria().getNome()).append("\n")
                .append("Conta: ").append(registro.getContaBancaria().getNomeDoBanco()).append("\n")
                .append("Pagamento: ").append(registro.getMetodoPagamento()).append("\n")
                .append("Descricao: ").append(registro.getDescricao() == null || registro.getDescricao().isBlank()
                    ? "-" : registro.getDescricao());
        }
        return texto.toString();
    }

    private void mostrarFaturas(Long chatId, Usuario usuario) {
        List<Fatura> faturas = faturaRepository.findByTitularId(usuario.getId()).stream()
            .filter(item -> item.getStatus() != io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum.PAGA)
            .toList();
        String texto = faturas.isEmpty() ? "Nenhuma fatura pendente." : formatarFaturas(faturas);
        sendKeyboard(chatId, texto, keyboard(button("Voltar ao menu", "menu:root")));
    }

    private String formatarFaturas(List<Fatura> faturas) {
        DateTimeFormatter referenciaFormatada = DateTimeFormatter.ofPattern("MM/yyyy");
        DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        StringBuilder texto = new StringBuilder();
        for (int index = 0; index < faturas.size(); index++) {
            Fatura fatura = faturas.get(index);
            if (index > 0) texto.append("\n\n");
            texto.append("Fatura ").append(index + 1).append("\n")
                .append("Referencia: ").append(fatura.getDataReferencia().format(referenciaFormatada)).append("\n")
                .append("Vencimento: ").append(fatura.getDataVencimento().format(dataFormatada)).append("\n")
                .append("Valor: ").append(moeda.format(fatura.getValorTotal())).append("\n")
                .append("Status: ").append(fatura.getStatus());
        }
        return texto.toString();
    }

    private void processConversationText(Long chatId, Conversation conversation, String text) {
        if (conversation.step != Step.AMOUNT && conversation.step != Step.DESCRIPTION) {
            send(chatId, "Use os botões da mensagem ou /cancelar.");
            return;
        }
        if (conversation.step == Step.AMOUNT) {
            try {
                String normalized = text.trim().replace(" ", "");
                if (normalized.contains(",")) normalized = normalized.replace(".", "").replace(",", ".");
                conversation.amount = new BigDecimal(normalized);
                if (conversation.amount.signum() <= 0) throw new IllegalArgumentException();
                conversation.date = LocalDate.now();
                askCategory(chatId, conversation);
            } catch (Exception ex) {
                send(chatId, "Informe um valor válido, por exemplo: 45,00.");
            }
            return;
        }
        conversation.description = text.equals("-") ? null : text;
        conversation.step = Step.RECURRING;
        sendKeyboard(chatId, "Deseja tornar esta movimentação recorrente?", keyboard(
            button("Sim", "mov:recurring:true"), button("Não", "mov:recurring:false")
        ));
    }

    private void processCallback(String callbackId, Long chatId, String data) {
        answerCallback(callbackId);
        if (data.equals("mov:cancel")) {
            conversations.remove(chatId);
            pending.remove(chatId);
            send(chatId, "Operação cancelada.");
            return;
        }
        if (data.equals("mov:confirm")) {
            Usuario usuario = usuarioRepository.findByTelegramChatId(chatId).orElse(null);
            if (usuario == null) { send(chatId, "Telegram não vinculado."); return; }
            confirmar(chatId, usuario);
            return;
        }
        if (data.startsWith("menu:")) {
            Usuario usuarioMenu = usuarioRepository.findByTelegramChatId(chatId).orElse(null);
            if (usuarioMenu == null) { send(chatId, "Telegram nao vinculado."); return; }
            switch (data.substring(5)) {
                case "registrar" -> iniciarConversa(chatId, usuarioMenu);
                case "contas" -> mostrarContas(chatId, usuarioMenu);
                case "categorias" -> mostrarCategorias(chatId, usuarioMenu);
                case "movimentacoes" -> mostrarMovimentacoes(chatId, usuarioMenu);
                case "faturas" -> mostrarFaturas(chatId, usuarioMenu);
                default -> menu(chatId);
            }
            return;
        }
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) { send(chatId, "Nenhuma movimentação em andamento. Envie /registrar."); return; }
        String[] parts = data.split(":", 3);
        if (parts.length < 3 || !parts[0].equals("mov")) return;
        switch (parts[1]) {
            case "type" -> {
                conversation.type = parts[2];
                conversation.step = Step.AMOUNT;
                send(chatId, "Informe o valor da movimentação:");
            }
            case "category" -> {
                conversation.categoryId = parts[2];
                conversation.step = Step.ACCOUNT;
                List<String[]> buttons = contaRepository.findByTitularId(conversation.usuario.getId()).stream()
                    .map(item -> button(item.getNomeDoBanco(), "mov:account:" + item.getId())).toList();
                sendKeyboard(chatId, "Escolha a conta:", keyboard(buttons));
            }
            case "account" -> {
                conversation.accountId = parts[2];
                conversation.step = Step.PAYMENT;
                sendKeyboard(chatId, "Escolha o método de pagamento:", keyboard(
                    button("PIX", "mov:payment:PIX"), button("Dinheiro", "mov:payment:DINHEIRO"),
                    button("Cartão de débito", "mov:payment:CARTAO_DEBITO"),
                    button("Cartão de crédito", "mov:payment:CARTAO_CREDITO"), button("Voucher", "mov:payment:VOUCHER")
                ));
            }
            case "payment" -> selecionarPagamento(chatId, conversation, parts[2]);
            case "card" -> {
                conversation.cardId = parts[2];
                if (conversation.payment == MetodoPagamentoEnum.CARTAO_CREDITO) {
                    conversation.step = Step.INSTALLMENTS;
                    sendKeyboard(chatId, "Escolha o número de parcelas:", keyboard(
                        button("1x", "mov:installments:1"), button("2x", "mov:installments:2"),
                        button("3x", "mov:installments:3"), button("6x", "mov:installments:6"), button("12x", "mov:installments:12")
                    ));
                } else {
                    conversation.step = Step.DESCRIPTION;
                    send(chatId, "Digite uma descrição ou envie - para deixar sem descrição:");
                }
            }
            case "installments" -> {
                conversation.installments = Integer.valueOf(parts[2]);
                conversation.step = Step.DESCRIPTION;
                send(chatId, "Digite uma descrição ou envie - para deixar sem descrição:");
            }
            case "recurring" -> {
                conversation.recurring = Boolean.valueOf(parts[2]);
                conversations.remove(chatId);
                pending.put(chatId, conversation.toPending());
                sendKeyboard(chatId, conversation.summary(), keyboard(
                    button("Confirmar", "mov:confirm"), button("Cancelar", "mov:cancel")
                ));
            }
            default -> { }
        }
    }

    private void selecionarPagamento(Long chatId, Conversation conversation, String payment) {
        conversation.payment = MetodoPagamentoEnum.valueOf(payment);
        if (conversation.payment == MetodoPagamentoEnum.CARTAO_CREDITO || conversation.payment == MetodoPagamentoEnum.CARTAO_DEBITO || conversation.payment == MetodoPagamentoEnum.VOUCHER) {
            List<String[]> buttons = cartaoRepository.findByContaBancaria_Id(conversation.accountId).stream()
                .filter(card -> card.getClass().getSimpleName().equals(payment.equals("CARTAO_CREDITO") ? "CartaoCredito" : payment.equals("CARTAO_DEBITO") ? "CartaoDebito" : "CartaoVoucher"))
                .map(card -> button(card.getApelido(), "mov:card:" + card.getId())).toList();
            sendKeyboard(chatId, "Escolha o cartão:", keyboard(buttons));
            conversation.step = Step.CARD;
        } else {
            conversation.step = Step.DESCRIPTION;
            send(chatId, "Digite uma descrição ou envie - para deixar sem descrição:");
        }
    }

    private void askCategory(Long chatId, Conversation conversation) {
        conversation.step = Step.CATEGORY;
        List<String[]> buttons = categoriaRepository.findByAutorId(conversation.usuario.getId()).stream()
            .filter(item -> item.getTipo().name().equals(conversation.type))
            .map(item -> button(item.getNome(), "mov:category:" + item.getId())).toList();
        sendKeyboard(chatId, "Escolha a categoria:", keyboard(buttons));
    }

    private void preparar(Long chatId, Usuario usuario, String payload) {
        try {
            String[] partes = payload.split("\\|", 5);
            if (partes.length < 4) throw new IllegalArgumentException("Informe tipo, valor, categoria e conta.");
            boolean entrada = partes[0].trim().equalsIgnoreCase("ENTRADA");
            if (!entrada && !partes[0].trim().equalsIgnoreCase("SAIDA")) {
                throw new IllegalArgumentException("O tipo deve ser ENTRADA ou SAIDA.");
            }
            String valorTexto = partes[1].trim().replace(" ", "");
            if (valorTexto.contains(",")) {
                valorTexto = valorTexto.replace(".", "").replace(",", ".");
            }
            BigDecimal valor = new BigDecimal(valorTexto);
            CategoriaDeMovimentacao categoria = categoriaRepository.findByAutorId(usuario.getId()).stream()
                .filter(item -> item.getNome().equalsIgnoreCase(partes[2].trim()))
                .filter(item -> item.getTipo().name().equals(entrada ? "ENTRADA" : "SAIDA"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada ou incompatível."));
            ContaBancaria conta = contaRepository.findByTitularId(usuario.getId()).stream()
                .filter(item -> item.getNomeDoBanco().equalsIgnoreCase(partes[3].trim()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));
            RegistroMovimentacaoReqDTO dto = new RegistroMovimentacaoReqDTO(
                valor, MetodoPagamentoEnum.PIX, categoria.getId(), LocalDate.now(),
                partes.length == 5 ? partes[4].trim() : null, false, null, null
            );
            pending.put(chatId, new PendingMovement(usuario, conta.getId(), dto));
            send(chatId, "Confirmar " + partes[0].toUpperCase() + " de R$ " + valor + " em " + partes[2].trim() + "? Envie /confirmar ou /cancelar.");
        } catch (Exception ex) {
            send(chatId, ex.getMessage());
        }
    }

    private void confirmar(Long chatId, Usuario usuario) {
        PendingMovement movement = pending.remove(chatId);
        if (movement == null) {
            send(chatId, "Não há movimentação pendente.");
            return;
        }
        try {
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities())
            );
            movimentacaoService.cadastrar(movement.contaId(), movement.data());
            send(chatId, "Movimentação salva com sucesso.");
        } catch (Exception ex) {
            send(chatId, ex.getMessage());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private String call(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.telegram.org/bot" + token + "/" + path))
            .GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private void send(Long chatId, String text) {
        try {
            String body = "chat_id=" + chatId + "&text=" + URLEncoder.encode(text == null ? "Erro inesperado." : text, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + token + "/sendMessage"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
        }
    }

    private void sendKeyboard(Long chatId, String text, String markup) {
        try {
            String body = "chat_id=" + chatId
                + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8)
                + "&reply_markup=" + URLEncoder.encode(markup, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + token + "/sendMessage"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
        }
    }

    private void answerCallback(String callbackId) {
        try {
            String body = "callback_query_id=" + URLEncoder.encode(callbackId, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + token + "/answerCallbackQuery"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
        }
    }

    private String keyboard(List<String[]> buttons) {
        StringBuilder json = new StringBuilder("{\"inline_keyboard\":[");
        for (int index = 0; index < buttons.size(); index++) {
            if (index > 0) json.append(",");
            json.append("[{\"text\":\"").append(jsonEscape(buttons.get(index)[0]))
                .append("\",\"callback_data\":\"").append(jsonEscape(buttons.get(index)[1])).append("\"}");
            json.append("]");
        }
        return json.append("]}").toString();
    }

    private String keyboard(String[]... buttons) {
        return keyboard(List.of(buttons));
    }

    private String[] button(String text, String data) {
        return new String[]{text, data};
    }

    private String jsonEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String unescape(String value) {
        return value.replace("\\\\\"", "\"").replace("\\\\n", "\n").replace("\\\\\\", "\\");
    }

    private record PendingMovement(Usuario usuario, String contaId, RegistroMovimentacaoReqDTO data) {
    }

    private enum Step { AMOUNT, CATEGORY, ACCOUNT, PAYMENT, CARD, INSTALLMENTS, DESCRIPTION, RECURRING }

    private static final class Conversation {
        private final Usuario usuario;
        private Step step;
        private String type;
        private BigDecimal amount;
        private LocalDate date;
        private String categoryId;
        private String accountId;
        private MetodoPagamentoEnum payment;
        private String cardId;
        private Integer installments;
        private String description;
        private boolean recurring;

        private Conversation(Usuario usuario) {
            this.usuario = usuario;
        }

        private String summary() {
            return "Resumo:\n" + type + " de R$ " + amount + "\nMétodo: " + payment
                + (cardId == null ? "" : "\nCartão selecionado")
                + (installments == null ? "" : "\nParcelas: " + installments + "x")
                + "\nRecorrente: " + (recurring ? "sim" : "não")
                + "\n\nConfirma o registro?";
        }

        private PendingMovement toPending() {
            return new PendingMovement(usuario, accountId, new RegistroMovimentacaoReqDTO(
                amount, payment, categoryId, date, description, recurring,
                payment == MetodoPagamentoEnum.CARTAO_CREDITO ? installments : null, cardId
            ));
        }
    }
}
