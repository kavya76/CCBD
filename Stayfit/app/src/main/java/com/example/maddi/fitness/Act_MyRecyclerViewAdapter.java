package com.example.maddi.fitness;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Act_MyRecyclerViewAdapter extends RecyclerView.Adapter<Act_MyRecyclerViewAdapter.ViewHolder> {
    public static float caloriecount = 0f;
    public static int count = 0;
    private List<Map<String, ?>> mDataset;
    private Context mContext;

    // Constructor
    public Act_MyRecyclerViewAdapter(Context myContext, List<Map<String, ?>> myDataset) {
        mContext = myContext;
        mDataset = myDataset;
    }

    // Using View Holder
    @Override
    public Act_MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_food, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Filling Data into ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, ?> food = mDataset.get(position);
        holder.bindMovieData((food));
    }

    // No of items in dataset
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // ViewHolder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView vTitle;
        public TextView vType;
        public TextView vCal;
        public Button vAdd;
        // Set Popup Window
        public RelativeLayout mRelativeLayout;
        public PopupWindow mPopupWindow;

        private FirebaseAuth mAuth;
        private DatabaseReference mDatabase;

        public ViewHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.title);
            vType = (TextView) v.findViewById(R.id.type);
            vCal = (TextView) v.findViewById(R.id.calories);
            vAdd = (Button) v.findViewById(R.id.addfood);
            //mRelativeLayout = (RelativeLayout) v.findViewById(R.id.recyclr_frag_pop);
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        private DatabaseReference getCaloriesRef(String ref) {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            return mDatabase.child("Calories").child(formattedDate).child(userId).child(ref);
        }

        public void bindMovieData(final Map<String, ?> fooditem) {

            vTitle.setText((String) fooditem.get(("iname")));
            vType.setText((String) fooditem.get("bname"));
            vCal.setText((String) fooditem.get("ical"));
            getCaloriesRef("totalcalories").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    caloriecount = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });;
            Log.d("caloriecount",String.valueOf(caloriecount));
            vAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                    Log.d("Before Adding", String.valueOf(caloriecount) );
                    caloriecount = caloriecount - (Float.parseFloat(String.valueOf(fooditem.get("ical"))));
                    getCaloriesRef("totalcalories").setValue(caloriecount);
                    Log.d("After Adding", String.valueOf(caloriecount));
                    Log.d("Adapter", (String.valueOf(Act_RecyclerFrag_Main.calRef1)));

                    if (count == 1) {
                        String toast1 = String.valueOf(count) + "item added";
                        Toast.makeText(mContext, toast1, Toast.LENGTH_SHORT).show();
                    } else if (count > 1) {
                        String toast2 = String.valueOf(count) + "items added";
                        Toast.makeText(mContext, toast2, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            JSONArray j = null;

        }
    }


}


