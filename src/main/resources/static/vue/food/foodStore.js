// 이 파일은 데이터 저장소 파일이야 => "store" 여기에 데이터가 저장돼
// Pinia라는 도구 안에서 store를 만들 때 쓰는 함수만 defineStore 이 안에서 뽑아옴 
const {defineStore} = Pinia
// store는 저장공간 => 처리함수, 데이터가 자동으로 html에 저장이 되게끔
// store는 전역공간 => 모든 html에서 사용이 가능함
// initalState 초기값, 처음상태를 만드는 함수를 정의
const initalState=()=>({
    // 해당 변수들 전부 초기화 => 리셋이 아니라 초기박을 정해준
	list:[], 
	curpage:1, // 지금 몇페이지 보고 있는지 => 현재 페이지 번호
	totalpage:0, 
	startPage:0, 
	endPage:0
})

// 새로운 store 생성
const useFoodStore=defineStore('food_store',{
   state:initalState,
   // store 안에 저장된 state를 가지고 새로운 값을 계산해서 반환하는 역할
   // 총액 계산이나 페이지 번호 => state 안에 있는 데이터를 가지고 만든다
   getters:{
	// range라는 이름의 계산함수 정의 => 매개변수는 state => state는 store 전체를 받음
	  range:(state)=>{
		const arr=[] // 페이지 번호를 담을 빈 배열 그릇하나를 만듦
		for(let i=state.startPage;i<=state.endPage;i++)
			{
				arr.push(i) // 조건이 참이면 i값을 arr 배열 맨뒤에 추가 => push ( )
			}
			return arr // 완성된 배열을 결과로 돌려줌
	  } // range는 페이지 번호 버튼을 몇개 그려야 하는지 배열로 만들어주는 계산기
	    // 나중에 home.html에서 v-for="(i,index) in store.range"로 페이지 버튼을 화면에 뿌리는데 사용
   },
   // 사용자가 요청한 기능
   // actions는 서버와 연동할 때 사용
   // 이 부분은 methods:{dataRecv()}가 이렇게 바뀌어서 작성
   actions:{
	async foodListData(){
		const res=await api.get('/food/list_vue',{
			params:{
				page: this.curpage // 요청보내는 값
			}
		})
		// Map에 있는 데이터값 받기
		console.log(res.data)
		
		// 받은 값 저장
		this.list=res.data.list 
		this.curpage=res.data.curpage 
		this.totalpage=res.data.totalpage 
		this.startPage=res.data.startPage 
		this.endPage=res.data.endPage 
	},
	move(page){
		this.curpage=page
		this.foodListData()
	}
   }
	
})