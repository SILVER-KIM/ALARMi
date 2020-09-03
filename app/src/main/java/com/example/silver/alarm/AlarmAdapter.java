package com.example.silver.alarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.CustomViewHolder> {
    int version = 1;
    String truth;
    DatabaseHelper helper;
    SQLiteDatabase database;
    CustomViewHolder holder;
    private Context context;
    //아까 리스트뷰의 아이템들을 array리스트로
    private ArrayList<AlarmData>arrayList;

    public AlarmAdapter(ArrayList<AlarmData>arrayList,Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    //리스트뷰가 처음으로 생성될 때 생성주기를 뜻함
    @Override
    public AlarmAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        holder = new CustomViewHolder(view);

        return holder;

    }

    //실제 추가 될 때에 대한 생명주기
    @Override
    public void onBindViewHolder(final AlarmAdapter.CustomViewHolder holder, final int position) {

        holder.time.setText(arrayList.get(position).getTime());
        holder.date.setText(arrayList.get(position).getDate());
        holder.name.setText(arrayList.get(position).getName());
        holder.onoffBTN.setChecked(arrayList.get(position).getType());

        holder.onoffBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                helper = new DatabaseHelper(context, DatabaseHelper.tableName,null,version);
                database = helper.getWritableDatabase();  //데이터베이스 읽고 쓰겠다
                String alarmNAME = holder.name.getText().toString();
                if(b == true) {
                    ((AlarmActivity)AlarmActivity.mContext).readINFO();
                    truth = String.valueOf(b);
                    Toast.makeText(context, "알람 활성화!:)", Toast.LENGTH_SHORT).show();
                    helper.changeState(database, alarmNAME, truth);
                    arrayList.get(position).setType(b);
                }
                else if(b == false) {
                    ((AlarmActivity)AlarmActivity.mContext).readINFO();
                    truth = String.valueOf(b);
                    Toast.makeText(context, "알람 비활성화!:(", Toast.LENGTH_SHORT).show();
                    helper.changeState(database, alarmNAME, truth);
                    arrayList.get(position).setType(b);
                }
            }
        });

        //길게 클릭됐을 때 삭제하기 (데베에서도 삭제)
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //데이터베이스
                helper = new DatabaseHelper(context, DatabaseHelper.tableName,null,version);
                database = helper.getWritableDatabase();  //데이터베이스 읽고 쓰겠다
                String alarmNAME = holder.name.getText().toString();
                remove(holder.getAdapterPosition());
                helper.deleteData(database, alarmNAME);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size():0);
    }

    public void remove(int position){
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);
        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace(); //예외상황이 나왔을 때 강제실행을 해줌
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView time;
        protected TextView date;
        protected TextView name;
        Switch onoffBTN;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.onoffBTN = (Switch)itemView.findViewById(R.id.onoffBTN);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.date = (TextView) itemView.findViewById(R.id.date);
            this.name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
