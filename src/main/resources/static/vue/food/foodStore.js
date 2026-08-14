const {defineStore} = Pinia
// store는 저장공간 => 처리함수, 데이터가 자동으로 html에 저장이 되게끔
// store는 전역공간 => 모든 html에서 사용이 가능함
const initalState=()=>({
	list:[],
	curpage:1,
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
	  range:(state)=>{
		const arr=[]
		for(let i=state.startPage;i<=state.endPage;i++)
			{
				arr.push(i)
			}
			return arr
	  }
   },
   // 사용자가 요청한 기능
   // actions는 서버와 연동할 때 사용
   // 이 부분은 methods:{dataRecv()}가 이렇게 바뀌어서 작성
   actions:{
	async foodListData(){
		const res=await api.get('/food/list_vue',{
			params:{
				page: this.curpage
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