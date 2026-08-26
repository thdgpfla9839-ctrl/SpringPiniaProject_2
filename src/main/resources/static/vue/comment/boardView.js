const {createApp,onMounted,ref}=Vue
   const {createPinia}=Pinia
   const commentApp=createApp({
	   setup()
	   {// store를 가지고 온다
	   const store=useBoardStore();
	   const msgRef=ref(null)
	   
	   // 시작과 동시에 값 읽어 온다
	   onMounted(()=>{
		  store.sessionId=SESSION_ID
	      store.boardCommentListData(BOARDNO)
	   })
	   
	   return {
		   store,
		   msgRef
	   }
	   
	 }
   })
   commentApp.use(createPinia())
   commentApp.mount("#comment")
