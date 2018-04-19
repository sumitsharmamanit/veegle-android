package com.datingapp.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.DatingApp;
import com.datingapp.HomeActivity;
import com.datingapp.Model.MessageDTO;
import com.datingapp.R;
import com.datingapp.adapter.MessageListAdapter;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MessageList extends BaseFragment {
    private Context context;
    private ManageSession appSession;
    private RecyclerView rvChat;
    private EditText etChat;
    private ImageView ivSend;
    private TextView tvNoRecord;
    MessageListAdapter messageListAdapter;

    ArrayList<MessageDTO> newlist = new ArrayList<>();
    ArrayList<MessageDTO> messageDTOList = new ArrayList<>();
    private Socket socket;
    public String msgCount = "";
    public int msgCountint = 0;
    boolean success;
    private boolean searchStatus = false, onlineStatus = false;
    private RelativeLayout rlMain, rlAdmin;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void disConnect() {
        try {
            if (socket != null)
                socket.off(Socket.EVENT_CONNECT, onConnect);
            socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.off("getUserListResponse", getUserListResponse);
            socket.off("offline_response", offlineResponse);
            socket.off("user_disconnected", userDisconnected);
            socket.off("user_connected", userConnected);
            socket.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disConnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((HomeActivity) context).hideAds();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            disConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        appSession = new ManageSession(context);
        initToolBar();
        initView(view);

    }

    private void online() {
        try {
            JSONObject object = new JSONObject();
            object.put("id", appSession.getProfileModel().getUser_id());
            socket.emit("online", object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            DatingApp app = (DatingApp) getActivity().getApplication();
            socket = app.getSocket();
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.on("getUserListResponse", getUserListResponse);
            socket.on("offline_response", offlineResponse);
            socket.on("createMessageResponce", createMessageResponse);
            socket.on("user_disconnected", userDisconnected);
            socket.on("user_connected", userConnected);
            socket.connect();
            online();
            if (appSession.isFirstTimeMessageShow()) {
                rlMain.setVisibility(View.GONE);
                rlAdmin.setVisibility(View.VISIBLE);
                appSession.setFirstTimeMessageShow(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener userConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("UserConnected", args[0].toString());
            String id = args[0].toString();
            for (int i = 0; i < messageDTOList.size(); i++) {
                {
                    if (messageDTOList.get(i).getUserId().equals(id)) {

                        messageDTOList.get(i).setChatonline("1");
                        messageDTOList.get(i).setIsOnline("1");
                        messageDTOList.get(i).setOnlineHalfHour("online");
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageListAdapter.notifyDataSetChanged();
                                }
                            });
                        break;

                    }
                }
            }
        }
    };

    private Emitter.Listener userDisconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("UserDisconnected", args[0].toString());
            String id = args[0].toString();
            for (int i = 0; i < messageDTOList.size(); i++) {
                {
                    if (messageDTOList.get(i).getUserId().equals(id)) {
                        messageDTOList.get(i).setChatonline("0");
                        messageDTOList.get(i).setIsOnline("0");
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageListAdapter.notifyDataSetChanged();
                                }
                            });

                        break;
                    }
                }
            }
        }
    };
    //listener for any new message.
    private Emitter.Listener createMessageResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getUserList();
        }
    };

    private void connectSocket() {

        try {

//            DatingApp app = (DatingApp) getActivity().getApplication();
//            socket = app.getSocket();
            Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());
            getUserList();


        } catch (Exception e) {
            e.printStackTrace();
            Log.v("User Socket Exception", " " + e);
        }
    }


    public void getUserList() {
        try {
            JSONObject jobject = new JSONObject();
            jobject.put("id", appSession.getProfileModel().getUser_id());
            Log.v("get User List", "==> " + jobject);
            socket.emit("getUserList", jobject);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Socket Exception ==> ", " " + e);
        }
    }


    private void initView(View v) {
        rvChat = (RecyclerView) v.findViewById(R.id.rv_chat);
        tvNoRecord = (TextView) v.findViewById(R.id.tv_norecord);

        rlMain = (RelativeLayout) v.findViewById(R.id.rl_main);
        rlAdmin = (RelativeLayout) v.findViewById(R.id.rl_admin);

        //chatAdapter = new ChatAdapter(context, chatListDTOList, onItemClick);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChat.setLayoutManager(mLinearLayoutManager);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(true);

        MenuItem searchItem = menu.findItem(R.id.action_search);
//        if (messageDTOList.size() > 0)
        searchItem.setVisible(true);
        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(((HomeActivity) context).getComponentName()));
        }


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchStatus = false;
                return false;
            }
        });

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // Do something
//                Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();

                searchStatus = true;
                newText = newText.toLowerCase();
                newlist = new ArrayList<>();

                if (messageDTOList.size() > 0) {
                    for (MessageDTO name : messageDTOList) {
                        String getName = name.getName().toLowerCase();
                        if (getName.contains(newText)) {
                            newlist.add(name);
                        }
                    }
                    messageListAdapter.filter(newlist);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
//                Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((HomeActivity) context).bottomVisibility(true);
        return super.onOptionsItemSelected(item);
    }


    private OnItemClickListener.OnItemClickCallback onItemClick = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            Log.i(getClass().getName(), "click");
            Fragment fragment = new ChatFragment();
            Bundle bundle = new Bundle();

            if (searchStatus) {
//                bundle.putString("shop_id", newlist.get(position).getToUserId());
                bundle.putString("shop_id", newlist.get(position).getUserId());
                bundle.putString("shop_name", newlist.get(position).getName());
                bundle.putString("profile_image", newlist.get(position).getFilename());
            } else {
//                bundle.putString("shop_id", messageDTOList.get(position).getToUserId());
                bundle.putString("shop_id", messageDTOList.get(position).getUserId());
                bundle.putString("shop_name", messageDTOList.get(position).getName());
                bundle.putString("profile_image", messageDTOList.get(position).getFilename());
            }
//            MessageListAdapter.msgPosition=position;


            //bundle.putString(PN_SHOP_ID, appointmentListCurrent.get(position).getEstimate_id());
            // bundle.putString("appointment_id", appointmentListCurrent.get(position).getId());
            // bundle.putString(PN_LATITUDE,currentLat);
            //bundle.putString(PN_LONGI_TUDE,currentLong);
            // bundle.putString(PN_TITLE, appointmentListCurrent.get(position).getShop_name().toString());
            fragment.setArguments(bundle);
            replaceFragmentWithBack(R.id.output, fragment, "ChatFragment", "MessageList");
        }
    };


    private void initToolBar() {
        android.support.v7.app.ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        ((HomeActivity) getActivity()).createBackButton("Messages", false);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (!isConnected) {
                    connectSocket();
//                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "diconnected");
//                    isConnected = false;
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "diconnected", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.e(TAG, "Error connecting");
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "Error connecting", Toast.LENGTH_LONG).show();
                    getUserList();
                }
            });
        }
    };


    private Emitter.Listener getUserListResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e(getClass().getName(), "User List Response Array is >>>>>" + args[0]);

                        try {
                            JSONObject object = new JSONObject(args[0].toString());
                            Gson gson = new Gson();
                            messageDTOList = gson.fromJson(object.optJSONArray("data").toString(), new TypeToken<ArrayList<MessageDTO>>() {
                            }.getType());
                            Log.v("Size ", " " + messageDTOList.size());

                            if (messageDTOList.size() > 0) {
//                                tvNoRecord.setVisibility(View.GONE);
//                                rvChat.setVisibility(View.VISIBLE);
                                if (getActivity() == null)
                                    return;

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
                                        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                        rvChat.setLayoutManager(mLinearLayoutManager);
                                        messageListAdapter = new MessageListAdapter(context, messageDTOList, onItemClick);
                                        rvChat.setAdapter(messageListAdapter);
                                        messageListAdapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvNoRecord.setVisibility(View.VISIBLE);
                                        rvChat.setVisibility(View.GONE);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            Log.v("EXception ", "" + e);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener offlineResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(context, "Offline Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
