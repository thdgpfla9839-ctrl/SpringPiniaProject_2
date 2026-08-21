package com.sist.web.mapper;
import com.sist.web.vo.*;
import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
@Mapper
@Repository
public interface CommentMapper {

	/*
			 *  <select id="commentListData" resultMap="com.sist.web.vo.CommentVO" parameterType="int">
				  SELECT no,fno,id,name,TO_CHAR(regdate,'yyyy-MM-dd hh24:mi:ss') as dbday
				  FROM piniaComment
				  ORDER BY no DESC
				  OFFSET #{start} ROWS FETCH NEXT 5 ROWS ONLY
				 </select>
				 */
	// 목록 출력
	public List<CommentVO> commentListData(@Param("start")int start,@Param("fno") int fno);
	
	/*
				 <select id="commentRowCount" resultType="int">
				  SELECT COUNT(*) FROm piniaComment
				 </select>
				*/
	
	public int commentRowCount(int fno);
	/*
	 * 
	 * 
				 <insert id="commentInsert" parameterType="com.sist.web.vo.CommentVO">
				  INSERT INTO piniaComment VALUES(pc_no_seq.nextval,#{fno},#{id},#{name},#{msg},SYSDATE)
				 </insert>
	 */
	public void commentInsert(CommentVO vo);

	
	// 댓글 삭제
	/*
	 *  <delete id="commentDelete" parameterType="int">
		  DELETE FROM piniaComment
		  WHERE no=#{no}
		 </delete>
	 */
	public void commentDelete(int no);
	
	// 댓글 수정
	/*
	 *   <update id="commentUpdate" parameterType="com.sist.web.vo.CommentVO">
		  UPDATE piniaComment SET
		  msg=#{msg}
		  WHERE no=#{no}
		 </update>
	 */
	
	public void commentUpdate(CommentVO vo);
}
