package com.sist.web.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.sist.web.vo.*;
@Mapper
@Repository
public interface BoardCommentMapper {

	/*
	 *  <select id="boardCommentListData" resultType="com.sist.web.vo.BootCommentVO" parameterType="int"> // 들어가는 데이터가 2개라 int => hashmap으로 바꿈
		  SELECT no,board_no,id,name,msg,TO_CHAR(regdate,'yyyy-MM-dd hh24:mi:ss') as dbday,
		         group_tab
		  FROM bootComment
		  WHERE board_no=#{board_no}
		  ORDER BY group_id DESC, group_step ASC
		  OFFSET #{start} ROWS FETCH NEXT 10 ROWS ONLY       
       </select>
	 */
	public List<BootCommentVO> boardCommentListData(Map map);
	
	/*
	 *   <select id="boardCommentCount" resultType="int" parameterType="int">
		  SELECT COUNT(*) FROM bootComment
		  WHERE board_no=#{board_no}
		 </select>
	 */
	public int boardCommentCount(int board_no);
	
	/*
	 *  <insert id="boardCommentInsert" parameterType="com.sist.web.vo.BootCommentVO">
		  <selectKey keyProperty="no" resultType="int" order="BEFORE">
		   SELECT NVL(MAX(no)+1,1) as no
		   FROM bootComment
		  </selectKey>
		   INSERT INTO bootComment(no,board_no,id,name,msg,group_id)
		   VALUES(#{no},#{board_no},#{id},#{name},#{msg},(SELECT NVL(MAX(group_id)+1,1) FROM bootComment))
		</insert>
	 */
	
	public void boardCommentInsert(BootCommentVO vo);
	
	@Select("SELECT id,group_id,group_step,group_tab "
			+"FROM bootComment "
			+"WHERE no=#{no}")
	public BootCommentVO boardParentInfoData(int no);
	
	@Update("UPDATE bootComment SET "
			+"group_step=group_step+1 "
			+"WHERE group_id=#{group_id} AND group_step>#{group_step}")
	public void boardGroupStepIncrement(@Param("group_id") int group_id,@Param("group_step") int group_step);
	
	@SelectKey(keyProperty = "no",resultType = int.class, before = true, statement = "SELECT NVL(MAX(no)+1,1) as no FROM bootComment")
	@Insert("INSERT INTO bootComment VALUES("
	         +"#{no},#{board_no},#{id},#{name},#{msg}, "
	         +"SYSDATE,#{group_id},#{group_step},#{group_tab},#{root},0"
			 + ")")
	public void boardCommentReReply(BootCommentVO vo);
	
	@Update("UPDATE bootComment SET "
			+"depth=depth+1 "
			+"WHERE no=#{no}")
	public void boardDepthIncrement(int no);
}
