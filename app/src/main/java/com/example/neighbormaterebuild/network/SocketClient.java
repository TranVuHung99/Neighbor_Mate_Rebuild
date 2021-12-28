package com.example.neighbormaterebuild.network;

import android.util.Log;

import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.model.Message;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class SocketClient {

    public static final String TAG = "Socket-Client";

    public static final String EVENT_LOGIN = "Server-login-ok";
    public static final String EVENT_CHAT_RECEIVE = "Server-chat-receive";
    public static final String EVENT_CHAT_SEND_STATUS = "Server-chat-send-ok";
    public static final String EVENT_CHAT_TYPING = "Server-chat-message-typing";
    public static final String EVENT_CHAT_IS_READ = "Server-message-is-read";

    public static final String EVENT_SEND_MESSAGE = "Client-chat-message";
    public static final String EVENT_SEND_READ_MESSAGE = "Client-chat-receive-ok";
    public static final String EVENT_OPEN_MEDIA = "Client-chat-open-ok";
    public static final String EVENT_SEND_TYPING = "Client-chat-message-typing";
    public static final String EVENT_SEND_IMAGE = "Client-chat-image";
    public static final String EVENT_SEND_LOCATION = "Client-chat-location";
    public static final String EVENT_SEND_SCREEN = "Client-change-screen";
    public static final String EVENT_SEND_CLIENT_LOG = "Client-logs";

    private static SocketClient instance;

    private SocketClient(){}


    public static SocketClient getInstance() {
        if (instance == null) {
            synchronized (SocketClient.class) {
                if (null == instance) {
                    instance = new SocketClient();
                }
            }
        }
        return instance;
    }


    private Socket socket;
    private OnSocketListener listener;

    public boolean isNull() {
        return socket == null;
    }


    public void disconnectSocket() {
        if (socket != null)
            socket.disconnect();
    }

    public void connectSocket() {
        if (socket != null)
            socket.connect();
    }

    public void create(String token) {
        try {
            IO.Options opts = new IO.Options();
            //opts.port = 3125;
            opts.query = "token=" + token + "&debug=1";
            //opts.secure = true;
            opts.reconnection = true;

            socket = IO.socket(Config.SOCKET_URL, opts);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, Socket.EVENT_CONNECT);
                }
            });

            socket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //Log.e(TAG, Socket.EVENT_RECONNECT);
                    if (listener != null)
                        listener.onReconnect(args);
                }
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, Socket.EVENT_CONNECT_ERROR);
                }
            });

            socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, Socket.EVENT_CONNECT_TIMEOUT);
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, Socket.EVENT_DISCONNECT);
                    if (listener != null)
                        listener.onDisconnect(args);
                }
            });


            socket.once(EVENT_LOGIN, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //Log.e(TAG, "Login Socket::::: " + args[0]);
                    //Log.e(TAG, "Login Socket::::: " + args[1]);
                }
            });

            socket.on(EVENT_CHAT_RECEIVE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //Log.e(TAG, "EVENT_CHAT_RECEIVE::::: " + args[0]);
                    if (listener != null)
                        listener.onChatReceive(args);
                }
            });

            socket.on(EVENT_CHAT_SEND_STATUS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //Log.e(TAG, "EVENT_CHAT_SEND_STATUS::::: " + args[0]);
                    if (listener != null)
                        listener.onChatSendStatus(args);
                }
            });

            socket.on(EVENT_CHAT_TYPING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //Log.e(TAG, "EVENT_CHAT_TYPING::::: " + args[0]);
                    if (listener != null)
                        listener.onChatTyping(args);
                }
            });

            socket.on(EVENT_CHAT_IS_READ, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //Log.e(TAG, "EVENT_CHAT_IS_READ::::: " + args[0]);
                    if (listener != null)
                        listener.onChatReadStatus(args);
                }
            });


        } catch (URISyntaxException e) {
        }
    }

    public interface OnSocketListener {
        void onReconnect(Object... args);

        void onDisconnect(Object... args);

        void onChatSendStatus(Object... args);

        void onChatReceive(Object... args);

        void onChatReadStatus(Object... args);

        void onChatTyping(Object... args);
    }

    public void addListener(OnSocketListener listener) {
        this.listener = listener;
    }

    public void sendMessage(String msg, String rID, String screen) {
        try {
            Map data = new HashMap<String, String>();
            data.put("msg", msg);
            data.put("r_id", rID);
            data.put("chat_center", screen);
            Log.e("SEND MESSAGE", "" + new JSONObject(data));
            socket.emit(EVENT_SEND_MESSAGE, new JSONObject(data));
        } catch (Exception e) {
        }

    }

    public void sendReadMessage(int userID, String time) {
        try {
            Map data = new HashMap<String, String>();
            data.put("u_id", userID + "");
            data.put("time", time);

            socket.emit(EVENT_SEND_READ_MESSAGE, new JSONObject(data));
        } catch (Exception e) {
        }
    }

    public void openImage(Message msg) {
        try {
            Map data = new HashMap<String, String>();
            data.put("msg_id", msg.getMsgID());
            data.put("r_id", msg.getrID());
            data.put("u_id", msg.getuID());
            data.put("time", msg.getTimeNotConvert());
            data.put("showed", "1");

            socket.emit(EVENT_OPEN_MEDIA, new JSONObject(data));
        } catch (Exception e) {
        }
    }

    public void sendTyping(String rID, int typingStatus) {
        JSONObject data = new JSONObject();
        try {
            data.put("r_id", rID);
            data.put("typing", typingStatus);
            socket.emit(EVENT_SEND_TYPING, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendImage(JSONObject file, String rID, String screen) {
        JSONObject data = new JSONObject();
        try {
            data.put("r_id", rID);
            data.put("msg", "");
            data.put("file", file);
            data.put("chat_center", screen);
            Log.d("SEND IMAGE", "sendImage: " + data);
            socket.emit(EVENT_SEND_IMAGE, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLocation(JSONObject location, String rID, String screen) {
        JSONObject data = new JSONObject();
        try {
            data.put("r_id", rID);
            data.put("msg", "");
            data.put("location", location);
            data.put("chat_center", screen);
            socket.emit(EVENT_SEND_LOCATION, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendClientLogs(String slug, String log) {
        JSONObject data = new JSONObject();
        try {
            data.put("slug", slug);
            data.put("log", log);
            socket.emit(EVENT_SEND_CLIENT_LOG, data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
