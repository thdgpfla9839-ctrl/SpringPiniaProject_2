package com.sist.web.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sist.web.entity.BootBoard;
import com.sist.web.repository.BootBoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl {

	// 이번에는 service클래스를 따로 만들지 않고 임플로 대체해봄
	private final BootBoardRepository bDao;
	
	// 전체 데이터 가져오기
	public Page<BootBoard> findAll(Pageable pg)
	{
		return bDao.findAll(pg);
	}
	
	// 게시판 총페이지
	public int boardTotalPage()
	{
		return (int)(Math.ceil(bDao.count()/10.0));
	}
	
	public BootBoard findByNo(int no)
	{
		return bDao.findByNo(no);
	}
	
	// 게시글 쓰기
	public void save(BootBoard vo)
	{
		bDao.save(vo);
	}
	
	
}
