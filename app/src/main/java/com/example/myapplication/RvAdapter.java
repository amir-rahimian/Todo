package com.example.myapplication;

import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RvAdapter extends RecyclerView.Adapter<RvHolder> {

    private List<Task> data ;
    private RvHolder.ClickListener clickListener ;

    public RvAdapter(List<Task> data , RvHolder.ClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RvHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.tast_card_layout,parent,false);
        return new RvHolder(v,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RvHolder holder, int position) {

        holder.getTitle().setText(data.get(position).getTitle());
        holder.getSubTitle().setText(data.get(position).getSubTitle());
        holder.getCheckBox().setChecked(data.get(position).isDone());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}

class RvHolder extends RecyclerView.ViewHolder {

    private TextView  title , subTitle ;
    private CheckBox checkBox ;
    private ClickListener clickListener ;
    public RvHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView,ClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener ;

        // UI
        title = itemView.findViewById(R.id.txtTitle);
        subTitle = itemView.findViewById(R.id.txtSubTitle);
        checkBox = itemView.findViewById(R.id.checkBox);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onCheckedChange(getAdapterPosition(),checkBox.isChecked());
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()){
                    title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else {
                    title.setPaintFlags(0);
                }
            }
        });
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getSubTitle() {
        return subTitle;
    }

    interface ClickListener {
        void onCheckedChange(int postion , boolean isChecked);
        void onClick();
        void onLongClick();
    }
}