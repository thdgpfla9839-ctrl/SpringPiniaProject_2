const { defineStore } = Pinia
const { nextTick } = Vue

const useChatStore = defineStore('chat', {

    state: () => ({

        stomp: null,

        users: [],

        messages: [],

        publicMessages: [],

        privateMessages: {},

        currentRoom: 'public',

        loginUser: '',

        chatBodyEl: null,

        msg: ''
    }),

    actions: {

        makeRoomId(user1, user2) {

            return [
                user1,
                user2
            ]
            .sort()
            .join('_')
        },

        getOtherUser(roomId) {

            if (roomId === 'public') {
                return ''
            }

            const users =
                roomId.split('_')

            return users[0] === this.loginUser
                ? users[1]
                : users[0]
        },

        changeRoom(user) {

            if (user === 'public') {

                this.currentRoom = 'public'

                this.messages =
                    this.publicMessages
            }

            else {

                const roomId =
                    this.makeRoomId(
                        this.loginUser,
                        user
                    )

                this.currentRoom =
                    roomId

                if (!this.privateMessages[roomId]) {
                    this.privateMessages[roomId] = []
                }

                this.messages =
                    this.privateMessages[roomId]
            }

            this.scrollToBottom()
        },

        connect() {

            const socket =
                new SockJS('/chat-ws')

            this.stomp =
                Stomp.over(socket)

            this.stomp.debug = null

            this.stomp.connect(
                {},

                () => {

                    console.log(
                        'WebSocket 연결 성공'
                    )

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
					
					this.stomp.send(
						'/app/chat/join',{},
						JSON.stringify({})
					)

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

                    this.stomp.subscribe(
                        '/user/queue/force-disconnect',

                        () => {

                            alert(
                                '중복 로그인으로 로그아웃되었습니다.'
                            )

                            location.href =
                                '/logout'
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
                JSON.stringify({
                    message: message
                })
            )
        },

        sendPrivate(to, message) {

            this.stomp.send(
                '/app/chat/private',
                {},
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