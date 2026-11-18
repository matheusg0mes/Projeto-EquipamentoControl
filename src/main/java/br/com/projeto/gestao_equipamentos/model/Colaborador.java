package br.com.projeto.gestao_equipamentos.model;

import br.com.projeto.gestao_equipamentos.security.Perfil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Entity 
@Table(name = "colaborador")
@Getter
@Setter
@NoArgsConstructor
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column (nullable = false)
    private String nome;

    @Column (nullable = false, unique = true)
    private String email;

    @Column (nullable = false, unique = true)
    private String cpf;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties("colaborador")
    @JoinColumn(name = "endereco_id", unique = true)
    private Endereco endereco;

    @OneToMany (mappedBy = "colaborador")
    @JsonIgnoreProperties("colaborador")
    @JsonIgnore
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<Emprestimo> emprestimos;
}
