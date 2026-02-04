package com.Ypisds.eservices.model;

import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    private Set<CategoriaServico> categorias;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @ManyToOne(targetEntity = Usuario.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario anunciante;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Servico servico)) return false;
        return Objects.equals(id, servico.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
