const { defineStore } = Pinia
const { nextTick } = Vue
// defineStore : 새로운 store를 만들 때 사용
const useChatStore = defineStore('chat', {

    state: () => ({
        // stomp 객체생성 => 규칙있게 설정해서 서버와 통신
        stomp: null,
        // 현재 접속한 사람의 사용자 목록을 나타냄 => 로그인 요청을 하면 서버에서 전체적으로 전송
        users: [],
        // 메시지는 화면을 백하면 지워지는데 채팅 메시지를 모아서 한번에 처리
        messages: [],
        // 메시지도 두개로 나눠지는데 그중에 하나 : 전체 채팅 메시지를 저장하는 공간
        publicMessages: [],
        // 1:1 채팅 메시지를 저장하는 공간
        privateMessages: {},
        // 현재 채팅방 => 1:1
        currentRoom: 'public',
        // 현재 로그인된 사람 => ${session.username}
        loginUser: '',
        // 채팅창 변경할 때 사용 => 전체에서 1:1로 이동할 수 있게끔 => DOM
        chatBodyEl: null,
        // 사용자가 입력한 메시지가 들어가는 부분 => msg는 v-model="store.msg"
        msg: ''
    }),
	// 여기까지가 채팅에서 사용하는 공통 변수 저장 => '상태 관리 프로그램'이라고 한다

	// actions 여기가 서버와 연결해서 데이터를 변경하거나 => Model 부분 / 자체 데이터를 변경(=> ViewModel 부분) => html을 변경할 때 (=> View 그니까 mount 부분)
    actions: {

		// 1:1방 생성할때 => makeRoomId => hong_kim 이런식으로
        makeRoomId(user1, user2) {

            return [
                user1,
                user2
            ]
            .sort() // sort를 안 해주면 채팅방이 kim_hong hong_kim 이렇게 생기는데 그러면 같은 채팅방이 아니게 되잖아 둘다 kim_hong 이렇게 되게끔 하려고 sort를 시키는 것
            .join('_')
        },

        getOtherUser(roomId) {

			// 전체 채팅을 하고 있으면
            if (roomId === 'public') {
                return ''
            }

			// 방이름을 가지고 사용자 분리 => hong_kim
            const users =
                roomId.split('_') // 참고로 split는 리턴형이 배열 []이다

				// 로그인 사용자가 첫번째면 => hong_kim이면 hong이 본인
				// 두번째 사용자가 상대방이 된다 => kim이 상대방
				// sort를 해서 채팅방이 둘다 hong_kim일텐데 hong 입장에서야 본인이지만 kim입장에서 hong은 본인이 아니잖아 => 그래서 user가 누군지 확인해주려고 하는 거야
            return users[0] === this.loginUser
                ? users[1]
                : users[0]
        },

        changeRoom(user) {

            if (user === 'public') {

				// 현재 대화방이 전체 채팅이면
                this.currentRoom = 'public'
                // Topic을 이용해서 전체 메시지를 전송해라
                this.messages =
                    this.publicMessages
            }

			// 그렇지 않으면 1:1 채팅
            else {

				// 채팅방을 생성
                const roomId =
                    this.makeRoomId(
                        this.loginUser,
                        user
                    )

				// 그런다음 현재 채팅방 이름을 변경
                this.currentRoom =
                    roomId

				// 해당되는 채팅방이 없는 경우 => 한명이 채팅방을 만들고 그냥 채팅방을 나가버리면 방이 없어져
                if (!this.privateMessages[roomId]) {
                    this.privateMessages[roomId] = []
                }

				// 해당 방에만 메시지를 전송해라
                this.messages =
                    this.privateMessages[roomId]
            }

			// 채팅창 아래로 이동
            this.scrollToBottom()
        },

		// 서버와 연결되는 부분
        connect() {

			// SockJS 연결
            const socket =
                new SockJS('/chat-ws') // 이 부분이 매칭 어쩌구

			// 메시지 전송을 하려면 stomp 연결을 해줘야함
            this.stomp =
                Stomp.over(socket)

			// stomp 콘솔 로그 제거	
            this.stomp.debug = null

			// 서버와 실제 연동되는 부분
            this.stomp.connect(
                {},

                () => {

                    console.log(
                        'WebSocket 연결 성공'
                    )

					// 접속자 목록을 얻어오는 부분 => 두가지 방식이 있다
					// 1. subscribe() : 응답(서버로부터 값 읽기)
					// 2. send() : 요청(서버로 값을 전송)
                    this.stomp.subscribe(
                        '/topic/users',

                        msg => {

                            const users =
                                JSON.parse(msg.body)

                            this.users =
                                users.filter(
                                    u =>
                                        u !== this.loginUser
                                )
                        }
                    )
					// 목록을 보내달라 요청 => @MessageMapping()
					/* @MessageMapping()
					   => 클라이언트가 서버로 보내는 메시지 처리
					   
					   SimpleMessageTemplate
					   => 서버가 클라이언트에게 메시지 전송
					   
					   WebSocket
					   => 채팅 / 알림 / 실시간 상태 변경
					   => 요즘엔 챗봇이 생기면서 사라지고 있는 상황
					*/
					this.stomp.send(
						'/app/chat/join',{},
						JSON.stringify({})
					)

					// 전체 채팅 메시지를 받아서 저장
                    this.stomp.subscribe(
                        '/topic/chat',

                        msg => {

                            const m =
                                JSON.parse(msg.body)

                            this.publicMessages.push(m)

                            if (
                                this.currentRoom ===
                                'public'
                            ) {

                                this.messages =
                                    this.publicMessages

                                this.scrollToBottom()
                            }
                        }
                    )

                    this.stomp.subscribe(
                        '/user/queue/chat',

                        msg => {

                            const m =
                                JSON.parse(msg.body)

                            const roomId =
                                this.makeRoomId(
                                    m.sender,
                                    m.receiver
                                )

                            if (
                                !this.privateMessages[
                                    roomId
                                ]
                            ) {

                                this.privateMessages[
                                    roomId
                                ] = []
                            }

                            this.privateMessages[
                                roomId
                            ].push(m)

                            if (
                                this.currentRoom ===
                                roomId
                            ) {

                                this.messages =
                                    this.privateMessages[
                                        roomId
                                    ]

                                this.scrollToBottom()
                            }
                        }
                    )

					// 채팅을 종료할 때
                    this.stomp.subscribe(
                        '/user/queue/force-disconnect',

                        () => {

                            alert(
                                '중복 로그인으로 로그아웃되었습니다.'
                            )

                            location.href =
                                '/logout' // 로그아웃되면 종료
                        }
                    )
                },

                error => {

                    console.error(
                        'WebSocket 연결 실패',
                        error
                    )
                }
            )
        },

		// 스크롤바를 맨 아래로 이동
        async scrollToBottom() {

            await nextTick()

            if (this.chatBodyEl) {

                this.chatBodyEl.scrollTop =
                    this.chatBodyEl.scrollHeight
            }
        },

        sendPublic(message) {

            this.stomp.send(
                '/app/chat/public',
                {},
				// 여기는 문자열만 보냄
                JSON.stringify({
                    message: message
                })
            )
        },

        sendPrivate(to, message) {

            this.stomp.send(
                '/app/chat/private',
                {},
				// 근데 여기는 보낸사람까지 보냄
                JSON.stringify({
                    receiver: to,
                    message: message
                })
            )
        },

        send() {

            if (!this.msg.trim()) {
                return
            }

            if (
                this.currentRoom ===
                'public'
            ) {

                this.sendPublic(
                    this.msg
                )
            }

            else {

                const users =
                    this.currentRoom.split('_')

                const receiver =
                    users[0] === this.loginUser
                        ? users[1]
                        : users[0]

                this.sendPrivate(
                    receiver,
                    this.msg
                )
            }

            this.msg = ''
        }
    }
})