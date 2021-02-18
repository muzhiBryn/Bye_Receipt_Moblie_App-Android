package edu.dartmouth.cs.donewithreceipt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;




public class HistoryFragment extends Fragment {


    private static final String TAG = "HistoryFragment";

    RecyclerView mRecyclerView;
    private HistoryItemAdapter mAdapter;

    public List<HistoryEntry> getAllEntriesData(){
        return allEntriesData;
    }

    private List<HistoryEntry> allEntriesData;
    private HashSet<String> allDataCloudKeys; //用来存放本地的有的cloundkey



    private DatabaseReference mFirebaseDatabase;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allEntriesData = new ArrayList<>();
        allDataCloudKeys = new HashSet<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
    }



    private void registerFirebaseListener(DatabaseReference FirebaseDatabase){
        mFirebaseDatabase.child("history_entries").addChildEventListener(new ChildEventListener() {
            @Override
            //一旦远端添加，则本地也添加
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!allDataCloudKeys.contains(dataSnapshot.getKey())){
                    HistoryEntry entry = new HistoryEntry(dataSnapshot);
                    allEntriesData.add(entry);
                    allDataCloudKeys.add(entry.getCloudKey());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            //一旦远端发生变化，修改本地
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                HistoryEntry entry = new HistoryEntry(dataSnapshot);
                if (allDataCloudKeys.contains(dataSnapshot.getKey())){
                    for (HistoryEntry entry2 : allEntriesData) {
                        if (entry2.getCloudKey().equals(entry.getCloudKey())) {
                            entry2.setHistoryEntry(entry);
                            break;
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                HistoryEntry entry = new HistoryEntry(dataSnapshot);
                if (allDataCloudKeys.contains(dataSnapshot.getKey())) {
                    for (HistoryEntry entry2: allEntriesData) {
                        if (entry2.getCloudKey().equals(entry.getCloudKey())) {
                            allEntriesData.remove(entry2);
                            break;
                        }
                    }
                    // 通知adpter 刷新界面
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }


    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.fra_history_rcl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//类似于listView
        mAdapter = new HistoryItemAdapter();
        mRecyclerView.setAdapter(mAdapter);
        //添加行间横线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        registerFirebaseListener(mFirebaseDatabase);
    }



    private class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.ViewHolder> {
        private RecyclerView.ViewHolder holder;
        private int position;
        // Adapter 需要提供以下方法
        //getItemCount() 获取总的条目数
        //onCreateViewHolder() 创建ViewHolder(用于一条记录的展现),定义一条记录的布局
        //onBindViewHolder() 将数据绑定至ViewHolder， 实现一条记录在布局中的显示

        // Usually involves inflating a layout from XML and returning the holder
        // 我们创建的ViewHolder必须继承RecyclerView.ViewHolder，
        // 这个RecyclerView.ViewHolder构造时必须传入一个View，
        // 这个View相当于我们ListView getView中的convertView
        // （即：inflate的item布局需要传入）
        @NonNull
        @Override
        public HistoryItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return (new HistoryItemAdapter.ViewHolder(getLayoutInflater().inflate(
                    R.layout.row_history_entry, parent,false)));
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(@NonNull HistoryItemAdapter.ViewHolder holder, int position) {
            // Set item views based on your views and data model
            final HistoryEntry entry = allEntriesData.get(position);
            holder.mEntryTitle.setText(entry.getStoreName());
            holder.mEntryDateTime.setText(entry.getDateStr());
            holder.mEntryContent.setText("Total Consumption: $" + entry.getSubtotal());
            //因为setText需要用string对齐，双引号就可以把double类型转换为string对齐

        }

        @Override
        public int getItemCount() {
            return (allEntriesData.size());
        }


        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView mEntryTitle;
            TextView mEntryDateTime;
            TextView mEntryContent;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View row) {
                super(row);
                mEntryTitle = row.findViewById(R.id.entry_title);
                mEntryDateTime = row.findViewById(R.id.entry_datetime);
                mEntryContent = row.findViewById(R.id.entry_content);
            }
        }
    }
}
