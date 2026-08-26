package com.sist.web.service;
// 레시피 상세보기 속 댓글
import java.util.List;

import com.sist.web.vo.CommentVO;

public interface CommentService {

	public List<CommentVO> commentListData(int start, int fno);
	public int commentRowCount(int fno);
	public void commentInsert(CommentVO vo);
	public void commentDelete(int no);
	public void commentUpdate(CommentVO vo);
}
