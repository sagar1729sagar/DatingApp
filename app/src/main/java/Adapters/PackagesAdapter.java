package Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Models.Packages;
import Models.User;
import Util.Prefs;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ssapps.com.datingapp.MainActivity;
import ssapps.com.datingapp.R;
import ssapps.com.datingapp.SettingsFragment;

public class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.MyViewHolder>{

    private List<Packages> packages = new ArrayList<>();
    private static final String euro = "\u20ac";
    private Context context;
    private Prefs prefs;
    private SweetAlertDialog information,dialog,error;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title,price,p0,p1,p2,p3,p4,p5,p6,p7,p8,p9,description;
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
            description = (TextView)view.findViewById(R.id.package_description);

        }


    }

    public PackagesAdapter(Context context,List<Packages> packages){
        this.context = context;
        this.packages = packages;
        prefs = new Prefs(context);
        information = new SweetAlertDialog(context,SweetAlertDialog.NORMAL_TYPE);
        dialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        dialog.dismiss();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.packages_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Packages myPackage = new Packages();
        holder.title.setText(myPackage.getPackageName());
        holder.price.setText("("+myPackage.getPrice()+euro+")/month");
        setTexts(position,myPackage,holder,true);

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.description.getVisibility() == View.GONE && holder.p0.getVisibility() == View.GONE){
                    holder.arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow));
                    setTexts(position,myPackage,holder,true);
                } else {
                    holder.arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.down_arrow));
                    setTexts(position,myPackage,holder,false);
                }
            }
        });


        holder.orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getname().equals("None")){
                    information.setTitleText("No user found");
                    information.setContentText("Please login or create a new account to continue");
                    information.show();
                } else {
                    //todo initiate order
                }
            }
        });


    }


    private void afterOrderCompletetion(String PackageName){
        User currentuser = User.find(User.class,"username = ?",prefs.getname()).get(0);
        if (currentuser.getPackages().equals("None")){
            currentuser.setIsPremiumMember("yes");
            currentuser.setPackages(PackageName);
        } else {
            String pn = currentuser.getPackages();
            currentuser.setPackages(pn+","+PackageName);
        }
        dialog.setTitleText("Saving...");
        dialog.show();
        Backendless.Data.save(currentuser, new AsyncCallback<User>() {
            @Override
            public void handleResponse(User response) {
                dialog.dismiss();
                Toast.makeText(context,"Your order has been saved successfully",Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("PackagesRedirect",true);
                context.startActivity(i);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
            dialog.dismiss();
                Toast.makeText(context,"The following error occured while saving your user profile"+
                "\n"+fault.getMessage()+"Please contact administrator",Toast.LENGTH_LONG).show();
            }
        });
    }





    private void setTexts(int position, Packages myPackage, MyViewHolder holder,boolean show) {
        if (show) {
            if (myPackage.getDescription().isEmpty() || myPackage.getDescription().equals("")) {
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(myPackage.getDescription());
            }
            if (myPackage.getBp0().isEmpty() || myPackage.getBp0().equals("")) {
                holder.p0.setVisibility(View.GONE);
            } else {
                holder.p0.setVisibility(View.VISIBLE);
                holder.p0.setText(myPackage.getBp0());
            }
            if (myPackage.getBp1().isEmpty() || myPackage.getBp1().equals("")) {
                holder.p1.setVisibility(View.GONE);
            } else {
                holder.p1.setVisibility(View.VISIBLE);
                holder.p1.setText(myPackage.getBp0());
            }
            if (myPackage.getBp2().isEmpty() || myPackage.getBp2().equals("")) {
                holder.p2.setVisibility(View.GONE);
            } else {
                holder.p2.setVisibility(View.VISIBLE);
                holder.p2.setText(myPackage.getBp0());
            }
            if (myPackage.getBp3().isEmpty() || myPackage.getBp3().equals("")) {
                holder.p3.setVisibility(View.GONE);
            } else {
                holder.p3.setVisibility(View.VISIBLE);
                holder.p3.setText(myPackage.getBp0());
            }
            if (myPackage.getBp4().isEmpty() || myPackage.getBp4().equals("")) {
                holder.p4.setVisibility(View.GONE);
            } else {
                holder.p4.setVisibility(View.VISIBLE);
                holder.p4.setText(myPackage.getBp0());
            }
            if (myPackage.getBp5().isEmpty() || myPackage.getBp5().equals("")) {
                holder.p5.setVisibility(View.GONE);
            } else {
                holder.p5.setVisibility(View.VISIBLE);
                holder.p5.setText(myPackage.getBp0());
            }
            if (myPackage.getBp6().isEmpty() || myPackage.getBp6().equals("")) {
                holder.p6.setVisibility(View.GONE);
            } else {
                holder.p6.setVisibility(View.VISIBLE);
                holder.p6.setText(myPackage.getBp0());
            }
            if (myPackage.getBp7().isEmpty() || myPackage.getBp7().equals("")) {
                holder.p7.setVisibility(View.GONE);
            } else {
                holder.p7.setVisibility(View.VISIBLE);
                holder.p7.setText(myPackage.getBp0());
            }
            if (myPackage.getBp8().isEmpty() || myPackage.getBp8().equals("")) {
                holder.p8.setVisibility(View.GONE);
            } else {
                holder.p8.setVisibility(View.VISIBLE);
                holder.p8.setText(myPackage.getBp0());
            }
            if (myPackage.getBp9().isEmpty() || myPackage.getBp9().equals("")) {
                holder.p9.setVisibility(View.GONE);
            } else {
                holder.p9.setVisibility(View.VISIBLE);
                holder.p9.setText(myPackage.getBp0());
            }
        } else if (!show){
            holder.description.setVisibility(View.GONE);
            holder.p0.setVisibility(View.GONE);
            holder.p1.setVisibility(View.GONE);
            holder.p2.setVisibility(View.GONE);
            holder.p3.setVisibility(View.GONE);
            holder.p4.setVisibility(View.GONE);
            holder.p5.setVisibility(View.GONE);
            holder.p6.setVisibility(View.GONE);
            holder.p7.setVisibility(View.GONE);
            holder.p8.setVisibility(View.GONE);
            holder.p9.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }


}