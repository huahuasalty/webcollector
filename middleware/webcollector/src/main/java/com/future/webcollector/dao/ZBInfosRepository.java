package com.future.webcollector.dao;

import com.future.webcollector.model.ZbInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZBInfosRepository extends JpaRepository<ZbInfos,Integer> {

}
