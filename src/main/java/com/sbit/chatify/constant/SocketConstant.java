package com.sbit.chatify.constant;

public interface SocketConstant {
    long HEARTBEAT_TIMEOUT_MS = 90_000;
    long HEARTBEAT_CHECK_INTERVAL_MS = 30_000;
    String INVALID_SESSION = "invalidSession";
    String FRIEND_REQUEST = "friendRequest";
    String ACK_FRIEND_REQUEST = "ackFriendRequest";
    String SEARCHED_USERS = "searchedUsers";
    String ACK_SEARCHED_USERS = "ackSearchedUsers";
    String CREATE_CHAT_GROUP = "createChatGroup";
    String ACCEPT_FRIEND_REQUEST = "acceptFriendRequest";
    String ACK_ACCEPT_FRIEND_REQUEST = "ackAcceptFriendRequest";
    String ADD_CONTACT = "addContact";
    String REMOVE_CONTACT = "removeContact";
    String REJECT_FRIEND_REQUEST = "rejectFriendRequest";
    String CANCEL_FRIEND_REQUEST = "cancelFriendRequest";
    String SEEN_LAST_MSG = "seenLastMsg";
    String NOTIFICATION = "notification";
    String GET_CHAT = "getChat";
    String ACK_GET_CHAT = "ackGetChat";
    String TEXT_MESSAGE = "textMessage";
    String ACK_TEXT_MESSAGE = "ackTextMessage";
    String RECEIVED_TEXT_MESSAGE = "receivedTextMessage";
}
