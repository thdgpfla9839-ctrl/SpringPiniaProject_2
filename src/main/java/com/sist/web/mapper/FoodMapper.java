package com.sist.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.sist.web.vo.*;
@Mapper // 데이터 베이스 연동
@Repository
public interface FoodMapper {

	/*
	 *   <select id="foodListData" resultType="com.sist.web.FoodVO" parameterType="int">
		   SELECT no,name,poster,address
		   FROM food
		   ORDER BY no ASC
		   OFFSET #{start} ROWS FETCH NEXT 12 ROWS ONLY
		  </select>
	 */
	// 목록 출력
	public List<FoodVO> foodListData(int start);
	
	// 총페이지 = 간단하니까 이렇게 작성
	@Select("SELECT CEIL(COUNT(*)/12.0) FROM food")
	public int foodTotalPage();
	
	// 상세보기
	@Select("SELECT * FROM food WHERE no=#{no}")
	public FoodVO foodDetailData(int no);
	
	@Update("UPDATE food SET hit=hit+1 WHERE no=#{no}")
	public void foodHitIncrement(int no);
}
