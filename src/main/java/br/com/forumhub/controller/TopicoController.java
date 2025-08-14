package br.com.forumhub.controller;


import br.com.forumhub.domain.topico.*;
import br.com.forumhub.domain.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/topicos")
//@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    private TopicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroTopico dados,
                                    @AuthenticationPrincipal Usuario usuarioLogado,
                                    UriComponentsBuilder uriBuilder){

        var topico = new Topico(dados, usuarioLogado.getNome());

        System.out.println("Dados recebidos e validados: " + topico);

        repository.save(topico);

        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        var dto = new DadosDetalhamentoTopico(topico);
        System.out.println(dto);
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemTopico>> listar(@PageableDefault(size = 10, sort = {"dataCriacao"})
                                                            Pageable paginacao, Sort asc){
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemTopico::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id){
        var topico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoTopico(topico));
    }

    @PutMapping("{id}")
    @Transactional
    public ResponseEntity atualizar(@PathVariable Long id,
                                    @RequestBody @Valid DadosAtualizaTopico dados){

        var optionalTopico = repository.findById(id);
        if(optionalTopico.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        var topico = optionalTopico.get();
        topico.atualizarinformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoTopico(topico));
    }

    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id){
        var optionalTopico = repository.findById(id);
        if(optionalTopico.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        var topico = optionalTopico.get();
        topico.excluir();
        return ResponseEntity.noContent().build();
    }

}
