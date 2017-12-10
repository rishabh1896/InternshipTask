package internship.rishabh.internshiptask;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import internship.rishabh.internshiptask.Model.Worldpopulation;

/**
 * Created by rishabh on 09-12-2017.
 */

public class MainAdapter extends RecyclerView.Adapter {
        private Context context;
        PopupWindow popupWindow;
        LinearLayout linearLayout;
        private List<Worldpopulation> data;
    public MainAdapter(Context context) {
            this.context=context;

        }
    public void setData(List<Worldpopulation> data)
    {
        this.data=data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflator=LayoutInflater.from(parent.getContext());

                View view=inflator.inflate(R.layout.countries_card,parent,false);
                viewHolder =new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                MyViewHolder vh=(MyViewHolder) holder;
                configureViewHolder2(vh,position);
    }


    private void configureViewHolder2(final MyViewHolder vh2, final int position) {
        try {
            Picasso.with(context)
                    .load(data.get(position).getFlag())
                    .into(vh2.thumbnails);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        vh2.title.setText(data.get(position).getCountry());
        vh2.population.setText(data.get(position).getPopulation());
        // vh2.description.setText(data.get(position).getSnippet().getDescription());
        vh2.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,FullScreen.class);
                intent.putExtra("path",data.get(position).getFlag());
                (context).startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnails;
        TextView title,population;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnails= (ImageView) itemView.findViewById(R.id.thumbnails);
            title= (TextView) itemView.findViewById(R.id.title);
            cardView= (CardView) itemView.findViewById(R.id.card);
            population= (TextView) itemView.findViewById(R.id.population);
        }
    }
}
