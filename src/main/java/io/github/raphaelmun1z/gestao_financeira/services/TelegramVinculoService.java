package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.TelegramVinculoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HexFormat;

@Service
public class TelegramVinculoService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UsuarioRepository repository;

    public TelegramVinculoService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TelegramVinculoResDTO obterCodigo() {
        Usuario usuario = usuarioAutenticado();
        if (usuario.getTelegramLinkCode() == null) {
            usuario.setTelegramLinkCode(novoCodigo());
        }
        return new TelegramVinculoResDTO(usuario.getTelegramLinkCode());
    }

    public void inicializarCodigo(Usuario usuario) {
        if (usuario.getTelegramLinkCode() == null) {
            usuario.setTelegramLinkCode(novoCodigo());
        }
    }

    @Transactional
    public TelegramVinculoResDTO alterarCodigo() {
        Usuario usuario = usuarioAutenticado();
        usuario.setTelegramLinkCode(novoCodigo());
        return new TelegramVinculoResDTO(usuario.getTelegramLinkCode());
    }

    private Usuario usuarioAutenticado() {
        Usuario usuarioAutenticado = UsuarioService.obterUsuarioAutenticado();
        return repository.findById(usuarioAutenticado.getId())
            .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado."));
    }

    private String novoCodigo() {
        String codigo;
        do {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            codigo = HexFormat.of().formatHex(bytes);
        } while (repository.existsByTelegramLinkCode(codigo));
        return codigo;
    }

    @Transactional
    public Usuario vincular(Long chatId, String codigo) {
        Usuario usuario = repository.findByTelegramLinkCode(codigo)
            .orElseThrow(() -> new NotFoundException("Código de vínculo do Telegram inválido."));
        usuario.setTelegramChatId(chatId);
        return usuario;
    }
}
