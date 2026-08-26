const {defineStore} = Pinia
/*
   stomp => controller를 날림 => 댓글이 달리는 순간에 알림 날려줌
   
   user => kafka => stomp => controller
*/
const useBoardStore=defineStore('board_comment',{
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
		
	}
})