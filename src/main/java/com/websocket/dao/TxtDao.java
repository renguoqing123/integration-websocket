package com.websocket.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.websocket.model.Txt;

@Repository
public interface TxtDao extends JpaRepository<Txt, Integer>,JpaSpecificationExecutor<Txt>{

}
