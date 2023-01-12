package com.example.demo.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.vo.Attach;

import jakarta.transaction.Transactional;

public interface AttachRepository extends JpaRepository<Attach,Integer> {
	public List<Attach> findAllByFbnum(Integer fbnum);
	@Transactional
	public Integer deleteByFbnum(Integer fbnum);
}
