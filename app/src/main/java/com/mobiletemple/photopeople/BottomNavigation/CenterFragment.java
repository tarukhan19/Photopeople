package com.mobiletemple.photopeople.BottomNavigation;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.mobiletemple.photopeople.AlbumPrinting.AlbumPrinting;
import com.mobiletemple.photopeople.BuySell.BuySellActivity;
import com.mobiletemple.photopeople.CalenderActivity;
import com.mobiletemple.photopeople.ContactUsActivity;
import com.mobiletemple.photopeople.Freelancer.FreelancerDetails;
import com.mobiletemple.photopeople.ProfilePic.PickerBuilder;
import com.mobiletemple.photopeople.ProfileUpdate;
import com.mobiletemple.photopeople.ProjectActivity;
import com.mobiletemple.photopeople.R;
import com.mobiletemple.photopeople.Studio.StudioProfileDetail;
import com.mobiletemple.photopeople.WalletActivity;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.databinding.FragmentCenterBinding;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;
import com.mobiletemple.photopeople.util.BitmapUtil;
import com.mobiletemple.photopeople.util.Endpoints;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class CenterFragment extends Fragment {
    Intent intent;
    SessionManager session;
    private int CAMERA_REQUEST = 11, GALLERY_REQUEST = 12;
    private Bitmap profilePicC, bitmap;
    private byte[] profilepic = null;
    String strpic = "",  pic;
    float maxHeight = 1000.1f;
    float maxWidth = 1000.1f;
    float minHeight = 150.1f;
    float minWidth = 100.1f;
    float imgRatio, maxRatio;
    int actualWidth, actualHeight;
    Bitmap imageUpload;
    RequestQueue requestQueue;

FragmentCenterBinding binding;
    public CenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_center, container, false);
        View view = binding.getRoot();
        session = new SessionManager(getActivity().getApplicationContext());
        requestQueue = Volley.newRequestQueue(getActivity());

        binding.username.setText(session.getLoginSession().get(SessionManager.KEY_NAME));
        if (!session.getImageSession().get(SessionManager.KEY_IMAGE).isEmpty())
        { Picasso.with(getActivity()).load(session.getImageSession().get(SessionManager.KEY_IMAGE)).placeholder(R.drawable.default_new_img).fit().centerCrop()
                .into(binding.ivPic);
        }
        else {Picasso.with(getActivity()).load(R.drawable.default_new_img).placeholder(R.drawable.default_new_img).fit().centerCrop()
                .into(binding.ivPic);}

        binding.walletiv.setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_IN);

        binding.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_dlg_image);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView cameraLBL = (TextView) dialog.findViewById(R.id.cameraLBL);
                TextView gallLBL = (TextView) dialog.findViewById(R.id.gallLBL);
                ImageView crossIV = (ImageView) dialog.findViewById(R.id.crossIV);

                cameraLBL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        new PickerBuilder(getActivity(), PickerBuilder.SELECT_FROM_CAMERA)
                                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                                    @Override
                                    public void onImageReceived(Uri imageUri) {
                                        dialog.dismiss();

                                        Bitmap bitmap = null;
                                        try {
                                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        CompressResizeImage(bitmap);

                                        ChangeprofileTask changeprofileStudioTask = new ChangeprofileTask();
                                        changeprofileStudioTask.execute();
                                        binding.ivPic.setImageURI(imageUri);
                                    }
                                })
                                .setImageName("testImage")
                                .setImageFolderName("testFolder")
                                .withTimeStamp(true)
                                .setCropScreenColor(Color.MAGENTA)
                                .start();

                    }
                });

                gallLBL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                        photoPickerIntent.setType("image/*");
//                        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
//                        dialog.dismiss();

                        new PickerBuilder(getActivity(), PickerBuilder.SELECT_FROM_GALLERY)
                                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                                    @Override
                                    public void onImageReceived(Uri imageUri) {
                                        dialog.dismiss();
                                        binding.ivPic.setImageURI(imageUri);


                                        try {
                                            final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                            CompressResizeImage(selectedImage);
                                            ChangeprofileTask changeprofileStudioTask = new ChangeprofileTask();
                                            changeprofileStudioTask.execute();
                                        } catch (Exception ex) {
                                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                                .setImageName("test")
                                .setImageFolderName("testFolder")
                                .setCropScreenColor(Color.MAGENTA)
                                .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                                    @Override
                                    public void onPermissionRefused() {

                                    }
                                })
                                .start();
                    }
                });
                crossIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });



        binding.contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getActivity(),ContactUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.shopzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getActivity(),BuySellActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase(Constants.FREELANCER_TYPE)){
                    intent=new Intent(getActivity(),FreelancerDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("freelancerId",session.getLoginSession().get(SessionManager.KEY_USERID));
                    intent.putExtra("from","profile");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);

                }

                else {
                    intent=new Intent(getActivity(),StudioProfileDetail.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("studioId",session.getLoginSession().get(SessionManager.KEY_USERID));
                    intent.putExtra("from","profile");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }
            }
        });

        binding.wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getActivity(),WalletActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogOutDialog();
            }
        });

        binding.project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getActivity(),ProjectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getActivity(),ProfileUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.albumprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getActivity(),AlbumPrinting.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from","profile");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.mobiletemple.photopeople \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        binding.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (session.getLoginSession().get(SessionManager.KEY_USER_TYPE).equalsIgnoreCase("1"))
                {
                    Toast.makeText(getActivity(), "Only for Freelancer", Toast.LENGTH_SHORT).show();
                }
                else {

                    intent = new Intent(getActivity(), CalenderActivity.class);
                    intent.putExtra("from","profile");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }
            }
        });

        return view;
    }


    private void showLogOutDialog()
    {
        final SweetAlertDialog sweetAlertDialog=    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("Are you sure?")
                .setContentText("You want to Logout");
        sweetAlertDialog.show();



        Button btn = (Button) sweetAlertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));

        sweetAlertDialog.setConfirmText("Yes");
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                logOut();
                sweetAlertDialog.dismissWithAnimation();

            }
        });
        sweetAlertDialog.setCancelText("Cancel");
        Button button=(Button)sweetAlertDialog.findViewById(R.id.cancel_button);
        button.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });

    }



    private void logOut()
    {
       final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.LOGOUT, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response",response);
                    progressDialog.dismiss();
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("status").equalsIgnoreCase("200")) {
                        session.logoutUser();
                        FirebaseAuth.getInstance().signOut();

                        Intent in7 = new Intent(getActivity(), LoginActivity.class);
                        in7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(in7);
                        getActivity().overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                // Toast.makeText(dialog.getContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }


        ){  @Override
        protected Map<String, String> getParams() {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));

            Log.e("params",headers.toString());



            return headers;
        }};



        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            CompressResizeImage(photo);

            ChangeprofileTask changeprofileStudioTask = new ChangeprofileTask();
            changeprofileStudioTask.execute();
        }

        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                CompressResizeImage(selectedImage);
                ChangeprofileTask changeprofileStudioTask = new ChangeprofileTask();
                changeprofileStudioTask.execute();
            } catch (Exception ex) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        // //Log.e("profilePicC", profilePicC + "");
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class ChangeprofileTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Uploading In Process ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Endpoints comm = new Endpoints();
            try {
                JSONObject ob = new JSONObject();
                ob.put("user_id", session.getLoginSession().get(SessionManager.KEY_USERID));
                ob.put("user_type",  session.getLoginSession().get(SessionManager.KEY_USER_TYPE));
                String result = comm.forImageChange(Endpoints.CHANGE_IMAGE, ob, profilepic);

                //Log.e("result",result);

                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // //Log.e("Registration ", s);
            pd.cancel();
            try {
                JSONObject obj = new JSONObject(s);
                // //Log.e("object", ">>>>>>>" + obj);

                int status = obj.getInt("status");
                String message = obj.getString("message");
                String picurl = obj.getString("profile_Image");

                if (status == 200 && message.equalsIgnoreCase("success")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Profile Pic Updated Successfully", Toast.LENGTH_SHORT).show();

                    Picasso.with(getActivity()).load(picurl).placeholder(R.drawable.default_new_img).into(binding.ivPic);
                    session.setImageSession(picurl);


                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                //   //Log.e("Exception", ">>>>>>" + ex);
            }
        }
    }

    public void CompressResizeImage(Bitmap bm)
    {

        actualHeight = bm.getHeight();
        actualWidth = bm.getWidth();

        imgRatio = actualWidth / actualHeight;
        maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
                imageUpload = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);

                profilePicC = imageUpload;
                binding.ivPic.setImageBitmap(imageUpload);
                strpic = BitmapUtil.getStringFromBitmap(profilePicC);
                profilepic = Base64.decode(strpic, Base64.NO_WRAP);


            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
                imageUpload = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);

                profilePicC = imageUpload;
                binding.ivPic.setImageBitmap(imageUpload);
                strpic = BitmapUtil.getStringFromBitmap(profilePicC);
                profilepic = Base64.decode(strpic, Base64.NO_WRAP);


            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
                imageUpload = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);

                profilePicC = imageUpload;
                binding.ivPic.setImageBitmap(imageUpload);
                strpic = BitmapUtil.getStringFromBitmap(profilePicC);
                profilepic = Base64.decode(strpic, Base64.NO_WRAP);


            }
        } else if ((actualHeight < minHeight || actualWidth < minWidth)) {
            Toast.makeText(getActivity(), "Please choose an image that's at least 100 pixels wide and at least 150 pixels tall.", Toast.LENGTH_SHORT).show();
        } else {
            imageUpload = Bitmap.createScaledBitmap(bm, actualWidth, actualHeight, true);

            profilePicC = imageUpload;
            binding.ivPic.setImageBitmap(imageUpload);
            strpic = BitmapUtil.getStringFromBitmap(profilePicC);
            profilepic = Base64.decode(strpic, Base64.NO_WRAP);

        }

    }



}
