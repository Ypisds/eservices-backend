package com.Ypisds.eservices.repository.specification;


import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;
import com.Ypisds.eservices.model.Servico;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;



public class ServiceSpecification {

    public static Specification<Servico> hasTituloLike (String titulo){
       return (root, query, builder) ->
               titulo == null ? null : builder.like(builder.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%");
    }

    public static Specification<Servico> precoLessThanOrEqualTo(BigDecimal preco){
        return ((root, query, builder) ->
                preco == null ? null : builder.lessThanOrEqualTo(root.get("preco"), preco)
                );
    }

   public static Specification<Servico> hasStatusEquals(Status status){
        return (root, query, builder) ->
                status == null ? null : builder.equal(root.get("status"), status);
   }

   public static Specification<Servico> createdInThisAno(Integer ano){
        if(ano == null) return null;

        LocalDateTime dataInicioDoAno = LocalDateTime.of(ano, 1, 1, 0, 0)
                .with(TemporalAdjusters.firstDayOfYear());
        LocalDateTime dataFimDoAno = LocalDateTime.of(ano, 12, 31, 23, 59)
                .with(TemporalAdjusters.lastDayOfYear());

        return (root, query, builder) ->
            builder.between(root.get("createdDate"), dataInicioDoAno, dataFimDoAno);
   }

   public static Specification<Servico> hasThisCategoria(CategoriaServico categoria){
        if(categoria == null) return null;

        return (root, query, builder) -> builder.equal(root.get("categoria"), categoria);
   }

}
