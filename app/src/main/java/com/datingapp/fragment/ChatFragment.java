package com.datingapp.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.MyCameraActivity;
import com.datingapp.DatingApp;
import com.datingapp.HomeActivity;
import com.datingapp.Model.ChatMessageDTO;
import com.datingapp.R;
import com.datingapp.VidePlayActivity;
import com.datingapp.adapter.ChatAdapter;
import com.datingapp.adapter.FullScreenImageAdapter;
import com.datingapp.adapter.VeiwPagerAdapter1;
import com.datingapp.api.APIClient;
import com.datingapp.api.APIInterface;
import com.datingapp.util.CommonUtils;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;
import com.datingapp.util.OnItemClickListener;
import com.datingapp.util.OutLook;
import com.datingapp.util.PermissionUtil;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class ChatFragment extends BaseFragment implements View.OnClickListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private Context context;
    private ManageSession appSession;
    private RecyclerView rvChat;
    private EditText etChat;
    private ImageView ivSend;
    private ChatAdapter chatAdapter;
    //  private ArrayList<ChatMessageDTO> chatListDTOList = new ArrayList<>();
    private Boolean isDetail = false;
    private String messageText = "", imagePathConvert = "", imageProfilePath = "";
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 121;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public String shop_id;
    public TextView typing, tvTittle;
    private RoundedImageView ivProfile;
    private String shop_name;
    private Socket socket;
    private RelativeLayout image_layout;
    private ImageView image, cross, ivAttatch, iv_record, ivBack;
    private boolean detailStatus = false;
    private AdView mAdView; // 6
    private ProgressDialog progressDialog;
    private List<String> imageList = new ArrayList<>();
    private File file;
    private MediaRecorder recorder;
    private Chronometer chronoMeter;
    private MediaPlayer mediaPlayer;
    private int last_position = -1;
    private Handler handler;
    private Runnable runnable;
    private OutLook outLook;
    public boolean isFileUploading = false;
    private ProgressBar progressBar;
    private String msg_id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mediaPlayer.release();
        mediaPlayer = null;
        disConnect();
    }

    public void disConnect() {
        try {
            if (socket != null) {
                socket.disconnect();
                socket.off(Socket.EVENT_CONNECT, onConnect);
                socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                socket.off("start_chat_typing_response", startChatTypingResponse);
                socket.off("end_chat_typing_response", endChatTypingResponse);
                socket.off("getChatListResponse", getChatListResponse);
                socket.off("online_response", onlineResponse);
                socket.off("createMessageResponce", createMessageResponce);
                socket.off("message_seen", messageSeen);
                socket.off("offline_response", onOfflineListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat, container, false);
    }

    public void hideShowProgress(final boolean isVisible) {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isVisible) {
                            progressBar.setVisibility(View.VISIBLE);
                        } else
                            progressBar.setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        progressBar = view.findViewById(R.id.progress_bar);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d("error", i + "");
                Log.d("error", i1 + "");
                return false;
            }
        });
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && mediaPlayer != null) {
                    updateSeekBar(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(runnable, 50);
                }
            }
        };
        appSession = new ManageSession(context);
        shop_id = getArguments().getString("shop_id");
        shop_name = getArguments().getString("shop_name");
        imageProfilePath = getArguments().getString("profile_image");
        isDetail = getArguments().getBoolean("detail", false);
        if (getArguments().containsKey("details")) {
            detailStatus = true;
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ((HomeActivity) context).getSupportActionBar().hide();
        initView(view);

        new OutLook().USER_VALUE = 0;
        new OutLook().SENDER_VALUE = 0;

        chatAdapter = new ChatAdapter(getActivity(), onImageMe, onImageFrom, listener, new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(i);
                        updateSeekBar(mediaPlayer.getCurrentPosition());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rvChat.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);
        outLook = new OutLook();
        showBottomAds();
    }


    public void play(int pos) {
        try {
            if (pos != last_position) {
                stop(last_position); // this will stop the previous audio.
                // if last position = is same
                mediaPlayer.reset();
                mediaPlayer.setDataSource(CommonUtils.folder_path + chatAdapter.chatMessageDTOS.get(pos).getOrig_name());
                mediaPlayer.prepare();
                mediaPlayer.start();
                chatAdapter.chatMessageDTOS.get(pos).setState(Constant.play);
                chatAdapter.notifyItemChanged(pos);
                handler.postDelayed(runnable, 0);
            } else {
                if (mediaPlayer.isPlaying())
                    pause(last_position);
                else {
                    chatAdapter.chatMessageDTOS.get(pos).setState(Constant.play);
                    chatAdapter.notifyItemChanged(pos);
                    mediaPlayer.start();
                    handler.postDelayed(runnable, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void pause(int pos) {
        try {
            handler.removeCallbacks(runnable);
            chatAdapter.chatMessageDTOS.get(pos).setState(Constant.pause);
            chatAdapter.notifyItemChanged(pos);
            mediaPlayer.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stop(int pos) {
        try {

            handler.removeCallbacks(runnable);
            if (pos > -1) {
                chatAdapter.chatMessageDTOS.get(pos).setSeekPercentage(0);
                chatAdapter.chatMessageDTOS.get(pos).setState(Constant.stop);
                chatAdapter.notifyItemChanged(pos);
            }
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showBottomAds() {
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
//                Toast.makeText(context, "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(context, "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
//                Toast.makeText(context, "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    private void onListener() {
        try {
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.on("start_chat_typing_response", startChatTypingResponse);
            socket.on("end_chat_typing_response", endChatTypingResponse);
            socket.on("getChatListResponse", getChatListResponse);
            socket.on("online_response", onlineResponse);
            socket.on("createMessageResponce", createMessageResponce);
            socket.on("message_seen", messageSeen);
            socket.on("offline_response", onOfflineListener);
            //getChatList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getChatList() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
            jsonObject.put("to_user_id", shop_id);

            Log.v("History List", "=======> " + jsonObject);
            socket.emit("getChatList", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            DatingApp app = (DatingApp) getActivity().getApplication();
            initSocket();
            if (!socket.connected()) {
                socket.on(Socket.EVENT_CONNECT, onConnect);
                socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                socket.connect();
                //online();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void initSocket() {
        try {
            if (socket == null)
                socket = ((DatingApp) getActivity().getApplication()).getSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // if (!isDetail) {
            connect();
            // } else {
            //   initSocket();
            //  socket.on(Socket.EVENT_CONNECT, onConnect);
            //   socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            //  }♀
            onListener();
            // }
            ((HomeActivity) context).hideAds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (!isFileUploading)
                disConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((HomeActivity) context).bottomVisibility(false);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_live).setVisible(false);
        ((HomeActivity) context).bottomVisibility(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View v) {
        rvChat = (RecyclerView) v.findViewById(R.id.rv_chat);
        ivSend = (ImageView) v.findViewById(R.id.iv_send);
        etChat = (EditText) v.findViewById(R.id.et_chat);
        typing = (TextView) v.findViewById(R.id.typing);
        image_layout = (RelativeLayout) v.findViewById(R.id.image_layout);
        image = (ImageView) v.findViewById(R.id.image);
        cross = (ImageView) v.findViewById(R.id.cross);
        ivAttatch = (ImageView) v.findViewById(R.id.iv_attatch);
        iv_record = (ImageView) v.findViewById(R.id.iv_record);
        chronoMeter = (Chronometer) v.findViewById(R.id.chronoMeter);
        chronoMeter.setFormat("Recording..... %s");
        chronoMeter.stop();
        chronoMeter.setBase(0);

        tvTittle = (TextView) v.findViewById(R.id.toolbar_title);
        ivProfile = (RoundedImageView) v.findViewById(R.id.iv_profile);
        ivBack = (ImageView) v.findViewById(R.id.iv_back);

        // iv_record.setOnLo
        iv_record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getActivity() != null && OutLook.isNetworkAvailable(getActivity())) {
                    if (isValidate()) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                    try {
                                        startRecord(CommonUtils.createVideoFile(".mp3"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                    stopRecording();
                                } else {
                                    askRecordAudioPermission();
                                }
                                etChat.requestFocus();
                                break;
                        }
                        return v.onTouchEvent(event) ? v.onTouchEvent(event) : true;
                    } else {
                        dialogForName("Please wait for reply to start conversation");
                        return false;
                    }
                } else {
                    CommonUtils.toast(getString(R.string.label_no_connectivity), getActivity(), Toast.LENGTH_LONG);
                    return false;
                }
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("map", shop_id);
                Log.i(getClass().getName(), "Map id is >>>>>>>" + shop_id);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        tvTittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("map", shop_id);
                Log.i(getClass().getName(), "Map id is >>>>>>>" + shop_id);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mAdView = (AdView) v.findViewById(R.id.adView);

        ivBack.setOnClickListener(this);
        ivSend.setOnClickListener(this);
        cross.setOnClickListener(this);
        ivAttatch.setOnClickListener(this);

        etChat.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etChat.setRawInputType(InputType.TYPE_CLASS_TEXT);
        etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && charSequence.length() > 0) {

                    try {
                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
                        jsonObject.put("to_user_id", shop_id);
                        jsonObject.put("from_id", appSession.getProfileModel().getUser_id());

                        socket.emit("start_chat_typing", jsonObject);
                        Log.v("Start Chat ", " " + jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("from_id", appSession.getProfileModel().getUser_id());
//                        jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
                        jsonObject.put("to_user_id", shop_id);
                        socket.emit("end_chat_typing", jsonObject);
                        Log.v("End Chat ", " " + jsonObject.toString());

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (imageProfilePath != null) {
            if (detailStatus) {
                Picasso.with(context).load(imageProfilePath).resize(75, 75).placeholder(R.drawable.user).error(R.drawable.user).into(ivProfile);
            } else {
                Picasso.with(context).load(Constant.IMAGE_SERVER_PATH + imageProfilePath).resize(75, 75).placeholder(R.drawable.user).error(R.drawable.user).into(ivProfile);
            }
        }

        tvTittle.setText(shop_name);
    }

    //< ------------------------------------------------------ Record Audio functionality -------------------------------------------------------------------------------->
    private void startRecord(File file) {
        try {
            this.file = file;
            chronoMeter.setVisibility(View.VISIBLE);
            //   ln_send_message.visibility=View.GONE
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(file.getAbsolutePath());
            recorder.prepare();
            recorder.start();  // Recording is now started
            chronoMeter.setBase(SystemClock.elapsedRealtime());
            chronoMeter.stop();
            chronoMeter.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.reset(); // You can reuse the object by going back to setAudioSource() step
            recorder.release(); // Now the object cannot be reused
            chronoMeter.stop();
            chronoMeter.setVisibility(View.GONE);
            sendAudioFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void insertMessage(final String type, final boolean isUploading, final String origName) {
        try {
            ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
            chatMessageDTO.setSender_id(appSession.getProfileModel().getUser_id());
            chatMessageDTO.setType(type);
            chatMessageDTO.setUploading(isUploading);
            chatMessageDTO.setOrig_name(origName);
            chatMessageDTO.setMessage(null);
            chatMessageDTO.setMsg_id(Constant.msg_id_prefix + System.currentTimeMillis());
            chatMessageDTO.setCreated_at(formatDate(Calendar.getInstance().getTime()));
            chatMessageDTO.setStatus("send");
            chatAdapter.chatMessageDTOS.add(chatMessageDTO);
            msg_id = chatMessageDTO.getMsg_id();
            Log.e("size", chatAdapter.chatMessageDTOS.size() + "size");
            chatAdapter.notifyDataSetChanged();
            rvChat.smoothScrollToPosition(chatAdapter.chatMessageDTOS.size() - 1);
            Log.e("size", chatAdapter.chatMessageDTOS.size() + "size");
            if (type.equals(Constant.image) || type.equals(Constant.GIF) || type.equals(Constant.video)) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("img", origName);
                jsonObject1.put("parent", (chatAdapter.chatMessageDTOS.size() - 1));
                imageList.add(jsonObject1 + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAudioFile() {
        try {

            //Debugger.debugE("time",""+)
            if (CommonUtils.getDuration(file.getAbsolutePath().toString()).
                    equalsIgnoreCase("00:00")) {
            } else {
                insertMessage(Constant.audio, true, file.getName());
                sendFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askRecordAudioPermission() {
        int hasMediaPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
        if (hasMediaPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQUEST_RECORD_AUDIO);
        }
    }
    //< ------------------------------------------------------ Record Audio functionality -------------------------------------------------------------------------------->

    private OnItemClickListener.OnItemClickCallback onImageMe = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            try {
                showFullImage(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private OnItemClickListener.OnItemClickCallback onImageFrom = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            try {
                showFullImage(position);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    // this is for send a text message.
    public void send() {
        try {
            if (isValidate()) {
                messageText = etChat.getText().toString().trim();
                image_layout.setVisibility(View.GONE);
                if (TextUtils.isEmpty(messageText) && TextUtils.isEmpty(imagePathConvert)) {
                    return;
                }

                Log.e(getClass().getName(), "Values is >>>>" + new OutLook().USER_VALUE + "  Sender value is >>>>" + new OutLook().SENDER_VALUE);
                //   if (new OutLook().USER_VALUE >= 2) {
                //  if (new OutLook().SENDER_VALUE >= 2) {
                sendMessage();
            } else {
                dialogForName("Please wait for reply to start conversation");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAttachement() {
        try {
            if (isValidate()) {
                if (hasPermissions(context, PERMISSIONS)) {
                    isFileUploading = true;// it means file uploading started.
                    MyCameraActivity.type = "";
                    Intent intent1 = new Intent(context, MyCameraActivity.class);
                    startActivityForResult(intent1, 123);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                dialogForName("Please wait for reply to start conversation");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_send:
                if (getActivity() != null && OutLook.isNetworkAvailable(getActivity())) {
                    send();
                } else {
                    CommonUtils.toast(getString(R.string.label_no_connectivity), getActivity(), Toast.LENGTH_LONG);
                }
                break;
            case R.id.cross:
                imagePathConvert = "";
                image_layout.setVisibility(View.GONE);
                break;
            case R.id.iv_attatch:
                if (getActivity() != null && OutLook.isNetworkAvailable(getActivity())) {
                    openAttachement();
                } else {
                    CommonUtils.toast(getString(R.string.label_no_connectivity), getActivity(), Toast.LENGTH_LONG);
                }
                break;

            case R.id.iv_back:
                ((HomeActivity) context).onBackPressed();
                break;
        }

    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    MyCameraActivity.type = "";
                    Intent intent3 = new Intent(context, MyCameraActivity.class);
                    startActivityForResult(intent3, 123);
                } else {
                    Toast.makeText(context, "Veegle requires this permission to launch...", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case Constant.REQUEST_RECORD_AUDIO: {
                try {
                    if (PermissionUtil.verifyPermissions(grantResults)) {
                        //  startRecord(CommonUtils.createFile(String.valueOf(System.currentTimeMillis()) + "_AUDIO", ".mp3"));
                    } else {
                        CommonUtils.toast(getString(R.string.msg_permission_required), getActivity(), Toast.LENGTH_LONG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public void connectSocket() {
        try {
            try {
//                DatingApp app = (DatingApp) getActivity().getApplication();
//                socket = app.getSocket();
                Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
                jsonObject.put("to_user_id", shop_id);

                Log.v("History List", "=======> " + jsonObject);
                socket.emit("getChatList", jsonObject);
                hideShowProgress(true); // make progress bar visible;
                try {
                    JSONObject jobject = new JSONObject();
                    jobject.put("id", appSession.getProfileModel().getUser_id());
                    Log.v("online", "==> " + jobject);
                    socket.emit("online", jobject);
                } catch (Exception e) {
                    Log.v("Socket Exception ==> ", " " + e);
                }
            } catch (Exception e) {
                Log.v("Socket Exception ==> ", " " + e);
            }

        /*    socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.v("User Socket Excetion", "TimeOut");
//                    socket.connect();

                    Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
                        jsonObject.put("to_user_id", shop_id);

                        Log.v("History List", "=======> " + jsonObject);
                        socket.emit("getChatList", jsonObject);

                        try {
                            JSONObject jobject = new JSONObject();
                            jobject.put("id", appSession.getProfileModel().getUser_id());
                            Log.v("online", "==> " + jobject);
                            socket.emit("online", jobject);
                        } catch (Exception e) {
                            Log.v("Socket Exception ==> ", " " + e);
                        }
                    } catch (Exception e) {
                        Log.v("Socket Exception ==> ", " " + e);
                    }
                }
            });

            socket.on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.v("User Socket Excetion", "ReConnect Error");
//                    socket.connect();

                    Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
                        jsonObject.put("to_user_id", shop_id);

                        Log.v("History List", "=======> " + jsonObject);
                        socket.emit("getChatList", jsonObject);

                        try {
                            JSONObject jobject = new JSONObject();
                            jobject.put("id", appSession.getProfileModel().getUser_id());
                            Log.v("online", "==> " + jobject);
                            socket.emit("online", jobject);
                        } catch (Exception e) {
                            Log.v("Socket Exception ==> ", " " + e);
                        }
                    } catch (Exception e) {
                        Log.v("Socket Exception ==> ", " " + e);
                    }
                }
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.v("User Socket Excetion", "Connect Error");
//                    socket.connect();

                    Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + socket.id());

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
                        jsonObject.put("to_user_id", shop_id);

                        Log.v("History List", "=======> " + jsonObject);
                        socket.emit("getChatList", jsonObject);

                        try {
                            JSONObject jobject = new JSONObject();
                            jobject.put("id", appSession.getProfileModel().getUser_id());
                            Log.v("online", "==> " + jobject);
                            socket.emit("online", jobject);
                        } catch (Exception e) {
                            Log.v("Socket Exception ==> ", " " + e);
                        }
                    } catch (Exception e) {
                        Log.v("Socket Exception ==> ", " " + e);
                    }
                }
            });*/

        } catch (Exception e) {
            Log.v("User Socket Exception", " " + e);
        }
    }

    public String formatDate(Date date) {
        String dat = "";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(OutLook.input);
            dat = sdf.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dat;
    }


    public String formateCal1(String inputDate) {
        String date = "";
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {
            Date newDate = format.parse(inputDate);

            format = new SimpleDateFormat("MMM dd, hh:mm a");
            date = format.format(newDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    public void sendMessage() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
            jsonObject.put("to_user_id", shop_id);
            jsonObject.put("msg_type", "TEXT");
            jsonObject.put("file_name", "");
            jsonObject.put("orig_name", "");
            jsonObject.put("msg_id", Constant.msg_id_prefix + System.currentTimeMillis());
            byte[] data = messageText.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            // String base64 = StringEscapeUtils.escapeJava(messageText);
            jsonObject.put("message", base64);
            jsonObject.put("image", "");
            Log.v("Create Message Full", " >>>>>>>>>>>>>>" + jsonObject);
            socket.emit("createMessage", jsonObject);
            etChat.setText("");

            ChatMessageDTO mdto = new ChatMessageDTO();
            mdto.setSender_id(appSession.getProfileModel().getUser_id());
            mdto.setMessage(base64);
            mdto.setFile("");
            mdto.setMsg_id(jsonObject.optString("msg_id"));
            Date currentTime = Calendar.getInstance().getTime();
            Log.i(getClass().getName(), "My Date is >>>>>>>>>>>" + currentTime);
            mdto.setCreated_at(formatDate(currentTime));
            mdto.setStatus("send");
            chatAdapter.chatMessageDTOS.add(mdto);

            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
//                    mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                    mLinearLayoutManager.setStackFromEnd(true);
//                    rvChat.setLayoutManager(mLinearLayoutManager);
//                    if (chatAdapter == null) {
//                        chatAdapter = new ChatAdapter(context, chatListDTOList, onImageMe, onImageFrom, listener);
//                        rvChat.setAdapter(chatAdapter);
//                    } else {
                    updateAdapter();
                    //}
                }
            });

        } catch (Exception e) {
            Log.v("EXception ", " " + e);
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int position = Integer.parseInt(view.getTag().toString());
            //   String contentDescription = (String) view.getContentDescription();
            switch (view.getContentDescription().toString()) {
                case Constant.download:
                    if (new OutLook().isNetworkConnected(getActivity())) {
                        chatAdapter.chatMessageDTOS.get(position).setSrcFile(null);
                        chatAdapter.chatMessageDTOS.get(position).setUploading(true);
                        chatAdapter.notifyItemChanged(position);
                        DownloadFile downloadFile = new DownloadFile(Constant.CHAT_FILES_PATH + chatAdapter.chatMessageDTOS.get(position).getOrig_name(),
                                position,
                                chatAdapter.chatMessageDTOS.get(position).getOrig_name());
                        downloadFile.execute();
                    } else {
                        CommonUtils.toast(getString(R.string.label_no_connectivity), getActivity(), Toast.LENGTH_LONG);
                    }
                    break;
                case Constant.play:
                    playMedia((Integer) view.getTag());
                    break;
                case Constant.playGif:
                    if (chatAdapter.chatMessageDTOS.get(position).getState().equals(Constant.pauseGIF)) {
                        chatAdapter.chatMessageDTOS.get(position).setState(Constant.playGif);
                        chatAdapter.notifyItemChanged(position);
                    }
                    break;
                case Constant.upload:
                    if (new OutLook().isNetworkConnected(getActivity())) {
                        chatAdapter.chatMessageDTOS.get(position).setSrcFile(null);
                        chatAdapter.chatMessageDTOS.get(position).setUploading(true);
                        chatAdapter.notifyItemChanged(position);
                        sendFile(CommonUtils.folder_path + chatAdapter.chatMessageDTOS.get(position).getOrig_name());
                    } else {
                        CommonUtils.toast(getString(R.string.label_no_connectivity), getActivity(), Toast.LENGTH_LONG);
                    }
                    break;
            }
        }
    };

    public void playMedia(int position) {

        switch (chatAdapter.chatMessageDTOS.get(position).getType()) {

            case Constant.audio:
                //  if (chatAdapter.chatMessageDTOS.get(position).getState().equalsIgnoreCase(Constant.play)) {
                play(position);
                //   }
                break;

            case Constant.video:
                openVideoPlayActivity(CommonUtils.IsFileExist(chatAdapter.chatMessageDTOS.get(position).getOrig_name()).getAbsolutePath());
                break;
        }
        last_position = position;

    }


    public void openVideoPlayActivity(String url) {
        try {
            Intent intent = new Intent(getActivity(), VidePlayActivity.class);
            intent.putExtra("videourl", url);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String convertImageIntoBase64(String imagePath) {
        String encodedImage = "";
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }


    public void updateItem(String fileName) {
        try {
            for (ChatMessageDTO ms : chatAdapter.chatMessageDTOS) {
                if (ms.getOrig_name().equalsIgnoreCase(fileName)) {
                    ms.setUploading(false);
                    ms.setState(Constant.success);
                    break;
                }
            }
            chatAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMedia(String msg_type, String fileName, String orig_name) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
            jsonObject.put("to_user_id", shop_id);
            jsonObject.put("message", "");
            jsonObject.put("msg_type", msg_type);
            jsonObject.put("file_name", fileName);
            jsonObject.put("orig_name", orig_name);
            jsonObject.put("msg_id", msg_id);
            jsonObject.put("image", "");
            Log.v("Create Message Full", " >>>>>>>>>>>>>>" + jsonObject);
            socket.emit("createMessage", jsonObject);

            updateItem(orig_name);// this is going to update item based on file name.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFileMessage(String msg_type, String fileName, String origName) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
            jsonObject.put("to_user_id", shop_id);
            jsonObject.put("message", "");
            jsonObject.put("msg_type", msg_type);
            jsonObject.put("file_name", fileName);
            jsonObject.put("image", "");
            jsonObject.put(Constant.origName, origName);

            Log.v("Create Message Full", " >>>>>>>>>>>>>>" + jsonObject);
            socket.emit("createMessage", jsonObject);
            etChat.setText("");

            if (getActivity() == null) return;

            ChatMessageDTO mdto = new ChatMessageDTO();
            mdto.setSender_id(appSession.getProfileModel().getUser_id());
            mdto.setMessage(null);
            mdto.setType(msg_type);
            mdto.setFile(fileName);
            mdto.setOrig_name(fileName);

            Date currentTime = Calendar.getInstance().getTime();
            Log.i(getClass().getName(), "My Date is >>>>>>>>>>>" + currentTime);
            mdto.setCreated_at(formatDate(currentTime));
            mdto.setStatus("send");
            //    chatAdapter.chatMessageDTOS.add(mdto);

            JSONObject jsonObject1 = new JSONObject();
            if (mdto.getFile() != null && !mdto.getFile().equals("")) {
                jsonObject1.put("img", mdto.getFile());
                jsonObject1.put("parent", (chatAdapter.chatMessageDTOS.size() - 1));
                imageList.add(jsonObject1 + "");
            }

            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateAdapter();
                }
            });

        } catch (Exception e) {
            Log.v("Exception ", " " + e);
        }
    }

    private void uploadImage(String imagePath) {


        MultipartBody.Part profileImage = null;

        try {
            Log.d("imagePath", "image->" + imagePath);
            File profileImageFile = new File(imagePath);
            RequestBody propertyImage = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
            profileImage = MultipartBody.Part.createFormData("atch", profileImageFile.getName(), propertyImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody keepOrig = RequestBody.create(MediaType.parse("keep_orig"), "1");

        RequestBody lang = RequestBody.create(MediaType.parse("lang"), "eng");

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> call = apiInterface.uploadImage(profileImage, lang, keepOrig);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    if (response.isSuccessful()) {
                        String str = null;
                        str = response.body().string();
                        Log.d("response", "res->" + str);
                        JSONObject jsonObject = new JSONObject(str);

                        String response1 = jsonObject.optString("error");
                        if (!response1.isEmpty()) {
                            com.datingapp.util.CommonUtils.toast(jsonObject.optString("error"), getActivity(), Toast.LENGTH_SHORT);
                        } else {
                            sendFileMessage(jsonObject.optString(Constant.msg_type), jsonObject.optString(Constant.file_name), jsonObject.optString(Constant.origName));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LogView", "" + call.toString());
                t.printStackTrace();
            }
        });
    }

    public void receiveGIF(Intent data) {
        try {
            imagePathConvert = data.getStringExtra(Constant.GIF);
            final File dest = CommonUtils.createVideoFile(imagePathConvert.substring(imagePathConvert.lastIndexOf(".")));
            copyFiles(new File(imagePathConvert), dest);
            insertMessage(Constant.GIF, true, dest.getName());
            sendFile(imagePathConvert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveImage(String path) {
        try {

            appSession.setImagePath("");
            imagePathConvert = path;
            File file = new File(imagePathConvert);
            insertMessage(Constant.image, true, file.getName());
            sendFile(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAdapter() {
        try {
            chatAdapter.notifyDataSetChanged();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!chatAdapter.chatMessageDTOS.isEmpty())
                        rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void receiveVideo(Intent data, String src) {
        try {
            if (data.hasExtra(Constant.video)) {
                final String path = data.getStringExtra(Constant.video);

                if (src == null) {
                    insertMessage(Constant.video, true, new File(path).getName());
                    sendFile(path);
                } else {
                    insertMessage(Constant.video, true, new File(src).getName());
                    sendFile(src);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String path) {
        try {
            MultipartBody.Part profileImage = null;

            final File profileImageFile = new File(path);
            RequestBody propertyImage = RequestBody.create(MediaType.parse(CommonUtils.getMimeType(path)), profileImageFile);
            profileImage = MultipartBody.Part.createFormData("atch", profileImageFile.getName(), propertyImage);

            RequestBody lang = RequestBody.create(MediaType.parse("lang"), "eng");
            RequestBody keepOrig = RequestBody.create(MediaType.parse("keep_orig"), "1");
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<ResponseBody> call = apiInterface.uploadImage(profileImage, lang, keepOrig);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String str = response.body().string();
                        JSONObject jsonObject = new JSONObject(str);

                        Log.e("LogView", "" + str.toString());

                        String response1 = jsonObject.optString("error");

                        if (!response1.isEmpty()) {

                            com.datingapp.util.CommonUtils.toast(jsonObject.optString("error"), getActivity(), Toast.LENGTH_SHORT);
                            errorHandler(profileImageFile.getName());
                        } else {

                            sendMedia(jsonObject.optString(Constant.msg_type), jsonObject.optString(Constant.file_name), jsonObject.optString(Constant.origName));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    errorHandler(profileImageFile.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void errorHandler(String fileName) {
        try {
            for (ChatMessageDTO chatMessageDTO : chatAdapter.chatMessageDTOS) {
                if (chatMessageDTO.getOrig_name().equals(fileName)) {
                    chatMessageDTO.setUploading(false);
                    chatMessageDTO.setState(Constant.error);
                    break;
                }
            }
            chatAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendVideo(String path, final int pos) {
        try {
            Log.e("position", pos + "");
            MultipartBody.Part profileImage = null;

            Log.e("filePath", path);
            final File profileImageFile = new File(path);
            Log.e("isFileExist", profileImageFile.exists() + ":exist");
            RequestBody propertyImage = RequestBody.create(MediaType.parse(CommonUtils.getMimeType(path)), profileImageFile);
            profileImage = MultipartBody.Part.createFormData("atch", profileImageFile.getName(), propertyImage);


            RequestBody lang = RequestBody.create(MediaType.parse("lang"), "eng");
            RequestBody keepOrig = RequestBody.create(MediaType.parse("keep_orig"), "1");

            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<ResponseBody> call = apiInterface.uploadImage(profileImage, lang, keepOrig);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        String str = response.body().string();

                        JSONObject jsonObject = new JSONObject(str);

                        Log.e("LogView", "" + str.toString());

                        String response1 = jsonObject.optString("error");

                        if (!response1.isEmpty()) {

                            com.datingapp.util.CommonUtils.toast(jsonObject.optString("error"), getActivity(), Toast.LENGTH_SHORT);
                            errorHandler(profileImageFile.getName());
                        } else {

                            sendMedia(jsonObject.optString(Constant.msg_type), jsonObject.optString(Constant.file_name), jsonObject.optString(Constant.origName));

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    errorHandler(profileImageFile.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isValidate() {
        try {
            if (chatAdapter.chatMessageDTOS.isEmpty()) {
                return true;
            } else if (chatAdapter.chatMessageDTOS.get(chatAdapter.chatMessageDTOS.size() - 1).getSender_id().equals(appSession.getProfileModel().getUser_id())
                    && chatAdapter.chatMessageDTOS.get(chatAdapter.chatMessageDTOS.size() - 2).getSender_id().equals(appSession.getProfileModel().getUser_id())
            ) {
                return true;
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            isFileUploading = false;

            if (resultCode == 2 && data.hasExtra("image")) {

                String path = data.getStringExtra("image");

                final File dest = CommonUtils.createVideoFile(path.substring(path.lastIndexOf(".")));

                if (path.endsWith(".GIF") || path.endsWith(".gif")) {

                    receiveGIF(data);

                } else {

                    copyFiles(new File(path), dest);

                    receiveImage(dest.getAbsolutePath());
                }
                return;

            } else if (resultCode == 2 && data.hasExtra(Constant.video)) {

                String path = data.getStringExtra(Constant.video);

                final File dest = CommonUtils.createVideoFile(path.substring(path.lastIndexOf(".")));

                copyFiles(new File(data.getStringExtra(Constant.video)), dest);
                //   Files.copy(Path)
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        receiveVideo(data, dest.getAbsolutePath());
                    }
                }, 500);

                return;

            } else if (resultCode == Constant.RequestTakeVideo) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        receiveVideo(data, null);
                    }
                }, 500);

                return;

            } else if (resultCode == 2 && data.hasExtra(Constant.GIF)) {

                receiveGIF(data);

                return;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFiles(File src, File dest) {
        try {
            InputStream in = new FileInputStream(src);
            try {
                OutputStream out = new FileOutputStream(dest);
                try {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dialogForName(String text) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notification);
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        window.setType(WindowManager.LayoutParams.FIRST_SUB_WINDOW);
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView btnYes = (TextView) dialog.findViewById(R.id.tv_yes);
//        final Button btnNo = (Button) dialog.findViewById(R.id.btn_no);


        TextView tvText = (TextView) dialog.findViewById(R.id.tv_message);
        tvText.setText(text);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }


    public void showFullImage(int position) {
        final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        nagDialog.setCancelable(true);

        nagDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialog_background)));

        nagDialog.setContentView(R.layout.full_view_pager);

        FullScreenImageAdapter adapter1;

        VeiwPagerAdapter1 viewPager = (VeiwPagerAdapter1) nagDialog.findViewById(R.id.pager);

        ImageView imageView = (ImageView) nagDialog.findViewById(R.id.img_close);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nagDialog.dismiss();
            }
        });

        OnItemClickListener.OnItemClickCallback onItemClickCross = new OnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                nagDialog.dismiss();
            }
        };
        adapter1 = new FullScreenImageAdapter(context, imageList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = CommonUtils.IsFileExist(view.getTag().toString());
                if (file.exists())
                    openVideoPlayActivity(file.getAbsolutePath());
                else
                    openVideoPlayActivity(Constant.CHAT_FILES_PATH + view.getTag().toString());
            }
        }, onItemClickCross);

        viewPager.setAdapter(adapter1);

        for (int i = 0; i < imageList.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject(imageList.get(i) + "");
                if (jsonObject.optString("img").equals(chatAdapter.chatMessageDTOS.get(position).getOrig_name())) {
                    viewPager.setCurrentItem(i);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        isFileUploading = true;
        nagDialog.show();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "SocketConnected:" + "true");
                    connectSocket();
//                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Disconnected:" + "true");
//                    isConnected = false;
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "diconnected", Toast.LENGTH_LONG).show();

//                    try {
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("user_id", appSession.getProfileModel().getUser_id());
//                        jsonObject.put("to_user_id", shop_id);
//
//                        Log.v("History List", "=======> " + jsonObject);
//                        socket.emit("getChatList", jsonObject);
//
//                        try {
//                            JSONObject jobject = new JSONObject();
//                            jobject.put("id", appSession.getProfileModel().getUser_id());
//                            Log.v("online", "==> " + jobject);
//                            socket.emit("online", jobject);
//                        } catch (Exception e) {
//                            Log.v("Socket Exception ==> ", " " + e);
//                        }
//                    } catch (Exception e) {
//                        Log.v("Socket Exception ==> ", " " + e);
//                    }
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            "Error connecting", Toast.LENGTH_LONG).show();

                    //connectSocket();
                }
            });
        }
    };


    private Emitter.Listener onOnlineListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener onOfflineListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("offline", args[0].toString());
        }
    };


    private Emitter.Listener startChatTypingResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e(getClass().getName(), "Start Chat Response >>>>>" + args[0].toString());
                        if (getActivity() == null) return;
                        JSONObject data = new JSONObject(args[0].toString());
                        if (data.optString("from_id").equals(shop_id))
                            typing.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener endChatTypingResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e(getClass().getName(), "End Chat Response >>>>>" + args[0].toString());
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = new JSONObject(args[0].toString());
                        if (data.optString("from_id").equals(shop_id))
                            typing.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener getChatListResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("ChatHistory", " " + args[0]);
                    try {
                        JSONObject object = new JSONObject(args[0].toString());
                        Gson gson = new Gson();
                        Log.i(ChatFragment.class.getName(), "data->" + object.toString());
                        ArrayList<ChatMessageDTO> chatListDTOList = gson.fromJson(object.optJSONArray("data").toString(), new TypeToken<ArrayList<ChatMessageDTO>>() {
                        }.getType());

                        imageList = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject();

                        for (int i = 0; i < chatListDTOList.size(); i++) {
                            //imageList.add("http://192.168.11.82/telenyze/"+chatListDTOList.get(i).getFile());
                            if (chatListDTOList.get(i).getType().equals(Constant.image) || chatListDTOList.get(i).getType().equals(Constant.GIF) || chatListDTOList.get(i).getType().equals(Constant.video)) {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("img", chatListDTOList.get(i).getOrig_name());
                                jsonObject1.put("parent", (chatAdapter.chatMessageDTOS.size() - 1));
                                imageList.add(jsonObject1 + "");
                            }
                        }

                        if (getActivity() == null) return;

                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
                        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        mLinearLayoutManager.setStackFromEnd(true);

                        chatAdapter.chatMessageDTOS.clear();
                        chatAdapter.chatMessageDTOS.addAll(chatListDTOList);
                        updateAdapter();
                        // chatAdapter.notifyDataSetChanged();
                        hideShowProgress(false); // make progress bar gone;

                    } catch (Exception e) {
                        Log.v("Exception ", "" + e);
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private class DownloadFile extends AsyncTask<String, String, File> {

        private String URL, fileName;
        private int position;
        private ProgressBar progressBar;

        public DownloadFile(String URL, int position, String fileName) {
            this.URL = URL;
            this.position = position;
            this.fileName = fileName;
        }

        @Override
        protected File doInBackground(String... strings) {
            try {
                java.net.URL url = new URL(URL);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                int res_code = ((HttpURLConnection) urlConnection).getResponseCode();
                File file = CommonUtils.IsFileExist(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (res_code == 200) {
                    if (urlConnection.getContentLength() != -1) {
                        file.setReadable(true, false);
                        InputStream inputStream = url.openStream();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        byte[] data = new byte[1024];
                        long total = 0;
                        int len = inputStream.read(data);
                        while (len != -1) {
                            fileOutputStream.write(data, 0, len);
                            len = inputStream.read(data);
                        }
                        inputStream.close();
                        fileOutputStream.close();
                    }
                    return file;
                }
                // }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            chatAdapter.chatMessageDTOS.get(position).setUploading(true);
            chatAdapter.chatMessageDTOS.get(position).setSrcFile(null);
        }

        @Override
        protected void onPostExecute(File s) {
            super.onPostExecute(s);
            try {
                if (s != null && getActivity() != null) {
                    chatAdapter.chatMessageDTOS.get(position).setSrcFile(s);
                    chatAdapter.chatMessageDTOS.get(position).setUploading(false);// gone loader.
                    chatAdapter.chatMessageDTOS.get(position).setState(Constant.success);
                    chatAdapter.notifyItemChanged(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    private Emitter.Listener onlineResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    };

    private Emitter.Listener createMessageResponce = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("Chat History Response", " >>>>>>>>>>" + args[0]);
                    try {
                        final JSONObject object = new JSONObject(args[0].toString());
                        Gson gson = new Gson();
                        ChatMessageDTO mdto = new ChatMessageDTO();

                        mdto = gson.fromJson(object.optJSONObject("data").toString(), ChatMessageDTO.class);
                        mdto.setId(object.optString("chat_id"));

                        Log.e("Chat Id Resp", " >>>>>>>>>>" + object.optString("chat_id"));
                        Log.e("DTO id Response", " >>>>>>>>>>" + mdto.getId());

                        if (mdto.getSender_id().equals(shop_id)) {

                            Log.i(getClass().getName(), "My Date is >>>>>>>>>>>");
                            mdto.setCreated_at(outLook.formateCal(mdto.getCreated_at() + ""));
                            mdto.setStatus("date");
                            chatAdapter.chatMessageDTOS.add(mdto);

                            JSONObject jsonObject = new JSONObject();

                            if (mdto.getType().equals(Constant.image) || mdto.getType().equals(Constant.GIF) || mdto.getType().equals(Constant.video)) {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("img", mdto.getOrig_name());
                                jsonObject1.put("parent", (chatAdapter.chatMessageDTOS.size() - 1));
                                imageList.add(jsonObject1 + "");
                            }

                            if (getActivity() == null) return;

                        } else if (mdto.getSender_id().equals(appSession.getProfileModel().getUser_id())) {
                            for (int i = chatAdapter.chatMessageDTOS.size()-1; i >= 0; i--) {
                                if (object.optString("msg_id").equals(chatAdapter.chatMessageDTOS.get(i).getMsg_id())) {
                                    chatAdapter.chatMessageDTOS.get(i).setId(mdto.getId());
                                    chatAdapter.chatMessageDTOS.get(i).setMessageseen("0");
                                    chatAdapter.chatMessageDTOS.get(i).setStatus("date");
                                    chatAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
//                            chatAdapter.chatMessageDTOS.get(chatAdapter.chatMessageDTOS.size() - 1).setId(mdto.getId());
//                            chatAdapter.chatMessageDTOS.get(chatAdapter.chatMessageDTOS.size() - 1).setMessageseen("0");
//                            chatAdapter.chatMessageDTOS.get(chatAdapter.chatMessageDTOS.size() - 1).setStatus("date");
//                            chatAdapter.notifyItemChanged(chatAdapter.chatMessageDTOS.size() - 1);

                        }

                        updateAdapter();
                    } catch (Exception e) {
                        Log.v("Exception ", "" + e);
                    }
                }
            });
        }
    };

    private Emitter.Listener messageSeen = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("message_seen Response", " >>>>>>>>>>" + args[0]);
                    try {
                        if (chatAdapter.chatMessageDTOS.size() > 0) {
                            if (getActivity() == null) return;

                            JSONObject object = new JSONObject(args[0].toString());

                            if (object.has("seen") && object.optJSONArray("seen") != null) {

                                Gson gson = new Gson();

                                ArrayList<ChatMessageDTO> chatMessageDTOArrayList = gson.fromJson(object.optJSONArray("seen").toString(), new TypeToken<ArrayList<ChatMessageDTO>>() {
                                }.getType());

                                Log.i(getClass().getName(), "Chat list >>>>>" + chatMessageDTOArrayList.size());

                                if (chatMessageDTOArrayList != null && chatMessageDTOArrayList.size() > 0) {

                                    for (int j = 0; j < chatMessageDTOArrayList.size(); j++) {
                                        Log.i(getClass().getName(), "Chat increase loop >>>>>" + chatMessageDTOArrayList.size());
                                        for (int i = (chatAdapter.chatMessageDTOS.size() - 1); i >= 0; i--) {
                                            Log.i(getClass().getName(), "double right check loop" + chatMessageDTOArrayList.size());

                                            Log.i(getClass().getName(), "Both id is >>>>>" + chatMessageDTOArrayList.get(j).getId() + "     " + chatAdapter.chatMessageDTOS.get(i).getId());
                                            try {
                                                if (chatAdapter.chatMessageDTOS.get(i).getId().equals(chatMessageDTOArrayList.get(j).getId())) {
                                                    Log.i(getClass().getName(), "main condition check >>>>>" + chatMessageDTOArrayList.size());
                                                    chatAdapter.chatMessageDTOS.get(i).setMessageseen("1");
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    updateAdapter();
                                }
                            } else {
                                updateToSeen();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("EXception ", "" + e);
                    }
                }
            });
        }
    };

    //here we are updating all the records which has been not seen.
    private void updateToSeen() {
        try {
            for (int i = 0; i < chatAdapter.chatMessageDTOS.size(); i++) {
                if (chatAdapter.chatMessageDTOS.get(i).getSender_id().equals(appSession.getProfileModel().getUser_id())) {
                    if (chatAdapter.chatMessageDTOS.get(i).getMessageseen().equals("0")) {
                        chatAdapter.chatMessageDTOS.get(i).setMessageseen("1");
                        chatAdapter.notifyItemChanged(i);
                    }
                }
            }
            rvChat.smoothScrollToPosition(chatAdapter.chatMessageDTOS.size() - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
    }


    public void updateSeekBar(int i) {
        try {
            if (last_position >= 0) {
                chatAdapter.chatMessageDTOS.get(last_position).setUploading(false);
                chatAdapter.chatMessageDTOS.get(last_position).setSeekPercentage(i);
                chatAdapter.chatMessageDTOS.get(last_position).setDuration(mediaPlayer.getDuration());
                chatAdapter.notifyItemChanged(last_position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stop(last_position);
        //updateSeekBar(0);// this means stop playing.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
