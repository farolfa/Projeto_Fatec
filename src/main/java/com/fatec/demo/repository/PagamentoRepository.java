package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento,Long>{

}
