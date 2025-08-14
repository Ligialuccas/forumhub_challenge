package br.com.forumhub.domain.topico;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroTopico(

        String titulo,
        String mensagem,
        String curso) {

}
