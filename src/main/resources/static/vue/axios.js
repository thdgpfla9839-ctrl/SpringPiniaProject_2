// 제일 먼저 실행되는 파일 => 서버와 통신할 도구 준비를 해줌
// const api => 만든 도구를 api라는 이름의 변수에 저장
// axios.create => 서버한테 요청 보낼 때 쓸 도구를 만든 거임
// axios는 서버한테 요청 보내는 수단 중 하나의 방법 => fetch, ajax도 있음
const api=axios.create({ 
	 // 나중에는 baseURL을 줘야하는데 여기에는 반드시 'http://AWS 주소' 붙여눠줘야함 AWS주소는 반드시 붙여준다
	 timeout:50000 // 50초 안에 서버가 응답을 안 하면 요청 실패처리하라는 옵션
})