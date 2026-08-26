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
		replyNo:null
		
	}),
	actions:{
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
			setCommentData(res)
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
				setCommentData(res)
			
		}
	}
})