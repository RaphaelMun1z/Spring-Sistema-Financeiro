package io.github.raphaelmun1z.gestao_financeira.config;

import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.Investimento;
import io.github.raphaelmun1z.gestao_financeira.entities.MetaFinanceira;
import io.github.raphaelmun1z.gestao_financeira.entities.Orcamento;
import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoDebito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.PapelDoUsuarioEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoVoucherEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.PagamentoFatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.CategoriaMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.ContaBancariaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.InvestimentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.MetaFinanceiraRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.PagamentoFaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.OrcamentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import io.github.raphaelmun1z.gestao_financeira.services.SnapshotFinanceiroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.List;

@Configuration
@Profile("dev & !prod")
@ConditionalOnProperty(prefix = "app.data-mock", name = "enabled", havingValue = "true")
public class DataMock implements CommandLineRunner {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String senhaUsuarioMock;
    private final ContaBancariaRepository contaRepository;
    private final CategoriaMovimentacaoRepository categoriaRepository;
    private final CartaoRepository cartaoRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;
    private final LancamentoRepository lancamentoRepository;
    private final FaturaRepository faturaRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final InvestimentoRepository investimentoRepository;
    private final MetaFinanceiraRepository metaRepository;
    private final PagamentoFaturaRepository pagamentoRepository;
    private final SnapshotFinanceiroService snapshotFinanceiroService;

    public DataMock(
        UsuarioRepository repository,
        PasswordEncoder passwordEncoder,
        @Value("${INITIAL_USER_PASSWORD:admin123}") String senhaUsuarioMock,
        ContaBancariaRepository contaRepository,
        CategoriaMovimentacaoRepository categoriaRepository,
        CartaoRepository cartaoRepository,
        RegistroDeMovimentacaoRepository registroRepository,
        LancamentoRepository lancamentoRepository,
        FaturaRepository faturaRepository,
        OrcamentoRepository orcamentoRepository,
        InvestimentoRepository investimentoRepository,
        MetaFinanceiraRepository metaRepository,
        PagamentoFaturaRepository pagamentoRepository,
        SnapshotFinanceiroService snapshotFinanceiroService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.senhaUsuarioMock = senhaUsuarioMock;
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
        this.cartaoRepository = cartaoRepository;
        this.registroRepository = registroRepository;
        this.lancamentoRepository = lancamentoRepository;
        this.faturaRepository = faturaRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.investimentoRepository = investimentoRepository;
        this.metaRepository = metaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.snapshotFinanceiroService = snapshotFinanceiroService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Usuario usuario = repository.findByEmail("admin@gmail.com").orElseGet(() -> repository.save(new Usuario(
            "Administrador", "admin@gmail.com", passwordEncoder.encode(senhaUsuarioMock), PapelDoUsuarioEnum.PADRAO
        )));

        LocalDate hoje = LocalDate.now();
        LocalDate inicioHistoricoMock = hoje.minusMonths(1).withDayOfMonth(1);
        var contasMock = contaRepository.findByTitularId(usuario.getId()).stream()
            .filter(conta -> conta.getNomeDoBanco().endsWith("(Mock)"))
            .toList();
        if (contaRepository.findByTitularId(usuario.getId()).stream()
            .anyMatch(conta -> conta.getNomeDoBanco().equals("Banco Aurora (Mock)"))) {
            contasMock.forEach(conta -> {
                if (conta.getDataAbertura().isAfter(inicioHistoricoMock)) {
                    conta.setDataAbertura(inicioHistoricoMock);
                }
            });
            snapshotFinanceiroService.fecharMesAnterior(hoje);
            return;
        }

        ContaBancaria aurora = contaRepository.save(new ContaBancaria(
            "Banco Aurora (Mock)", new BigDecimal("3500.00"), new BigDecimal("8000.00"),
            new BigDecimal("8000.00"), usuario));
        ContaBancaria horizonte = contaRepository.save(new ContaBancaria(
            "Banco Horizonte (Mock)", new BigDecimal("1200.00"), BigDecimal.ZERO,
            BigDecimal.ZERO, usuario));
        aurora.setDataAbertura(inicioHistoricoMock);
        horizonte.setDataAbertura(inicioHistoricoMock);

        Map<String, CategoriaDeMovimentacao> categorias = Map.of(
            "Salário", categoriaRepository.save(new CategoriaDeMovimentacao("Salário", TipoCategoriaEnum.ENTRADA, "#22C55E", "briefcase", usuario)),
            "Moradia", categoriaRepository.save(new CategoriaDeMovimentacao("Moradia", TipoCategoriaEnum.SAIDA, "#EF4444", "house", usuario)),
            "Alimentação", categoriaRepository.save(new CategoriaDeMovimentacao("Alimentação", TipoCategoriaEnum.SAIDA, "#F59E0B", "utensils", usuario)),
            "Transporte", categoriaRepository.save(new CategoriaDeMovimentacao("Transporte", TipoCategoriaEnum.SAIDA, "#3B82F6", "car", usuario)),
            "Assinaturas", categoriaRepository.save(new CategoriaDeMovimentacao("Assinaturas", TipoCategoriaEnum.SAIDA, "#8B5CF6", "repeat", usuario)),
            "Freelance", categoriaRepository.save(new CategoriaDeMovimentacao("Freelance", TipoCategoriaEnum.ENTRADA, "#14B8A6", "laptop", usuario))
        );

        CartaoCredito credito = cartaoRepository.save(new CartaoCredito(
            "Cartão Principal (Mock)", aurora, new BigDecimal("8000.00"), 10, 17));
        cartaoRepository.save(new CartaoDebito("Débito Diário (Mock)", aurora));
        CartaoVoucher voucher = cartaoRepository.save(new CartaoVoucher(
            "Vale Alimentação (Mock)", horizonte, 5, new BigDecimal("500.00"),
            new BigDecimal("400.00"), TipoVoucherEnum.VA_VR));

        orcamentoRepository.save(new Orcamento(YearMonth.from(hoje), new BigDecimal("1800.00"), new BigDecimal("650.00"), categorias.get("Moradia")));
        orcamentoRepository.save(new Orcamento(YearMonth.from(hoje), new BigDecimal("900.00"), new BigDecimal("310.00"), categorias.get("Alimentação")));
        orcamentoRepository.save(new Orcamento(YearMonth.from(hoje), new BigDecimal("500.00"), new BigDecimal("160.00"), categorias.get("Transporte")));

        registrarSaldo(aurora, categorias.get("Salário"), new BigDecimal("5200.00"), MetodoPagamentoEnum.PIX, hoje.minusDays(2), "Salário mensal");
        registrarSaldo(aurora, categorias.get("Moradia"), new BigDecimal("1800.00"), MetodoPagamentoEnum.PIX, hoje.minusDays(10), "Aluguel");
        registrarSaldo(aurora, categorias.get("Transporte"), new BigDecimal("160.00"), MetodoPagamentoEnum.CARTAO_DEBITO, hoje.minusDays(3), "Combustível");
        registrarSaldo(aurora, categorias.get("Assinaturas"), new BigDecimal("49.90"), MetodoPagamentoEnum.PIX, hoje.minusMonths(1), "Streaming recorrente", true);
        registrarSaldo(horizonte, categorias.get("Freelance"), new BigDecimal("900.00"), MetodoPagamentoEnum.PIX, hoje.minusDays(5), "Projeto freelance");
        registrarVoucher(horizonte, voucher, categorias.get("Alimentação"), new BigDecimal("85.00"), hoje.minusDays(1), "Almoço");

        registrarCredito(credito, aurora, categorias.get("Alimentação"), new BigDecimal("450.00"), 3, hoje.minusDays(8), "Supermercado parcelado");
        registrarCredito(credito, aurora, categorias.get("Transporte"), new BigDecimal("280.00"), 2, hoje.minusMonths(2), "Viagem parcelada");
        List<Fatura> faturasPagas = registrarCredito(credito, aurora, categorias.get("Alimentação"),
            new BigDecimal("120.00"), 1, hoje.minusMonths(2), "Compra já paga");
        pagarMock(faturasPagas.get(0), aurora, hoje.minusDays(2));

        investimentoRepository.save(new Investimento("Reserva de emergência", hoje.minusMonths(8), new BigDecimal("5000.00"), new BigDecimal("5400.00"), 0.8f, usuario));
        investimentoRepository.save(new Investimento("CDB de longo prazo", hoje.minusMonths(3), new BigDecimal("2500.00"), new BigDecimal("2575.00"), 1.0f, usuario));
        metaRepository.save(new MetaFinanceira("Reserva de emergência", new BigDecimal("10000.00"), new BigDecimal("5400.00"), YearMonth.from(hoje).plusMonths(12), usuario));
        metaRepository.save(new MetaFinanceira("Viagem de férias", new BigDecimal("6000.00"), new BigDecimal("1800.00"), YearMonth.from(hoje).plusMonths(8), usuario));
        snapshotFinanceiroService.fecharMesAnterior(hoje);
    }

    private void registrarSaldo(ContaBancaria conta, CategoriaDeMovimentacao categoria, BigDecimal valor,
                                MetodoPagamentoEnum metodo, LocalDate data, String descricao) {
        registrarSaldo(conta, categoria, valor, metodo, data, descricao, false);
    }

    private void registrarSaldo(ContaBancaria conta, CategoriaDeMovimentacao categoria, BigDecimal valor,
                                MetodoPagamentoEnum metodo, LocalDate data, String descricao, boolean recorrente) {
        Cartao cartao = metodo == MetodoPagamentoEnum.CARTAO_DEBITO
            ? cartaoRepository.findByContaBancaria_Id(conta.getId()).stream()
                .filter(item -> item instanceof CartaoDebito)
                .findFirst()
                .orElse(null)
            : null;
        RegistroDeMovimentacao registro = registroRepository.save(new RegistroDeMovimentacao(
            valor, data, descricao, metodo, recorrente, 1, categoria, conta, cartao));
        BigDecimal variacao = categoria.getTipo() == TipoCategoriaEnum.ENTRADA ? valor : valor.negate();
        conta.setSaldoCorrente(conta.getSaldoCorrente().add(variacao));
        lancamentoRepository.save(new Lancamento(valor, data, registro));
    }

    private void registrarVoucher(ContaBancaria conta, CartaoVoucher voucher, CategoriaDeMovimentacao categoria,
                                  BigDecimal valor, LocalDate data, String descricao) {
        RegistroDeMovimentacao registro = registroRepository.save(new RegistroDeMovimentacao(
            valor, data, descricao, MetodoPagamentoEnum.VOUCHER, false, 1, categoria, conta, voucher));
        voucher.setSaldoCorrente(voucher.getSaldoCorrente().subtract(valor));
        lancamentoRepository.save(new Lancamento(valor, data, registro));
    }

    private List<Fatura> registrarCredito(CartaoCredito cartao, ContaBancaria conta, CategoriaDeMovimentacao categoria,
                                           BigDecimal total, int parcelas, LocalDate data, String descricao) {
        RegistroDeMovimentacao registro = registroRepository.save(new RegistroDeMovimentacao(
            total, data, descricao, MetodoPagamentoEnum.CARTAO_CREDITO, false, parcelas, categoria, conta, cartao));
        conta.setCreditoRestante(conta.getCreditoRestante().subtract(total));
        BigDecimal base = total.divide(BigDecimal.valueOf(parcelas), 2, RoundingMode.DOWN);
        int centavosRestantes = total.subtract(base.multiply(BigDecimal.valueOf(parcelas)))
            .movePointRight(2).intValueExact();
        YearMonth referencia = cartao.calcularReferencia(data);
        List<Fatura> faturas = new java.util.ArrayList<>();
        for (int mes = 0; mes < parcelas; mes++) {
            YearMonth mesReferencia = referencia.plusMonths(mes);
            Fatura fatura = faturaRepository.findByCartaoDeCredito_IdAndDataReferencia(cartao.getId(), mesReferencia)
                .orElseGet(() -> faturaRepository.save(new Fatura(mesReferencia, cartao)));
            BigDecimal valor = mes < centavosRestantes ? base.add(new BigDecimal("0.01")) : base;
            Lancamento lancamento = lancamentoRepository.save(new Lancamento(valor, fatura.getDataVencimento(), registro, fatura, mes + 1));
            fatura.getLancamentos().add(lancamento);
            fatura.recalcular();
            faturas.add(fatura);
        }
        return faturas;
    }

    private void pagarMock(Fatura fatura, ContaBancaria conta, LocalDate data) {
        fatura.pagar();
        conta.setSaldoCorrente(conta.getSaldoCorrente().subtract(fatura.getValorTotal()));
        conta.setCreditoRestante(conta.getCreditoRestante().add(fatura.getValorTotal()));
        pagamentoRepository.save(new PagamentoFatura(fatura, conta, fatura.getValorTotal(), data));
    }
}
