package net.coding.program.login.phone;


import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import net.coding.program.MainActivity_;
import net.coding.program.MyApp;
import net.coding.program.R;
import net.coding.program.common.Global;
import net.coding.program.common.TermsActivity;
import net.coding.program.common.base.MyJsonResponse;
import net.coding.program.common.guide.GuideActivity;
import net.coding.program.common.network.MyAsyncHttpClient;
import net.coding.program.common.ui.BaseFragment;
import net.coding.program.common.util.ViewStyleUtil;
import net.coding.program.common.widget.LoginEditText;
import net.coding.program.model.AccountInfo;
import net.coding.program.model.UserObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EFragment(R.layout.fragment_phone_set_password3)
public class PhoneSetGlobalFragment extends BaseFragment {

    @FragmentArg
    PhoneSetPasswordActivity.Type type;

    @ViewById
    LoginEditText email, globalKey;

    @ViewById
    TextView loginButton, textClause;

    @AfterViews
    final void initPhoneSetPasswordFragment() {
        ViewStyleUtil.editTextBindButton(loginButton, email, globalKey);
        if (type == PhoneSetPasswordActivity.Type.register) {
            textClause.setText(Html.fromHtml(PhoneSetPasswordActivity.REGIST_TIP));
        }
    }

    @Click
    void loginButton() {
        String emailString = email.getText().toString();
        String globalKeyString = globalKey.getText().toString();

        if (!InputCheck.checkEmail(getContext(), emailString)) {
            return;
        }

        String url = Global.HOST_API + "/account/activate/phone";
        RequestParams params = ((ParentActivity) getActivity()).getRequestParmas();
        params.put("email", emailString);
        params.put("global_key", globalKeyString);

        MyAsyncHttpClient.post(getActivity(), url, params, new MyJsonResponse(getActivity()) {
            @Override
            public void onMySuccess(JSONObject respanse) {
                super.onMySuccess(respanse);

                UserObject user = new UserObject(respanse.optJSONObject("data"));
                AccountInfo.saveAccount(getActivity(), user);
                MyApp.sUserObject = user;
                AccountInfo.saveReloginInfo(getActivity(), user.email, user.global_key);

                Global.syncCookie(getActivity());

                AccountInfo.saveLastLoginName(getActivity(), user.name);

                getActivity().sendBroadcast(new Intent(GuideActivity.BROADCAST_GUIDE_ACTIVITY));
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity_.class));
            }

            @Override
            public void onMyFailure(JSONObject response) {
                super.onMyFailure(response);

                showProgressBar(false, "");
            }
        });
//        RequestParams params = ((ParentActivity) getActivity()).getRequestParmas();
//        String url = type.getSetPasswordPhoneUrl(params);
//        params.put("password", SimpleSHA1.sha1(emailString));
//
//        MyAsyncHttpClient.post(getActivity(), url, params, new MyJsonResponse(((BaseActivity) getActivity())) {
//            @Override
//            public void onMySuccess(JSONObject response) {
//                super.onMySuccess(response);
//
//                if (type == PhoneSetPasswordActivity.Type.register) {
//                    loadCurrentUser();
//                } else {
//                    closeActivity();
//                }
//            }
//
//            @Override
//            public void onMyFailure(JSONObject response) {
//                super.onMyFailure(response);
//                showProgressBar(false, "");
//            }
//        });
//
        showProgressBar(true, "");
    }

    @Click
    void textClause() {
        Intent intent = new Intent(getActivity(), TermsActivity.class);
        startActivity(intent);
    }

    protected void loadCurrentUser() {
        AsyncHttpClient client = MyAsyncHttpClient.createClient(getActivity());
        String url = Global.HOST_API + "/current_user";
        client.get(getActivity(), url, new MyJsonResponse(getActivity()) {

            @Override
            public void onMySuccess(JSONObject respanse) {
                super.onMySuccess(respanse);
//                showProgressBar(false);
                UserObject user = new UserObject(respanse.optJSONObject("data"));
                AccountInfo.saveAccount(getActivity(), user);
                MyApp.sUserObject = user;
                AccountInfo.saveReloginInfo(getActivity(), user.email, user.global_key);

                Global.syncCookie(getActivity());

                AccountInfo.saveLastLoginName(getActivity(), user.name);

                getActivity().sendBroadcast(new Intent(GuideActivity.BROADCAST_GUIDE_ACTIVITY));
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity_.class));
            }

            @Override
            public void onMyFailure(JSONObject response) {
                super.onMyFailure(response);
                showProgressBar(false, "");
            }
        });
    }

//    protected void loadUserinfo() {
//        AsyncHttpClient client = MyAsyncHttpClient.createClient(getActivity());
//        String url = Global.HOST_API + "/userinfo";
//        client.get(getActivity(), url, new MyJsonResponse(getActivity()) {
//            @Override
//            public void onMySuccess(JSONObject response) {
//                super.onMySuccess(response);
//                MyData.getInstance().update(getActivity(), response.optJSONObject("data"));
//                closeActivity();
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                ((BaseActivity) getActivity()).showProgressBar(false, "");
//            }
//        });
//    }

    private void closeActivity() {
        Toast.makeText(getActivity(), type.getSetPasswordSuccess(), Toast.LENGTH_SHORT).show();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
