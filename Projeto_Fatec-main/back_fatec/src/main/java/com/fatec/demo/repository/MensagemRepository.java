package com.fatec.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Mensagem;

public interface MensagemRepository extends JpaRepository<Mensagem,Long> {
	List<Mensagem> findByParticipanteIdOrderByTimestampAsc(Long participanteId);

	List<Mensagem> findByParticipanteIdAndRemetenteIdNotAndLidaFalse(Long participanteId, Long remetenteId);

	Mensagem findTopByParticipanteIdOrderByTimestampDesc(Long participanteId);

	long countByParticipanteIdAndRemetenteIdNotAndLidaFalse(Long participanteId, Long remetenteId);

	@Modifying
	@Transactional
	void deleteByRemetenteId(Long remetenteId);

	@Modifying
	@Transactional
	void deleteByParticipanteIdIn(List<Long> participanteIds);

}
