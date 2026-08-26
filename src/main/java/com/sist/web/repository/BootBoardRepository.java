package com.sist.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sist.web.entity.BootBoard;
// Integer => primary key를 int로 설정했어서
public interface BootBoardRepository extends JpaRepository<BootBoard, Integer>
{
	// 상세보기
	public BootBoard findByNo(int no);

}
