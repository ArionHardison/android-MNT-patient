package com.dietmanager.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.activities.DietitianListActivity;
import com.dietmanager.app.activities.FollowingsActivity;
import com.dietmanager.app.activities.WaitingForNewDietitianActivity;
import com.dietmanager.app.build.api.APIError;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.build.api.ErrorUtils;
import com.dietmanager.app.build.configure.BuildConfigure;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.models.Message;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.dietmanager.app.BuildConfig;
import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.AccountPaymentActivity;
import com.dietmanager.app.activities.EditAccountActivity;
import com.dietmanager.app.activities.FavouritesActivity;
import com.dietmanager.app.activities.LoginActivity;
import com.dietmanager.app.activities.ManageAddressActivity;
import com.dietmanager.app.activities.OrdersActivity;
import com.dietmanager.app.adapter.ProfileSettingsAdapter;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.utils.ListViewSizeHelper;
import com.dietmanager.app.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.text_line)
    TextView textLine;
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.view_line)
    View viewLine;
    @BindView(R.id.logout)
    Button logout;
    @BindView(R.id.arrow_image)
    ImageView arrowImage;
    @BindView(R.id.list_layout)
    RelativeLayout listLayout;
    @BindView(R.id.myaccount_layout)
    LinearLayout myaccountLayout;
    @BindView(R.id.error_layout)
    RelativeLayout errorLayout;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private Activity activity;
    private Context context;
    @BindView(R.id.profile_setting_lv)
    ListView profileSettingLv;
    private ViewGroup toolbar;
    private View toolbarLayout;
    ImageView userImage;
    TextView userName, userPhone, userEmail;
    private static final int REQUEST_LOCATION = 1450;
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        this.activity = getActivity();
        customDialog=new CustomDialog(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.updateNotificationCount(context, GlobalData.notificationCount);
        initView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toolbar != null) {
            toolbar.removeView(toolbarLayout);
        }
    }

    private void openSettingPage(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(context, ManageAddressActivity.class));
                break;
            case 1:
                startActivity(new Intent(context, FollowingsActivity.class));
                break;
            case 2:
                startActivity(new Intent(context, AccountPaymentActivity.class).putExtra("is_show_wallet", true)
                        .putExtra("is_show_cash", false)
                        .putExtra("isFromProfile", true).putExtra("without_cache", true));
                break;
            case 3:
                startActivity(new Intent(context, OrdersActivity.class));
                break;
            /*case 4:
                startActivity(new Intent(context, PromotionActivity.class));
                break;
            case 5:
                startActivity(new Intent(context, ReferralActivity.class));
                break;*/
            case 4:
                startActivity(new Intent(context, DietitianListActivity.class));
                break;
//            case 3:
//                changeLanguage();
//                break;
//            case 4:
//                startActivity(new Intent(context, ChangePasswordActivity.class));
//                break;
//            case 4:
//                startActivity(new Intent(context, NotificationActivity.class));
//                break;
//            case 5:
//                startActivity(new Intent(context, PromotionActivity.class));
//                break;
//            case 6:
//                startActivity(new Intent(context, ChangePasswordActivity.class));
//                break;
            default:
        }
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("ProfileFragment");
        toolbar = getActivity().findViewById(R.id.toolbar);
        if (GlobalData.profileModel != null) {
            toolbarLayout = LayoutInflater.from(context).inflate(R.layout.toolbar_profile, toolbar, false);
            userImage = toolbarLayout.findViewById(R.id.user_image);
            userName = toolbarLayout.findViewById(R.id.user_name);
            userPhone = toolbarLayout.findViewById(R.id.user_phone);
            userEmail = toolbarLayout.findViewById(R.id.user_mail);
            userImage.setVisibility(View.GONE);
            initView();
            Button editBtn = toolbarLayout.findViewById(R.id.edit);
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, EditAccountActivity.class));
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, EditAccountActivity.class));
                }
            });
            toolbar.addView(toolbarLayout);
            toolbar.setVisibility(View.VISIBLE);

            errorLayout.setVisibility(View.GONE);
            final List<String> list = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.profile_settings)));
            String loginBy = GlobalData.profileModel.getLoginBy();

            List<Integer> listIcons = new ArrayList<>();
            listIcons.add(R.drawable.ic_local_post);
            listIcons.add(R.drawable.ic_favorite_light);
            listIcons.add(R.drawable.payment);
            listIcons.add(R.drawable.ic_orders_light);
            listIcons.add(R.drawable.ic_diet);
            //listIcons.add(R.drawable.notification_background);
          //  listIcons.add(R.drawable.ic_share);
//            listIcons.add(R.drawable.ic_g_translate_light);

            /*if (!loginBy.equalsIgnoreCase("facebook") &&
                    !loginBy.equalsIgnoreCase("google")) {
                list.add(getResources().getString(R.string.chage_password));
                listIcons.add(R.drawable.ic_lock_outline_light);

            }*/

            ProfileSettingsAdapter adbPerson = new ProfileSettingsAdapter(context, list, listIcons);
            profileSettingLv.setAdapter(adbPerson);
            ListViewSizeHelper.getListViewSize(profileSettingLv);
            profileSettingLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openSettingPage(position);
                }
            });
            arrowImage.setTag(true);
//            collapse(listLayout);
            HomeActivity.updateNotificationCount(context, GlobalData.notificationCount);

            String VERSION_NAME = BuildConfig.VERSION_NAME;
            int versionCode = BuildConfig.VERSION_CODE;
            appVersion.setText("App version " + VERSION_NAME + " (" + versionCode + ")");
        } else {
            toolbar.setVisibility(View.GONE);
            //set Error Layout
            scrollView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    private void changeLanguage() {
        List<String> languages = Arrays.asList(getResources().getStringArray(R.array.languages));
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.language_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Change Language");
        final AlertDialog alert = alertDialog.create();
        final RadioGroup chooseLanguage = convertView.findViewById(R.id.choose_language);
        final RadioButton english = convertView.findViewById(R.id.english);
        final RadioButton arabic = convertView.findViewById(R.id.arabic);
        String dd = LocaleUtils.getLanguage(context);
        switch (dd) {
            case "en":
                english.setChecked(true);
                break;
            case "ar":
                arabic.setChecked(true);
                break;
            default:
                english.setChecked(true);
                break;
        }
        chooseLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.english:
                        setLanguage("English");
                        alert.dismiss();
                        break;
                    case R.id.arabic:
                        setLanguage("Arabic");
                        alert.dismiss();
                        break;
                }
            }
        });
        alert.show();
    }

    @OnClick(R.id.error_layout)
    public void onLoginShowClicked() {

    }

    private void setLanguage(String value) {
        SharedHelper.putKey(getActivity(), "language", value);
        switch (value) {
            case "English":
                LocaleUtils.setLocale(getActivity(), "en");
                break;
            case "Arabic":
                LocaleUtils.setLocale(getActivity(), "ar");
                break;
            default:
                LocaleUtils.setLocale(getActivity(), "en");
                break;
        }
        startActivity(new Intent(getActivity(), HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("change_language", true));
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initView() {
        if (GlobalData.profileModel != null) {
            Glide.with(context)
                    .load(BuildConfigure.BASE_URL+GlobalData.profileModel.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.man)
                            .error(R.drawable.man))
                    .into(userImage);
            userPhone.setText(GlobalData.profileModel.getPhone());
            userName.setText(GlobalData.profileModel.getName());
            userEmail.setText(" - " + GlobalData.profileModel.getEmail());
        }
    }

    @OnClick({R.id.arrow_image, R.id.logout, R.id.myaccount_layout, R.id.login_btn, R.id.unsubscribe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.myaccount_layout:
                if (arrowImage.getTag().equals(true)) {
                    //rotate arrow image
                    arrowImage.animate().setDuration(500).rotation(180).start();
                    arrowImage.setTag(false);
                    //collapse animation
                    collapse(listLayout);
                    viewLine.setVisibility(View.VISIBLE);
                    textLine.setVisibility(View.GONE);
                } else {
                    //rotate arrow image
                    arrowImage.animate().setDuration(500).rotation(360).start();
                    arrowImage.setTag(true);
                    viewLine.setVisibility(View.GONE);
                    textLine.setVisibility(View.VISIBLE);
                    //expand animation
                    expand(listLayout);
                }
                break;
            case R.id.logout:
                alertDialog();
                break;
            case R.id.unsubscribe:
                alertDialogUnsubscribe();
                break;
            case R.id.login_btn:
                SharedHelper.putKey(context, "logged", "false");
                startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
                break;
        }
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
//                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("MainAct", "Google User Logged out");
                               /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.alert_logout))
                .setPositiveButton(getResources().getString(R.string.logout), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        logout();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        nbutton.setTypeface(nbutton.getTypeface(), Typeface.BOLD);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);
    }

    public void alertDialogUnsubscribe() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.alert_unsubscribe))
                .setPositiveButton(getResources().getString(R.string.unsubscribe), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        unsubscribeAPI();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        nbutton.setTypeface(nbutton.getTypeface(), Typeface.BOLD);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);
    }
    public CustomDialog customDialog;
    public ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    private void unsubscribeAPI() {
                customDialog.show();
                apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
                Call<Message> call = apiInterface.unsubscribe();
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                        customDialog.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            activity.startActivity(new Intent(activity, WaitingForNewDietitianActivity.class));
                            activity.finishAffinity();
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            if (error != null) {
                                if (error.getType() != null) {
                                    Toast.makeText(context, error.getType().get(0), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                        customDialog.dismiss();
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                });

    }
}