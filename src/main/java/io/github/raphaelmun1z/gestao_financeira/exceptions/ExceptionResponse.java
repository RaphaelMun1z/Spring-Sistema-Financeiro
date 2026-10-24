package io.github.raphaelmun1z.gestao_financeira.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ExceptionResponse", description = "Formato padrão das respostas de erro da API.")
public class ExceptionResponse {
    @Schema(description = "Data e hora da ocorrência do erro.", example = "2026-01-31T14:30:00")
    private String localDateTime;
    @Schema(description = "Mensagens legíveis que explicam o erro.", example = "[\"Recurso não encontrado.\"]")
    private List<String> message;
    @Schema(description = "Recurso e caminho HTTP associados ao erro.", example = "uri=/api/contas-bancarias")
    private String details;

    public ExceptionResponse(String localDateTime, List<String> message, String details) {
        this.localDateTime = localDateTime;
        this.message = message;
        this.details = details;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
