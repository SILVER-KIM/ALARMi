package com.example.silver.alarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.cardemulation.HostNfcFService;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class GameListAdapter extends BaseAdapter {
    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private LayoutInflater inflater;
    private ArrayList<GameListItem> gameList = new ArrayList<GameListItem>();
    private int layout;
    private int mSelectedPosition;
    private Context context;
    private int check;

    //GameListAdapter의 생성자
    public GameListAdapter(Context context, int layout, ArrayList<GameListItem> gameList) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.gameList = gameList;
        this.layout = layout;

    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return gameList.size();
    }
    //지정한 위치에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return gameList.get(position);
    }
    //저장한 위치에 있는 데이터와 관계된 아이템의 id를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }
    //position에 위치한 데이터를 화면에 출력하는데 사용될 view를 리턴
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        context = parent.getContext();
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.game_dialog_list_row, parent, false);
        }

        final RadioButton rdGameChecked = (RadioButton) convertView.findViewById(R.id.rdGameChecked);
        TextView tvGameName = (TextView) convertView.findViewById(R.id.tvGameName);
        ImageView imgGameInfo = (ImageView) convertView.findViewById(R.id.imgGameInfo);
        ImageView goGame = (ImageView) convertView.findViewById(R.id.goGame);
        //position에 위치한 데이터 참조 획득
        final GameListItem gameListItem = gameList.get(position);

        rdGameChecked.setChecked(gameListItem.getGameChecked());
        tvGameName.setText(gameListItem.getGameName());
        imgGameInfo.setImageDrawable(gameListItem.getGameInfo());

        //각각 설명 이미지 클릭했을 때 설명 화면으로 넘어가기
        imgGameInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((gameListItem.getGameName()).equals("핸드폰 흔들기")) {
                    Intent goSrules = new Intent(context, RulesActivity.class);
                    goSrules.putExtra("what", "shake");
                    context.startActivity(goSrules);
                }
                if ((gameListItem.getGameName()).equals("두더지 게임")) {
                    Intent goMrules = new Intent(context, RulesActivity.class);
                    goMrules.putExtra("what", "mole");
                    context.startActivity(goMrules);
                }
                if ((gameListItem.getGameName()).equals("과녁 맞추기")) {
                    Intent goArules = new Intent(context, RulesActivity.class);
                    goArules.putExtra("what", "arrow");
                    context.startActivity(goArules);
                }
            }
        });

        //각각 실행 이미지 클릭했을 때 실행 화면으로 넘어가기
        goGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((gameListItem.getGameName()).equals("핸드폰 흔들기")) {
                    check = 1;
                    CheckTypesTask task = new CheckTypesTask();
                    task.execute();
                }
                if ((gameListItem.getGameName()).equals("두더지 게임")) {
                    check = 2;
                    CheckTypesTask task = new CheckTypesTask();
                    task.execute();
                }
                if ((gameListItem.getGameName()).equals("과녁 맞추기")) {
                    check = 3;
                    CheckTypesTask task = new CheckTypesTask();
                    task.execute();
                }
            }
        });

        //라디오 버튼 기능 만들기
        if(position == mSelectedPosition){
            rdGameChecked.setChecked(true);
            gameListItem.setGameChecked(true);
        }
        else{
            rdGameChecked.setChecked(false);
            gameListItem.setGameChecked(false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedPosition = position;
                GameListAdapter.this.notifyDataSetChanged();
                //Toast.makeText(context.getApplicationContext(), gameListItem.getGameName(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    //아이템에 데이터를 추가해 주기위한 함수
    public void addItem(Boolean gameChecked, String gameName, Drawable gameInfo){
        GameListItem item = new GameListItem();

        item.setGameChecked(gameChecked);
        item.setGameName(gameName);
        item.setGameInfo(gameInfo);

        gameList.add(item);
    }

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {
        final Progress ramiProgress = new Progress(context);

        @Override
        protected void onPreExecute() {
            // show dialog
            ramiProgress.show();
            ramiProgress.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try{
                if(check == 1) {
                    Thread.sleep(3000);
                    Intent intent = new Intent(context, ShakeGame.class);
                    context.startActivity(intent);
                }
                if(check == 2) {
                    Thread.sleep(3000);
                    Intent intent = new Intent(context, MolegameStart.class);
                    context.startActivity(intent);
                }
                if(check == 3) {
                    Thread.sleep(3000);
                    Intent intent = new Intent(context, FpsStartActivity.class);
                    context.startActivity(intent);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ramiProgress.dismiss();
            super.onPostExecute(result);
        }
    }
}
