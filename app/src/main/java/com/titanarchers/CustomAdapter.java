package com.titanarchers;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private List<ArrowGroupModel> groupList;
    private OnItemClickListener listener;
    public CustomAdapter(List<ArrowGroupModel> groupList, OnItemClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        //holder.tvArrowScore1.setText(String.valueOf(groupList.get(position).getArrowPoint1().score));
        //holder.tvArrowScore2.setText(String.valueOf(groupList.get(position).getArrowPoint2().score));
        //holder.tvArrowScore3.setText(String.valueOf(groupList.get(position).getArrowPoint3().score));
        holder.bind(groupList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        int modelSize = 0;
        try
        {
           modelSize = groupList.size() ;
        }
        catch (Exception e) {

            modelSize = 0;
            // warn user
            // clear mEditor
            //FIXME sometimes have left over mpref and need yo delete so using this for those occasions
            //get null pointer error in CustomAdapter - getItemCount()
            //mEditor.clear().commit();
        }
        return modelSize;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvArrowScore1, tvArrowScore2, tvArrowScore3, tvRating;
        public CardView cvGroup;

        public MyViewHolder(View itemView) {
            super(itemView);

            //itemView.setOnClickListener(this); //add click for row to enable_disable row
            tvArrowScore1 = itemView.findViewById(R.id.arrowScore1);
            tvArrowScore2 = itemView.findViewById(R.id.arrowScore2);
            tvArrowScore3 = itemView.findViewById(R.id.arrowScore3);
            tvRating = itemView.findViewById(R.id.rating);
            cvGroup = itemView.findViewById(R.id.card_view);
        }

        public void bind(final ArrowGroupModel item, final OnItemClickListener listener) {
            tvArrowScore1.setTextColor(item.getGroupTextColor());
            tvArrowScore1.setText(String.valueOf(item.getArrowPoint1().score));
            tvArrowScore2.setTextColor(item.getGroupTextColor());
            tvArrowScore2.setText(String.valueOf(item.getArrowPoint2().score));
            tvArrowScore3.setTextColor(item.getGroupTextColor());
            tvArrowScore3.setText(String.valueOf(item.getArrowPoint3().score));

            int grpRatingColor = item.getGroupColor();
            if (grpRatingColor == Color.WHITE) grpRatingColor = Color.GREEN;
            tvRating.setTextColor(grpRatingColor);
            tvRating.setText(String.valueOf(item.getGroupRating()));

            if (!item.getShowGroup()) {
                cvGroup.setBackgroundColor(Color.WHITE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int grpRatingColor = item.getGroupColor();

                    if (!item.getShowGroup()) {
                        item.setShowGroup(true);
                        if (grpRatingColor == Color.WHITE) grpRatingColor = Color.GREEN;
                        cvGroup.setBackgroundColor(grpRatingColor);
                    }
                    else {
                        item.setShowGroup(false);
                        cvGroup.setBackgroundColor(Color.WHITE);
                    }
                    listener.onItemClick(item);
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(ArrowGroupModel arrowGroup);
    }
}
