package Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Models.SearchResults;
import ssapps.com.datingapp.R;
import ssapps.com.datingapp.RecyclerViewClickListener;

public class SearchResultsAdapter  extends RecyclerView.Adapter<SearchResultsAdapter.MyViewHolder>{

    private List<SearchResults> results = new ArrayList<>();
    private Context context;
    private RecyclerViewClickListener mListener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_row, parent, false);

        return new MyViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        SearchResults result = results.get(position);
        Picasso.with(context).load(result.getPhotourl()).transform(new CropSquareTransformation()).placeholder(context.getResources().getDrawable(R.drawable.fb))
                                .into(holder.profile_image);

        if (result.getIsOnline().equals("Yes")){
           // holder.online_offline_image.setImageDrawable(R.drawable.ic_online_dot);
            holder.online_offline_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_online_dot));
            holder.online_offilne_text.setText(context.getResources().getString(R.string.online));
        } else if (result.getIsOnline().equals("No")){
          //  holder.online_offline_image.setImageDrawable(R.drawable.ic_offline_dot);
            holder.online_offline_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_offline_dor));
            holder.online_offilne_text.setText(context.getResources().getString(R.string.offline));
        }

        holder.details.setText(result.getUsername()+","+result.getAge_self());

    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView  online_offline_image,dummy;
        public TextView details,online_offilne_text;
        public RoundedImageView profile_image;

        public MyViewHolder(View view ,RecyclerViewClickListener listener){
            super(view);
            profile_image = (RoundedImageView)view.findViewById(R.id.circle_image);
            online_offline_image = (ImageView)view.findViewById(R.id.online_offline_image);
            details = (TextView)view.findViewById(R.id.name_age_tv);
            online_offilne_text = (TextView)view.findViewById(R.id.online_offline_tv);
            dummy = (ImageView)view.findViewById(R.id.dummy_image);

            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view,getAdapterPosition());
        }
    }

   public SearchResultsAdapter(Context context,List<SearchResults> results,RecyclerViewClickListener listener){
        this.context = context;
        this.results = results;
        this.mListener = listener;
   }

    public class CropSquareTransformation implements Transformation {
        @Override public Bitmap transform(Bitmap source) {

            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(context.getResources(),source);
            dr.setCornerRadius(Math.max(source.getWidth(),source.getHeight()) / 2.0f);

            return dr.getBitmap();

        }

        @Override public String key() { return "square()"; }
    }

}