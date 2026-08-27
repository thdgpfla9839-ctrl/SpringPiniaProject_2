const {defineStore} = Pinia
/*
   stomp => controller를 날림 => 댓글이 달리는 순간에 알림 날려줌
   
   user => kafka => stomp => controller
*/
const useBoardStore=defineStore('board_comment',{
	// 여기에 있는 데이터가 변경이 되면 자동으로 HTML이 변경된다
	state:()=>({
		list:[],
		curpage:1,
		totalpage:0,
		board_no:0,
		sessionId:'',
		count:0,
		msg:'',
		stomp:null, // 대댓글 알림
		updateMsg:{},
		updateReplyNo:null,
		replyMsg:{},
		replyNo:null,
		stomp:null
		
	}),
	actions:{
		connect(id){
			const sock=new SockJS("/chat-ws")
			this.stomp=Stomp.over(sock)
			this.stomp.connect({},()=>{
				// /sub/notice/'+id라는 주소로 오는 메시지를 받아보고 싶다고 
				// enableSimpleBroker 해당 브로커한테 등록해둠
				// /sub/notice/ => 이 경로 뒤에 /user가 없으니 웹소캣config에서 setUserDestinationPrefix 이게 작동한 것은 아니구나도 알 수 있음
		        // /user로 받으려면 => convertAndSendToUser로 컨트롤러에서 보냈어야함 => /user가 붙으면 브로커가 특정 유저 전용이구나라고 판단 
				// 그래서 서버가 시큐리티 로그인 정보랑 현재 로그인한 사람의 정보를 대조해서 
				// 실제로 로그인되어 있는 kim의 세션(principal)을 찾아서 보내 => 그럼 브로커카 이 세션이 진짜 로그인한 kim이 맞는지 검증한 뒤 알림을 전달하는 복잡한 과정이야
				// BUT, 지금처럼 사용해주면 그냥 주소 문자열 일치로 문자열만 알면 누구나 연결이 가능한 방식을 사용함
				this.stomp.subscribe('/sub/notice/'+id,msg=>{
					this.showToast(msg.body)
					this.boardCommentListData(this.board_no)
				})
			})
		},
		disConnection(){
			if(this.stomp && this.stomp.connected)
				{
					this.stomp.disconnect(()=>{
						console.log("STOMP 종료")
					})
				}
		},
		setCommentData(res)
		{
			console.log(res.data)
			this.list=res.data.list
			this.curpage=res.data.curpage
			this.totalpage=res.data.totalpage
			this.count=res.data.count	
		},
		async boardCommentListData(board_no){
			this.board_no=board_no
			const res=await api.get('/reply/list_vue',{
				params:{		
					page:this.curpage,
					board_no:board_no
				}
				
			})
			this.setCommentData(res)
		},
		async boardCommentInsert(msgRef){
			
			if(this.msg==='')
				{
					msgRef?.focus()
					return
				}
				// post 방식에서는 params를 사용하면 안 됨 => restController 해당 부분을 보면 post인지 get인지 알 수 있음
				// api는
				const res=await api.post('/reply/insert_vue',{
					page:this.curpage,
					board_no:this.board_no,
					msg:this.msg
				})
				this.setCommentData(res)
				this.msg=''
		},
		toggleReply(no){
			this.replyNo=this.replyNo==no?null:no
			
		},
		async boardCommentReplyInsert(no){
			const res =await api.post('/reply/rereply_insert_vue',{
				no:no,
				board_no:this.board_no,
				page:this.curpage,
				msg:this.replyMsg[no]
			})
			this.setCommentData(res)
			this.replyNo=null
			this.replyMsg[no]=''
		},
		showToast(message){
			const toast=document.getElementById("replyToast");
			const toastMsg=document.getElementById("toastMsg");
			toastMsg.innerText=message
			toast.classList.add("show")
			
			setTimeout(()=>{
				hideToast()
			},5000)
		}
	}
})
function hideToast(){
	const toast=document.getElementById("replyToast");
	toast.classList.remove("show")
}