export enum SocketRoutes {
    // accounts
    account_creation = "create-account",
    login = "login",
    logout = "logout",
    view_account = "view-account",
    modify_account = "modify-account",
    modify_avatar = "modify-avatar",

    new_user_connected = "new-user-connected",
    user_disconnected = "user-disconnected",

    // chat rooms
    send_message = "send-message",
    chat_room_history = "chat-room-history",
    create_chat_room = "create-chat-room",
    new_chat_room = "new-chat-room",
    delete_chat_room = "delete-chat-room",
    enter_chat_room = "enter-chat-room",
    leave_chat_room = "leave-chat-room",
    get_all_rooms = "get-all-rooms",

    // games creation
    create_game = "create-game",
    delete_game = "delete-game",

    // parties
    get_all_parties = "get-all-parties",
    create_party = "create-party",
    join_party = "join-party",
    leave_party = "leave-party",
    party_removed = "party-removed",
    new_party = "new-party",
    player_joined = "player-joined",
    player_left = "player-left",
    party_started = "party-started",

    // live game
    start_game = "start-game",
    update_stats = "update-stats",
    end_game = "end-game",
    end_party = "end-party",
    answer = "answer",
    stroke = "stroke",
    add_vp = "add-virtual-player",
    remove_vp = "remove-virtual-player",
    get_clue = "get-clue",
    kick = "kick",
}
