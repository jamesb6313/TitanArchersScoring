package com.titanarchers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private List<ArrowGroupModel> groupList;
    public CustomAdapter(List<ArrowGroupModel> groupList) {
        this.groupList = groupList;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tvArrowScore1.setText(String.valueOf(groupList.get(position).getArrowPoint1().score));
        holder.tvArrowScore2.setText(String.valueOf(groupList.get(position).getArrowPoint2().score));
        holder.tvArrowScore3.setText(String.valueOf(groupList.get(position).getArrowPoint3().score));

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

        public TextView tvArrowScore1, tvArrowScore2, tvArrowScore3;

        public MyViewHolder(View itemView) {
            super(itemView);

            //itemView.setOnClickListener(this); //add click for row to enable_disable row


            tvArrowScore1 = itemView.findViewById(R.id.arrowScore1);
            tvArrowScore2 = itemView.findViewById(R.id.arrowScore2);
            tvArrowScore3 = itemView.findViewById(R.id.arrowScore3);
        }

        //onClick Listener for view
        //@Override
        //public void onClick(View v) {

        //}
    }
}
