package io.github.raphaelmun1z.gestao_financeira.config;

import io.github.raphaelmun1z.gestao_financeira.entities.*;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoDebito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.PapelDoUsuarioEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Configuration
//@Profile("dev")
public class DataMock implements CommandLineRunner {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ContaBancariaRepository contaBancariaRepository;
    private final CategoriaMovimentacaoRepository categoriaRepository;
    private final CartaoRepository cartaoRepository;
    private final FaturaRepository faturaRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final InvestimentoRepository investimentoRepository;
    private final MetaFinanceiraRepository metaFinanceiraRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;
    private final LancamentoRepository lancamentoRepository;

    public DataMock(
        UsuarioRepository repository,
        PasswordEncoder passwordEncoder,
        ContaBancariaRepository contaBancariaRepository,
        CategoriaMovimentacaoRepository categoriaRepository,
        CartaoRepository cartaoRepository,
        FaturaRepository faturaRepository,
        OrcamentoRepository orcamentoRepository,
        InvestimentoRepository investimentoRepository,
        MetaFinanceiraRepository metaFinanceiraRepository,
        RegistroDeMovimentacaoRepository registroRepository,
        LancamentoRepository lancamentoRepository
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.contaBancariaRepository = contaBancariaRepository;
        this.categoriaRepository = categoriaRepository;
        this.cartaoRepository = cartaoRepository;
        this.faturaRepository = faturaRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.investimentoRepository = investimentoRepository;
        this.metaFinanceiraRepository = metaFinanceiraRepository;
        this.registroRepository = registroRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // O mock de usuário foi mantido conforme já existia no projeto.
        String nome = "Administrador";
        String email = "admin@gmail.com";
        String senha = "admin123";
        String senhaHash = passwordEncoder.encode(senha);
        PapelDoUsuarioEnum papel = PapelDoUsuarioEnum.PADRAO;

        if (repository.findByEmail(email).isEmpty()) {
            repository.save(new Usuario(nome, email, senhaHash, papel));
        }

        Usuario usuario = repository.findByEmail(email).orElseThrow();
        ContaBancaria conta = criarConta(usuario);
        CategoriaDeMovimentacao entrada = criarCategoria("Salário", TipoCategoriaEnum.ENTRADA, "#22C55E", "wallet", usuario);
        CategoriaDeMovimentacao moradia = criarCategoria("Moradia", TipoCategoriaEnum.SAIDA, "#EF4444", "home", usuario);
        CategoriaDeMovimentacao alimentacao = criarCategoria("Alimentação", TipoCategoriaEnum.SAIDA, "#F59E0B", "utensils", usuario);

        CartaoCredito credito = criarCartaoCredito(conta);
        criarCartaoDebito(conta);
        criarCartaoVoucher(conta);

        YearMonth referencia = YearMonth.now().plusMonths(1);
        Fatura fatura = criarFatura(credito, referencia);
        criarOrcamento(moradia, referencia, BigDecimal.valueOf(1800));
        criarOrcamento(alimentacao, referencia, BigDecimal.valueOf(900));

        if (investimentoRepository.findAll().stream().noneMatch(item -> "Reserva de emergência".equals(item.getCategoria()))) {
            investimentoRepository.save(new Investimento(
                "Reserva de emergência",
                LocalDate.now().minusMonths(8),
                BigDecimal.valueOf(12000),
                BigDecimal.valueOf(12840),
                0.65F,
                usuario
            ));
        }

        criarMetaFinanceira(usuario);
        RegistroDeMovimentacao salario = criarRegistro(
            "Salário mensal", BigDecimal.valueOf(6500), entrada, conta,
            MetodoPagamentoEnum.PIX, false, null, null
        );
        RegistroDeMovimentacao supermercado = criarRegistro(
            "Compras do mês", BigDecimal.valueOf(420.75), alimentacao, conta,
            MetodoPagamentoEnum.CARTAO_CREDITO, false, 1, credito
        );
        criarLancamento(salario, BigDecimal.valueOf(6500), LocalDate.now().withDayOfMonth(5), null);
        criarLancamento(supermercado, BigDecimal.valueOf(420.75), LocalDate.now(), fatura);
    }

    private ContaBancaria criarConta(Usuario usuario) {
        return contaBancariaRepository.findAll().stream()
            .filter(item -> "Banco Exemplo".equals(item.getNomeDoBanco()) && item.getTitular().equals(usuario))
            .findFirst()
            .orElseGet(() -> contaBancariaRepository.save(new ContaBancaria(
                "Banco Exemplo",
                BigDecimal.valueOf(8200),
                BigDecimal.valueOf(6000),
                BigDecimal.valueOf(4579.25),
                usuario
            )));
    }

    private CategoriaDeMovimentacao criarCategoria(
        String nome,
        TipoCategoriaEnum tipo,
        String cor,
        String icone,
        Usuario usuario
    ) {
        return categoriaRepository.findAll().stream()
            .filter(item ->
                nome.equals(item.getNome()) &&
                    usuario.getId().equals(item.getAutor().getId())
            )
            .findFirst()
            .orElseGet(() -> categoriaRepository.save(
                new CategoriaDeMovimentacao(nome, tipo, cor, icone, usuario)
            ));
    }

    private CartaoCredito criarCartaoCredito(ContaBancaria conta) {
        return (CartaoCredito) cartaoRepository.findAll().stream()
            .filter(item -> item instanceof CartaoCredito && "Cartão principal".equals(item.getApelido()))
            .findFirst()
            .orElseGet(() -> cartaoRepository.save(new CartaoCredito(
                "Cartão principal", conta, BigDecimal.valueOf(6000), 10, 17
            )));
    }

    private CartaoDebito criarCartaoDebito(ContaBancaria conta) {
        return (CartaoDebito) cartaoRepository.findAll().stream()
            .filter(item -> item instanceof CartaoDebito && "Cartão débito".equals(item.getApelido()))
            .findFirst()
            .orElseGet(() -> cartaoRepository.save(new CartaoDebito("Cartão débito", conta)));
    }

    private void criarCartaoVoucher(ContaBancaria conta) {
        cartaoRepository.findAll().stream()
            .filter(item -> item instanceof CartaoVoucher && "Vale alimentação".equals(item.getApelido()))
            .findFirst()
            .orElseGet(() -> cartaoRepository.save(new CartaoVoucher(
                "Vale alimentação", conta, 1, BigDecimal.valueOf(850), BigDecimal.valueOf(850)
            )));
    }

    private Fatura criarFatura(CartaoCredito cartao, YearMonth referencia) {
        return faturaRepository.findByCartaoDeCredito_IdAndDataReferencia(cartao.getId(), referencia)
            .orElseGet(() -> faturaRepository.save(new Fatura(referencia, cartao)));
    }

    private void criarOrcamento(CategoriaDeMovimentacao categoria, YearMonth referencia, BigDecimal limite) {
        if (orcamentoRepository.findByCategoriaId(categoria.getId()).stream()
            .noneMatch(item -> referencia.equals(item.getMesReferencia()))) {
            orcamentoRepository.save(new Orcamento(referencia, limite, BigDecimal.ZERO, categoria));
        }
    }

    private void criarMetaFinanceira(Usuario usuario) throws ReflectiveOperationException {
        if (metaFinanceiraRepository.findAll().stream().anyMatch(item -> "Viagem de férias".equals(item.getObjetivo()))) {
            return;
        }

        MetaFinanceira meta = new MetaFinanceira(
            "Viagem de férias",
            BigDecimal.valueOf(12000),
            BigDecimal.valueOf(4250),
            YearMonth.now().plusMonths(10)
        );
        Field criador = MetaFinanceira.class.getDeclaredField("criador");
        criador.setAccessible(true);
        criador.set(meta, usuario);
        metaFinanceiraRepository.save(meta);
    }

    private RegistroDeMovimentacao criarRegistro(
        String descricao,
        BigDecimal valor,
        CategoriaDeMovimentacao categoria,
        ContaBancaria conta,
        MetodoPagamentoEnum metodo,
        Boolean recorrente,
        Integer parcelas,
        Cartao cartao
    ) {
        return registroRepository.findAll().stream()
            .filter(item -> descricao.equals(item.getDescricao()))
            .findFirst()
            .orElseGet(() -> registroRepository.save(new RegistroDeMovimentacao(
                valor,
                LocalDate.now(),
                descricao,
                metodo,
                recorrente,
                parcelas,
                categoria,
                conta,
                cartao
            )));
    }

    private void criarLancamento(
        RegistroDeMovimentacao registro,
        BigDecimal valor,
        LocalDate data,
        Fatura fatura
    ) {
        if (lancamentoRepository.findAll().stream()
            .noneMatch(item -> registro.equals(item.getRegistroDeMovimentacao()))) {
            lancamentoRepository.save(new Lancamento(valor, data, registro, fatura));
        }
    }
}
