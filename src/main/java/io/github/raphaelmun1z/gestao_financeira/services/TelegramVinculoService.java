package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.TelegramVinculoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class TelegramVinculoService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UsuarioRepository repository;

    public TelegramVinculoService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TelegramVinculoResDTO gerarCodigo() {
        Usuario usuarioAutenticado = UsuarioService.obterUsuarioAutenticado();
        Usuario usuario = repository.findById(usuarioAutenticado.getId())
            .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado."));
        String codigo = String.valueOf(100000 + RANDOM.nextInt(900000));
        LocalDateTime expiraEm = LocalDateTime.now().plusMinutes(10);
        usuario.setTelegramLinkCode(codigo);
        usuario.setTelegramLinkCodeExpiresAt(expiraEm);
        return new TelegramVinculoResDTO(codigo, expiraEm);
    }

    @Transactional
    public Usuario vincular(Long chatId, String codigo) {
        Usuario usuario = repository.findByTelegramLinkCode(codigo)
            .filter(item -> item.getTelegramLinkCodeExpiresAt() != null)
            .filter(item -> item.getTelegramLinkCodeExpiresAt().isAfter(LocalDateTime.now()))
            .orElseThrow(() -> new NotFoundException("Código de vínculo do Telegram inválido ou expirado."));
        usuario.setTelegramChatId(chatId);
        usuario.setTelegramLinkCode(null);
        usuario.setTelegramLinkCodeExpiresAt(null);
        return usuario;
    }
}
