package com.mobiletemple.photopeople;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.mobiletemple.photopeople.Network.ConnectivityReceiver;
import com.mobiletemple.photopeople.Network.MyApplication;
import com.mobiletemple.photopeople.model.LensDTO;
import com.mobiletemple.photopeople.model.LightningDTO;
import com.mobiletemple.photopeople.model.SupportDto;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateEquipment extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    RecyclerView lensrecycle,lightningrecycle,supportrecycle;
    TextView  cameraspinner, lensspinner,lightningspinner,supportingspinner;
    LinearLayout nextButton;
    SessionManager sessionManager;
    RequestQueue queue;
    ProgressDialog dialog;
    Intent intent;

    RecyclerView lensrv,lightrv,supportrv;
    Dialog lensDialog,lightdialog,supportdialog;
    AlertDialog.Builder lensalertdialog,lightalertdialog,supportalertdialog;
    View lensView,supportview,lightview;

    ArrayList<LensDTO> lensDTOArrayList;
    ArrayList<SupportDto> supportDtoArrayList;
    ArrayList<LightningDTO> lightningDTOArrayList;


    ArrayList<String> lenslistsentrray,lenslistnamerray,cameraIdarraylist,cameraNamearraylist, lightningnamearraylist,lightningsentarraylist,supportsentaraylist,supportnamearraylist;
    LensAdapter lensAdapter;
    LensAdp lensAdp;
    boolean isConnected;
    SupportAdapter supportAdapter;
    SupportAdp supportAdp;


    LightningAdapter lightningAdapter;
    LightningAdp lightningAdp;

    ListView camLV;
    LinearLayout cameraLL;
    Dialog camdialog;
    ArrayAdapter<String> cameraarrayadapter;

    String namestring,genderString, mobilestring, locationstring, dobString,travelBy, ageS = "0", emailidstring, adressstring, pricestring, cameranamestring, cameraIdtring,lensidstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_equipment);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        LinearLayout backImage =  toolbar.findViewById(R.id.backImage);
        LinearLayout filter = (LinearLayout) toolbar.findViewById(R.id.filter);

        mTitle.setText("Update Equipments");
        filter.setVisibility(View.INVISIBLE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UpdateEquipment.this,ProfileUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
            }
        });

        queue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(getApplicationContext());
        dialog = new ProgressDialog(this);
        intent = getIntent();

        lensrecycle=findViewById(R.id.lensrecycle);
        supportrecycle=findViewById(R.id.supportrecycle);
        lightningrecycle=findViewById(R.id.lightningrecycle);

        supportsentaraylist=new ArrayList<>();
        supportnamearraylist=new ArrayList<>();

        camLV = new ListView(this);
        camdialog = new Dialog(UpdateEquipment.this);
        cameraNamearraylist=new ArrayList<>();
        cameraIdarraylist=new ArrayList<>();
        cameraarrayadapter = new ArrayAdapter<String>(this,
                R.layout.item_cameraspinner, R.id.text, cameraNamearraylist);

        lensDTOArrayList=new ArrayList<>();
        lenslistsentrray=new ArrayList<>();
        lenslistnamerray=new ArrayList<>();
        lensAdapter=new LensAdapter(this, lensDTOArrayList);

        lightningDTOArrayList=new ArrayList<>();
        lightningnamearraylist=new ArrayList<>();
        lightningsentarraylist=new ArrayList<>();
        lightningAdapter=new LightningAdapter(this, lightningDTOArrayList);

        supportDtoArrayList=new ArrayList<>();
        supportnamearraylist=new ArrayList<>();
        supportsentaraylist=new ArrayList<>();
        supportAdapter=new SupportAdapter(this, supportDtoArrayList);

        cameraLL= findViewById(R.id.cameraLL);
        cameraspinner = findViewById(R.id.cameraspinner);
        lensspinner = findViewById(R.id.lensspinner);
        lightningspinner = findViewById(R.id.lightningspinner);
        supportingspinner = findViewById(R.id.supportingspinner);
        nextButton=findViewById(R.id.nextButton);

        lensalertdialog = new AlertDialog.Builder(this);
        lensalertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        lensView=  LayoutInflater.from(this).inflate(R.layout.item_equipment_rv, null);
        lensalertdialog.setView(lensView);
        lensDialog = lensalertdialog.create();
        lensrv = (RecyclerView) lensView.findViewById(R.id.rv);
        lensrv.setLayoutManager(new LinearLayoutManager(this));
        lensrv.setHasFixedSize(true);
        lensrv.setAdapter(lensAdapter);


        supportalertdialog = new AlertDialog.Builder(this);
        supportalertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        supportview=  LayoutInflater.from(this).inflate(R.layout.item_equipment_support, null);
        supportalertdialog.setView(supportview);
        supportdialog = supportalertdialog.create();
        supportrv = (RecyclerView) supportview.findViewById(R.id.rv);
        supportrv.setLayoutManager(new LinearLayoutManager(this));
        supportrv.setHasFixedSize(true);
        supportrv.setAdapter(supportAdapter);




        lightalertdialog = new AlertDialog.Builder(this);
        lightalertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        lightview=  LayoutInflater.from(this).inflate(R.layout.item_equipment_light, null);
        lightalertdialog.setView(lightview);
        lightdialog = lightalertdialog.create();
        lightrv = (RecyclerView) lightview.findViewById(R.id.rv);
        lightrv.setLayoutManager(new LinearLayoutManager(this));
        lightrv.setHasFixedSize(true);
        lightrv.setAdapter(lightningAdapter);

        supportrecycle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        supportAdp =new SupportAdp (this, supportsentaraylist,supportnamearraylist,supportDtoArrayList);
        lensrecycle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lensAdp=new LensAdp(this, lenslistsentrray,lenslistnamerray,lensDTOArrayList);

        lightningrecycle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lightningAdp =new LightningAdp (this, lightningsentarraylist,lightningnamearraylist,lightningDTOArrayList);

        lensspinner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                lensalertdialog.setView(lensrv);
                lensDialog.show();
            }
        });

        supportingspinner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                supportalertdialog.setView(supportrv);
                supportdialog.show();
            }
        });


        lightningspinner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                lightalertdialog.setView(lightrv);
                lightdialog.show();
            }
        });


        camLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                cameraspinner.setText(txt.getText().toString());
                cameranamestring = txt.getText().toString();
                cameraIdtring = cameraIdarraylist.get(position).toString();
                camdialog.dismiss();
            }
        });

        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camdialog.setContentView(camLV);
                camdialog.show();

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else
                {
                    ProfileTask task = new ProfileTask();
                    task.execute();
                }
            }
        });


    }

    private void loadSpinnerData()
    {
        dialog.setMessage("Loading Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.LOADEQUIPMENTS, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                Log.e("response",response);
                try {
                    dialog.dismiss();
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    String status=obj.getString("status");
                    String message=obj.getString("message");

                    if (status.equalsIgnoreCase("200")&& message.equalsIgnoreCase("success"))
                    {
                        JSONArray camera_listing = obj.getJSONArray("camera_listing");
                        JSONArray suporting_listng = obj.getJSONArray("suporting_listing");
                        JSONArray lighting_listing = obj.getJSONArray("light_listing");
                        JSONArray lence_listing = obj.getJSONArray("lens_listing");

                        JSONObject selectCamera=obj.getJSONObject("camera");

                        String camName=selectCamera.getString("camera_name");
                        if (camName.equalsIgnoreCase("null"))
                        {
                            cameraspinner.setHint("Select Camera");
                        }

                        else {cameraspinner.setText(camName);}
                        cameraIdtring=selectCamera.getString("id");

                        for (int i = 0; i < camera_listing.length(); i++)
                        {
                            JSONObject camera_listingobject = camera_listing.getJSONObject(i);
                            String camera_id = camera_listingobject.getString("id");
                            String camera_name = camera_listingobject.getString("camera_name");
                            cameraIdarraylist.add(camera_id);
                            cameraNamearraylist.add(camera_name);
                        }
                        camLV.setAdapter(cameraarrayadapter);

                        JSONArray select_lens_listng = obj.getJSONArray("lence");


                        for (int i = 0; i < lence_listing.length(); i++)
                        {
                            LensDTO lensDTO=new LensDTO();
                            JSONObject lence_listingobject = lence_listing.getJSONObject(i);
                            String lence_id = lence_listingobject.getString("id");
                            String lence_name = lence_listingobject.getString("lence_name");

                            lensDTO.setName(lence_name);
                            lensDTO.setId(lence_id);
                            lensDTOArrayList.add(lensDTO);

                            for (int j=0;j<select_lens_listng.length();j++)
                            {
                                JSONObject select_llensobject = select_lens_listng.getJSONObject(j);
                                String select_lens_id = select_llensobject.getString("id");
                                String select_lens_name = select_llensobject.getString("lence_name");
                                if (select_lens_id.equalsIgnoreCase(lence_id))
                                {
                                    lensDTO.setSelected(!lensDTO.isSelected());
                                    lenslistsentrray.add(select_lens_id);
                                    lenslistnamerray.add(select_lens_name);

                                }


                            }
                            lensrv.setAdapter(lensAdapter);
                            lensrecycle.setAdapter(lensAdp);

                        }

                        JSONArray select_lighting_listng = obj.getJSONArray("light");

                        for (int i = 0; i < lighting_listing.length(); i++)
                        {

                            LightningDTO lightningDTO=new LightningDTO();
                            JSONObject lighting_listingobject = lighting_listing.getJSONObject(i);
                            String lighting_id = lighting_listingobject.getString("id");
                            String lighting_name = lighting_listingobject.getString("light_name");
                            lightningDTO.setName(lighting_name);
                            lightningDTO.setId(lighting_id);
                            lightningDTOArrayList.add(lightningDTO);


                            for (int j=0;j<select_lighting_listng.length();j++)
                            {
                                JSONObject select_lightingobject = select_lighting_listng.getJSONObject(j);
                                String select_lighting_id = select_lightingobject.getString("id");
                                String select_lighting_name = select_lightingobject.getString("light_name");
                                if (select_lighting_id.equalsIgnoreCase(lighting_id))
                                {
                                    lightningDTO.setSelected(!lightningDTO.isSelected());
                                    lightningsentarraylist.add(select_lighting_id);
                                    lightningnamearraylist.add(select_lighting_name);

                                }

                            }
                            lightrv.setAdapter(lightningAdapter);
                            lightningrecycle.setAdapter(lightningAdp);
                        }

                       // lightLV.setAdapter(lightningnarrayadapter);

                        JSONArray select_suporting_listng = obj.getJSONArray("suport");

                        for (int i = 0; i < suporting_listng.length(); i++)
                        {
                            SupportDto supportDto=new SupportDto();

                            JSONObject suporting_listngobject = suporting_listng.getJSONObject(i);
                            String suporting_id = suporting_listngobject.getString("id");
                            String suporting_name = suporting_listngobject.getString("suporting_name");
                            supportDto.setName(suporting_name);
                            supportDto.setId(suporting_id);
                            supportDtoArrayList.add(supportDto);

                            for (int j=0;j<select_suporting_listng.length();j++)
                            {
                                JSONObject select_suportingobject = select_suporting_listng.getJSONObject(j);
                                String select_suporting_id = select_suportingobject.getString("id");
                                String select_suporting_name = select_suportingobject.getString("suporting_name");
                                if (select_suporting_id.equalsIgnoreCase(suporting_id))
                                {
                                    supportDto.setSelected(!supportDto.isSelected());
                                    supportsentaraylist.add(select_suporting_id);
                                    supportnamearraylist.add(select_suporting_name);

                                }



                            }
                            supportrv.setAdapter(supportAdapter);
                            supportrecycle.setAdapter(supportAdp);
                        }


                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(dialog.getContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("user_id",sessionManager.getLoginSession().get(SessionManager.KEY_USERID));



                //Log.e("params", ob.toString());
                return ob;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }


    // Profile Upload
    class ProfileTask extends AsyncTask<String, Void, String>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(UpdateEquipment.this);
            pd.setMessage("Profile Uploading ...");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            Endpoints comm = new Endpoints();

            try {


                JSONObject ob = new JSONObject();
                ob.put("user_id", sessionManager.getuserId().get(SessionManager.KEY_USERID));

                ob.put("camera_id",cameraIdtring);



                //Log.e("JSON", ob.toString());

                // String result = comm.getStringResponse(Endpoints.SIGNUP_URL,ob);
                String result = comm.forUpdateFreelancerEquipments(Endpoints.UPDATE_FREELANCER_EQUIPMENTS, ob,lenslistsentrray, lightningsentarraylist,supportsentaraylist);


                //Log.e("Registration response", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }


        }

        @Override
        protected void onPostExecute(String s)
        {
            //Log.e("Upload Response ", s);
            pd.cancel();
            try {
                if (s != null) {
                    JSONObject obj = new JSONObject(s);
                    int status = obj.getInt("status");
                    String message = obj.getString("Message");
                    if (status == 200 && message.equals("success"))
                    {

                        final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(UpdateEquipment.this, SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Profile Updated!");
                        sweetAlertDialog.show();

                        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
                        btn.setBackgroundColor(ContextCompat.getColor(UpdateEquipment.this,R.color.colorPrimary));

                        sweetAlertDialog.setConfirmText("Ok");
                        btn.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                sweetAlertDialog.dismissWithAnimation();

                                Intent in = new Intent(UpdateEquipment.this, ProfileUpdate.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(in);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);



                            }
                        });
                        sweetAlertDialog.show();

                    }


                    else {
                        Toast.makeText(UpdateEquipment.this, "Profile Upload Failed; Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
            }
        }
    }





    private class LensAdapter extends RecyclerView.Adapter<LensAdapter.CustomViewHodler>
    {

        private Context mContext;
        ArrayList<LensDTO> lensDTOS;
        public LensAdapter(Context context, ArrayList<LensDTO> lensDTOS) {
            this.mContext = context;
            this.lensDTOS = lensDTOS;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameraspinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final LensDTO lensDTO=lensDTOS.get(position);
            holder.text.setText(lensDTO.getName());
            holder.ll.setBackgroundColor(lensDTO.isSelected() ? Color.LTGRAY : Color.WHITE);

            holder.text.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    lensDTO.setSelected(!lensDTO.isSelected());
                    holder.ll.setBackgroundColor(lensDTO.isSelected() ? Color.LTGRAY : Color.WHITE);


                    if (lensDTO.isSelected()==true)
                    {
                        lenslistnamerray.add(lensDTO.getName());
                        lenslistsentrray.add(lensDTO.getId());
                        //Log.e("lenslistsentrrayadd",lenslistsentrray+" "+lenslistnamerray);

                    }
                    else
                    {
                        lenslistnamerray.remove(lensDTO.getName());
                        lenslistsentrray.remove(lensDTO.getId());
                        //Log.e("lenslistsentrrayrem",lenslistsentrray+" "+lenslistnamerray);

                    }

                    lensrecycle.setAdapter(lensAdp);
                    notifyDataSetChanged();
                 //   lensAdp.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return  lensDTOS == null ? 0 : lensDTOS.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
//            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll=itemView.findViewById(R.id.ll);
            }
        }
    }





    private class LensAdp extends RecyclerView.Adapter<LensAdp.CustomViewHodler>
    {
        private Context mContext;
        ArrayList<String> lenslistsent;
        ArrayList<String> lenslistname;
        ArrayList<LensDTO> lensDTOS;
        int click=0;
        public LensAdp(Context context,  ArrayList<String> lenslistsent, ArrayList<String> lenslistname, ArrayList<LensDTO> lensDTOS) {
            this.mContext = context;
            this.lenslistname=lenslistname;
            this.lenslistsent=lenslistsent;
            this.lensDTOS=lensDTOS;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameralisting, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position)
        {

            String name = lenslistname.get(position);
            final LensDTO lensDTO=lensDTOS.get(position);

            holder.text.setText(name);
            holder.imageView.setVisibility(View.GONE);

//
//            holder.imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    lenslistsentrray.remove(position);
//                    lenslistname.remove(position);
//                    lensDTO.setSelected(false);
//
//
//                    ll.setBackgroundColor(lensDTO.isSelected() ? Color.LTGRAY : Color.WHITE);
//                    lensAdapter=new LensAdapter(mContext, lensDTOArrayList);
//
//                    rv.setAdapter(lensAdapter);
//                    notifyDataSetChanged();
//                    lensAdapter.notifyDataSetChanged();
//                    //Log.e("lenslistsentrrayrec",lenslistsentrray+" "+lenslistname);
//                }
//            });
//




        }

        @Override
        public int getItemCount() {
            return  lenslistsent == null ? 0 : lenslistsent.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            ImageView imageView;
            TextView text;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                imageView = (ImageView) itemView.findViewById(R.id.cross);
            }
        }
    }





    private class SupportAdapter extends RecyclerView.Adapter<SupportAdapter.CustomViewHodler>
    {

        private Context mContext;
        ArrayList<SupportDto> supportDtos;
        public SupportAdapter(Context context, ArrayList<SupportDto> supportDtos) {
            this.mContext = context;
            this.supportDtos = supportDtos;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameraspinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final SupportDto supportDto=supportDtos.get(position);
            holder.text.setText(supportDto.getName());
            holder.ll.setBackgroundColor(supportDto.isSelected() ? Color.LTGRAY : Color.WHITE);

            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    supportDto.setSelected(!supportDto.isSelected());
                    holder.ll.setBackgroundColor(supportDto.isSelected() ? Color.LTGRAY : Color.WHITE);


                    if (supportDto.isSelected()==true)
                    {
                        supportnamearraylist.add(supportDto.getName());
                        supportsentaraylist.add(supportDto.getId());
                        //Log.e("lenslistsentrrayadd",supportnamearraylist+" "+supportsentaraylist);

                    }
                    else
                    {
                        supportnamearraylist.remove(supportDto.getName());
                        supportsentaraylist.remove(supportDto.getId());
                        //Log.e("lenslistsentrrayrem",supportnamearraylist+" "+supportsentaraylist);

                    }

                    supportrecycle.setAdapter(supportAdp);
                    notifyDataSetChanged();
                    //   lensAdp.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return  supportDtos == null ? 0 : supportDtos.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll=itemView.findViewById(R.id.ll);
            }
        }
    }





    private class SupportAdp extends RecyclerView.Adapter<SupportAdp.CustomViewHodler>
    {
        private Context mContext;
        ArrayList<String> supportlistsent;
        ArrayList<String>  supportlistname;
        ArrayList<SupportDto>  supportDTOS;
        int click=0;
        public SupportAdp(Context context,  ArrayList<String> supportlistsent, ArrayList<String> supportlistname, ArrayList<SupportDto> supportDTOS)
        {
            this.mContext = context;
            this.supportlistname=supportlistname;
            this.supportlistsent=supportlistsent;
            this.supportDTOS=supportDTOS;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameralisting, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position)
        {

            String name = supportlistname.get(position);
            final SupportDto supportDto=supportDTOS.get(position);

            holder.text.setText(name);
            holder.imageView.setVisibility(View.GONE);



        }

        @Override
        public int getItemCount() {
            return  supportlistsent == null ? 0 : supportlistsent.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            ImageView imageView;
            TextView text;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                imageView = (ImageView) itemView.findViewById(R.id.cross);
            }
        }
    }







    private class LightningAdapter extends RecyclerView.Adapter<LightningAdapter.CustomViewHodler>
    {

        private Context mContext;
        ArrayList<LightningDTO> lightningDTOS;
        public LightningAdapter(Context context, ArrayList<LightningDTO> lightningDTOS) {
            this.mContext = context;
            this.lightningDTOS = lightningDTOS;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameraspinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position)
        {

            final LightningDTO lightningDTO=lightningDTOS.get(position);
            holder.text.setText(lightningDTO.getName());
            holder.ll.setBackgroundColor(lightningDTO.isSelected() ? Color.LTGRAY : Color.WHITE);

            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    lightningDTO.setSelected(!lightningDTO.isSelected());
                    holder.ll.setBackgroundColor(lightningDTO.isSelected() ? Color.LTGRAY : Color.WHITE);


                    if (lightningDTO.isSelected()==true)
                    {
                        lightningnamearraylist.add(lightningDTO.getName());
                        lightningsentarraylist.add(lightningDTO.getId());
                        //Log.e("lenslistsentrrayadd",lightningnamearraylist+" "+lightningsentarraylist);

                    }
                    else
                    {
                        lightningnamearraylist.remove(lightningDTO.getName());
                        lightningsentarraylist.remove(lightningDTO.getId());
                        //Log.e("lenslistsentrrayrem",lightningnamearraylist+" "+lightningsentarraylist);

                    }

                    lightningrecycle.setAdapter(lightningAdp);
                    notifyDataSetChanged();
                    //   lensAdp.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return  lightningDTOS == null ? 0 : lightningDTOS.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll=itemView.findViewById(R.id.ll);
            }
        }
    }





    private class  LightningAdp extends RecyclerView.Adapter<LightningAdp.CustomViewHodler>
    {
        private Context mContext;
        ArrayList<String> lightlistsent;
        ArrayList<String>  lightlistname;
        ArrayList<LightningDTO>  lightningDTOS;
        int click=0;

        public LightningAdp(Context context,  ArrayList<String> lightlistsent, ArrayList<String> lightlistname, ArrayList<LightningDTO> lightningDTOS)
        {
            this.mContext = context;
            this.lightlistsent=lightlistsent;
            this.lightlistname=lightlistname;
            this.lightningDTOS=lightningDTOS;
        }


        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameralisting, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position)
        {

            String name = lightlistname.get(position);
            final LightningDTO lightningDTO=lightningDTOS.get(position);

            holder.text.setText(name);
            holder.imageView.setVisibility(View.GONE);



        }

        @Override
        public int getItemCount() {
            return  lightlistsent == null ? 0 : lightlistsent.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            ImageView imageView;
            TextView text;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                imageView = (ImageView) itemView.findViewById(R.id.cross);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(UpdateEquipment.this,ProfileUpdate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,R.anim.trans_left_out);
    }


    @Override
    public void onStart()
    {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else
        {
            loadSpinnerData();
        }
        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        //Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            loadSpinnerData();
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        //Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }
}
