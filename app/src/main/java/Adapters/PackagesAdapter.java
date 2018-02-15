package Adapters;

import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Models.Packages;
import ssapps.com.datingapp.R;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.MyViewHolder>{

    private ArrayList<Packages> packages = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title,price,p0,p1,p2,p3,p4,p5,p6,p7,p8,p9;
        public ImageView arrow;
        public Button orderButton;

        public MyViewHolder(View view){
            super(view);
            title = (TextView)view.findViewById(R.id.package_title);
            price = (TextView)view.findViewById(R.id.package_price);
            p0 = (TextView)view.findViewById(R.id.package_p0);
            p1 = (TextView)view.findViewById(R.id.package_p1);
            p2 = (TextView)view.findViewById(R.id.package_p2);
            p3 = (TextView)view.findViewById(R.id.package_p3);
            p4 = (TextView)view.findViewById(R.id.package_p4);
            p5 = (TextView)view.findViewById(R.id.package_p5);
            p6 = (TextView)view.findViewById(R.id.package_p6);
            p7 = (TextView)view.findViewById(R.id.package_p7);
            p8 = (TextView)view.findViewById(R.id.package_p8);
            p9 = (TextView)view.findViewById(R.id.package_p9);
            arrow = (ImageView) view.findViewById(R.id.package_arrow);
            orderButton = (Button)view.findViewById(R.id.package_order);

        }


    }

    public PackagesAdapter(ArrayList<Packages> packages){
        this.packages = packages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.packages_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //todo
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }


}