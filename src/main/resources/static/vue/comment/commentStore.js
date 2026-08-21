/*
    Pinia 
	1) Store생성 순서 :  App 생성(Vue 생성) = createApp(
		              Pinia 등록 = definStore()
				      store 생성 : 1) state : HTML코드를 자동으로 갱신해주는 변수들
 				                  2) getters : 실제 계산 값 => 예를 들어 1,000  
	    				          3) actions : 실제 서버와 연결 => state를 갱신
						
	2) 피니아 동작순서 :
	     사용자 이벤트발생(버튼 클릭이나 마우스 클릭)
		 store => action함수 호출
		 서버연결 => axios / fetch
		 서버에서 요청 처리 결과값 읽기
		 store에 있는 state변수를 변경
		 HTML에 적용								  
	) 
*/
// 피니아를 이용해 새로운 스토어 생성 => defineStore
const {defineStore} = Pinia
// defineStore에 이름을 지정함
// HTML 적용하기 위한 전체 컴포넌트 사용이 가능하게 변수 설정
// state 변수는 자바에서 static => 공통 사용 변수
const useCommentStore=defineStore('comment',{
	state:()=>({
		/*
				map.put("rList", list);
				map.put("count", count);
				map.put("curpage", page);
				map.put("totalpage", totalpage);
		*/	
		rList:[],
		curpage:1,
		totalpage:0,
		count:0,
		sessionId:'',
		fno:0,
		msg:''
	}),
	getters:{
		
	},
	actions:{
		// 데이터 가져오기
		// 먼저 목록
		async commentListData(fno){
			this.fno=fno
			// 서버 연결해서 데이터 가져오기
			const res=await api.get('/comment/list_vue',{
				params:{
					page:this.curpage,
					fno:fno
				}
			})
			console.log(res.data) // res.data => map에 저장한 값
			this.rList=res.data.rList // map.put으로 보내준 데이터 전부 가져와야해
			this.curpage=res.data.curpage
			this.totalpage=res.data.totalpage
			this.count=res.data.count
		},
		async commentInsert(msgRef){
			if(this.msg=='')
				{
					msgRef?.focus() // ?의 의미
					return 
				}
				const res=await api.post('/comment/insert_vue"',{
					page:this.curpage,
					fno:this.fno,
					msg:this.msg
				})
				console.log(res.data) 
				this.rList=res.data.rList 
				this.curpage=res.data.curpage
				this.totalpage=res.data.totalpage
				this.count=res.data.count
				this.msg=''
		}
	}
})