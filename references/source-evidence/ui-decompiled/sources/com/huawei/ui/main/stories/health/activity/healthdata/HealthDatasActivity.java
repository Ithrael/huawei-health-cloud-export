package com.huawei.ui.main.stories.health.activity.healthdata;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.huawei.haf.language.LanguageInstallHelper;
import com.huawei.haf.router.routes.AppRoute$$Info$$HuaweiHealth;
import com.huawei.haf.threadpool.ThreadPoolManager;
import com.huawei.health.BuildConfig;
import com.huawei.health.h5pro.core.H5ProLaunchOption;
import com.huawei.health.userprofilemgr.api.UserProfileMgrApi;
import com.huawei.hihealth.HiAggregateOption;
import com.huawei.hihealth.HiDataFilter;
import com.huawei.hihealth.HiDataReadOption;
import com.huawei.hihealth.HiDataReadProOption;
import com.huawei.hihealth.HiHealthData;
import com.huawei.hihealth.HiUserPreference;
import com.huawei.hihealth.api.HiHealthManager;
import com.huawei.hihealth.api.HiHealthNativeApi;
import com.huawei.hihealth.motion.HealthOpenSDK;
import com.huawei.hihealth.motion.IFlushResult;
import com.huawei.hihealth.util.HiJsonUtil;
import com.huawei.hwcloudmodel.model.unite.GetRunLevelRsp;
import com.huawei.hwcommonmodel.application.BaseApplication;
import com.huawei.hwcommonmodel.constants.AnalyticsValue;
import com.huawei.hwcommonmodel.utils.DateFormatUtil;
import com.huawei.hwlogsmodel.LogUtil;
import com.huawei.operation.h5pro.jsmodules.MenstrualModule;
import com.huawei.ui.commonui.base.BaseActivity;
import com.huawei.ui.commonui.healthtextview.HealthTextView;
import com.huawei.ui.commonui.subheader.HealthSubHeader;
import com.huawei.ui.commonui.utils.Utils;
import com.huawei.ui.main.R;
import com.huawei.ui.main.R$id;
import com.huawei.ui.main.R$layout;
import com.huawei.ui.main.R$string;
import com.huawei.ui.main.stories.fitness.activity.step.FitnessStepDetailActivity;
import com.huawei.ui.main.stories.health.interactors.healthdata.RunningStateIndexData;
import com.huawei.ui.main.stories.me.util.StepCounterSupportUtil;
import com.huawei.ui.main.stories.template.health.module.HealthDataDetailActivity;
import com.huawei.ui.main.stories.utils.LastTimeHealthDataReader;
import com.huawei.up.model.UserInfomation;
import defpackage.mnp;
import health.compact.a.CommonUtil;
import health.compact.a.HiCommonUtil;
import health.compact.a.HiDateUtil;
import health.compact.a.LanguageUtil;
import health.compact.a.ReleaseLogUtil;
import health.compact.a.Services;
import health.compact.a.UnitUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: /work/unpacked/classes3.dex */
public class HealthDatasActivity extends BaseActivity implements View.OnClickListener {
    private static boolean a = false;
    private static boolean b = false;
    private static boolean c = false;
    private static boolean d = false;
    private static boolean e = false;
    private static boolean g = false;
    private static boolean i = false;
    private HealthTextView aa;
    private HealthTextView ab;
    private RelativeLayout ac;
    private HealthTextView ad;
    private Context ae;
    private HealthTextView af;
    private HealthTextView ag;
    private RelativeLayout ah;
    private int ai;
    private HealthTextView aj;
    private int ak;
    private int al;
    private RelativeLayout am;
    private int an;
    private ae ao;
    private HealthOpenSDK ap;
    private Handler aq;
    private GetRunLevelRsp ar;
    private boolean as;
    private LastTimeHealthDataReader<HealthDatasActivity> at;
    private HealthTextView au;
    private HealthTextView av;
    private RelativeLayout aw;
    private Map<Integer, Long> ax;
    private RelativeLayout ay;
    private HealthTextView az;
    private Map<View, ClickViewCallback> ba;
    private List<HiHealthData> bb;
    private HealthTextView bc;
    private RelativeLayout bd;
    private RunningStateIndexData be;
    private RelativeLayout bf;
    private long bg;
    private HealthTextView bh;
    private HealthTextView bi;
    private HealthTextView bj;
    private ad bk;
    private RelativeLayout bl;
    private HealthTextView bm;
    private HealthTextView bn;
    private HealthTextView bo;
    private RelativeLayout bp;
    private int br;
    private HealthTextView bs;
    private RelativeLayout bt;
    private ah bu;
    private HealthTextView bv;
    private RelativeLayout bw;
    private af bx;
    private ae by;
    private HealthTextView bz;
    private RelativeLayout ca;
    private List<Integer> cb;
    private HealthTextView cc;
    private z cd;
    private RelativeLayout ce;
    private HealthTextView cf;
    private long cg;
    private int ch;
    private ae ci;
    private HealthTextView cj;
    private HealthTextView ck;
    private RelativeLayout cl;
    private wqy co;
    private HealthTextView f;
    private HealthTextView h;
    private HealthTextView k;
    private ae l;
    private RelativeLayout m;
    private ae n;
    private RelativeLayout o;
    private RelativeLayout p;
    private HealthTextView q;
    private RelativeLayout s;
    private HealthTextView t;
    private int u;
    private HealthTextView v;
    private HealthTextView w;
    private RelativeLayout x;
    private RelativeLayout y;
    private HealthTextView z;
    private String r = BuildConfig.OVE_TERMS_OF_SERVICE_VERSION;
    private boolean j = false;
    private int bq = StepCounterSupportUtil.c(BaseApplication.getContext());

    private int a(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        return calendar.get(7);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private RelativeLayout a(int i2, int i3) {
        View viewFindViewById = sdd.b(this, i2).findViewById(i3);
        if (viewFindViewById instanceof RelativeLayout) {
            return (RelativeLayout) viewFindViewById;
        }
        LogUtil.g("UIHLH_HealthDatasActivity", new Object[]{"getRelativeLayoutView view is not instanceof RelativeLayout"});
        return new RelativeLayout(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i2) {
        double dC = ((double) i2) / 1000.0d;
        if (UnitUtil.f()) {
            dC = UnitUtil.c(dC, 3);
            this.ag.setText(this.ae.getResources().getString(R$string.IDS_band_data_sport_distance_unit_en));
        } else {
            this.ag.setText(this.ae.getResources().getString(R$string.IDS_hw_show_sport_distance_unit));
        }
        this.af.setText(UnitUtil.d(dC, 1, dC < 1000.0d ? 2 : (dC < 1000.0d || dC >= 10000.0d) ? 0 : 1));
    }

    private void a(HealthTextView healthTextView) {
        View viewJ = j(R$id.health_data_item_achievement_layout, R$id.title_layout);
        if (viewJ == null || healthTextView == null) {
            LogUtil.g("UIHLH_HealthDatasActivity", new Object[]{"changTextViewParams view == null || textView == null"});
            return;
        }
        if (viewJ.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewJ.getLayoutParams();
            if (LanguageUtil.bp(this.ae)) {
                healthTextView.setMaxLines(1);
                layoutParams.weight = 2.0f;
                healthTextView.setEllipsize(TextUtils.TruncateAt.END);
            }
            viewJ.setLayoutParams(layoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, int i2) {
        HashMap map = new HashMap(16);
        map.put("click", 1);
        map.put("type", Integer.valueOf(i2));
        mgw.a().d(getApplicationContext(), str, map, 0);
    }

    private void aa() {
        c(R$id.health_data_item_step_layout, R$id.health_data_item_dis_layout, R$id.health_data_item_climb_layout, R$id.health_data_item_calorie_layout, R$id.health_data_item_bmp_layout, R$id.health_data_item_weight_layout, R$id.health_data_item_bloodpresure_layout, R$id.health_data_item_bloodsugar_layout, R$id.health_data_item_blood_oxygen_layout, R$id.health_data_item_vo2max_layout, R$id.health_data_item_stress_layout, R$id.health_data_item_running_layout, R$id.health_data_item_state_index_layout, R$id.health_data_item_temperature_layout, R$id.health_data_item_sleep_layout, R$id.health_data_item_lactic_layout, R$id.health_data_item_achievement_layout, R$id.health_data_item_physiologicalCycle_layout, R$id.health_data_item_load_layout);
    }

    private void ab() {
        StringBuilder sb = new StringBuilder("#/?from=1");
        if (h() == 1) {
            sb.append("&isActived=true");
        } else {
            sb.append("&isActived=false");
        }
        H5ProLaunchOption.Builder builder = new H5ProLaunchOption.Builder();
        builder.addPath(sb.toString());
        dqg.b().initH5Pro();
        builder.setImmerse().showStatusBar().setForceDarkMode(1).setStatusBarTextBlack(true).enableOnPauseCallback().enableOnResumeCallback().setNeedSoftInputAdapter();
        builder.addCustomizeJsModule("innerapi", dqg.b().getCommonJsModule("innerapi"));
        builder.addCustomizeJsModule("menstrual", MenstrualModule.class);
        dqg.b().loadH5ProApp(this.ae, "com.huawei.health.h5.cycle-calendar", builder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public void ac() {
        new kro().a(this, new x(this));
    }

    private void ad() {
        if (this.at == null) {
            this.at = new LastTimeHealthDataReader<>(this, new ai(this, 2));
        }
        this.at.b(LastTimeHealthDataReader.CardData.BLOOD_SUGAR);
    }

    private void ae() {
        UserInfomation userInfo = ((UserProfileMgrApi) Services.e(AppRoute$$Info$$HuaweiHealth.M_45, UserProfileMgrApi.class)).getUserInfo();
        if (userInfo == null) {
            userInfo = new UserInfomation(UnitUtil.f() ? 1 : 0);
        }
        this.al = userInfo.getGender();
        HiDataReadOption hiDataReadOption = new HiDataReadOption();
        hiDataReadOption.setTimeInterval(0L, System.currentTimeMillis());
        hiDataReadOption.setType(new int[]{47401});
        hiDataReadOption.setSortOrder(1);
        hiDataReadOption.setCount(1);
        HiDataReadProOption hiDataReadProOptionD = HiDataReadProOption.builder().d(hiDataReadOption).d(HiJsonUtil.a(new HiDataFilter(new HiDataFilter.DataFilterExpression(47401, HiDataFilter.DataFilterExpression.EQUAL, 101.0d)))).d();
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"requestLastData : ", hiDataReadProOptionD.toString()});
        HiHealthNativeApi.c(this.ae).readHiHealthDataPro(hiDataReadProOptionD, new r(this, 1));
    }

    private void af() {
        HiDataReadOption hiDataReadOptionN = n();
        this.bu = new ah(this);
        HiHealthNativeApi.c(BaseApplication.getContext()).readHiHealthData(hiDataReadOptionN, this.bu);
    }

    private void ag() {
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"Enter requestCoreSleepSummary!"});
        HiAggregateOption hiAggregateOptionF = f();
        this.bk = new ad(this);
        HiHealthManager.e(BaseApplication.getContext()).aggregateHiHealthData(hiAggregateOptionF, this.bk);
    }

    private void ah() {
        this.co.b(this.by);
    }

    private void ai() {
        HiDataReadOption hiDataReadOption = new HiDataReadOption();
        hiDataReadOption.setStartTime(0L);
        hiDataReadOption.setEndTime(System.currentTimeMillis());
        hiDataReadOption.setType(new int[]{44306});
        hiDataReadOption.setSortOrder(1);
        hiDataReadOption.setCount(1);
        this.bx = new af(this);
        HiHealthNativeApi.c(BaseApplication.getContext()).readHiHealthData(hiDataReadOption, this.bx);
    }

    private void aj() {
        if (Utils.ab()) {
            Utils.d(this.bv);
            Utils.d(this.bs);
            Utils.d(this.af);
            Utils.d(this.ag);
            Utils.d(this.ad);
            Utils.d(this.ab);
            Utils.d(this.aa);
            Utils.d(this.z);
            Utils.d(this.cc);
            Utils.d(this.cj);
            Utils.d(this.bj);
            Utils.d(this.bi);
            Utils.d(this.bn);
            Utils.d(this.bo);
            Utils.d(this.h);
            Utils.d(this.f);
            Utils.d(this.au);
            Utils.d(this.av);
            Utils.d(this.aj);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ak() {
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"startStepsActivity begin"});
        if (this.br >= 0.01d || this.bq != 3) {
            d(AnalyticsValue.HEALTH_HEALTH_FITNESSDATA_STEPS_2030004.value());
            this.ap.c((IFlushResult) null);
            Intent intent = new Intent(this.ae, (Class<?>) FitnessStepDetailActivity.class);
            intent.putExtra("today_current_steps_total", this.br);
            this.ae.startActivity(intent);
        } else {
            Toast.makeText(this.ae, this.ae.getString(R$string.IDS_hwh_home_hardware_limit_no_step_detail_data_tips), 0).show();
        }
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"startStepsActivity end"});
    }

    private void b(int i2) {
        View viewJ = j(i2, R$id.data_line);
        if (viewJ == null) {
            return;
        }
        viewJ.setVisibility(8);
    }

    public static void b(int i2, int i3) {
        HashMap map = new HashMap(3);
        map.put("click", 1);
        map.put("from", Integer.valueOf(i3));
        map.put("type", Integer.valueOf(i2));
        mgw.a().d(BaseApplication.getContext(), AnalyticsValue.SPORT_RECORD_ATHLETIC_ABILITY_CARD_2040192.value(), map, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(long j) {
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"requestData"});
        HiDataReadOption hiDataReadOption = new HiDataReadOption();
        hiDataReadOption.setStartTime(0L);
        hiDataReadOption.setEndTime(j);
        hiDataReadOption.setType(new int[]{47401});
        hiDataReadOption.setSortOrder(1);
        hiDataReadOption.setCount(720);
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"readHiHealthData : ", hiDataReadOption.toString()});
        HiHealthNativeApi.c(this.ae).readHiHealthData(hiDataReadOption, new r(this, 0));
    }

    private void b(ImageView imageView, int i2) {
        if (LanguageUtil.bc(this.ae)) {
            imageView.setBackground(sbj.c(this.ae, i2));
        } else {
            imageView.setBackgroundResource(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long c(int i2) {
        if (this.ax.containsKey(Integer.valueOf(i2))) {
            return this.ax.get(Integer.valueOf(i2)).longValue();
        }
        return 0L;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private HealthTextView c(int i2, int i3) {
        HealthTextView healthTextViewFindViewById = sdd.b(this, i2).findViewById(i3);
        if (healthTextViewFindViewById instanceof HealthTextView) {
            return healthTextViewFindViewById;
        }
        LogUtil.g("UIHLH_HealthDatasActivity", new Object[]{"getTextView view is not instanceof HealthTextView"});
        return new HealthTextView(this);
    }

    private void c(TextView textView, String str) {
        textView.post(new wjh(this, textView, str));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(List<HiHealthData> list) {
        boolean z;
        int i2;
        boolean z2;
        int iC;
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"handleData data size ", Integer.valueOf(list.size())});
        e(list);
        long jB = mnp.b(System.currentTimeMillis());
        int i3 = 0;
        while (true) {
            z = true;
            if (i3 >= list.size()) {
                i2 = 101;
                i3 = -1;
                z2 = true;
                break;
            } else if (jB == list.get(i3).getStartTime()) {
                i2 = (int) list.get(i3).getDouble("point_value");
                z2 = false;
                break;
            } else {
                list.get(i3).getStartTime();
                i3++;
            }
        }
        if (z2 || (iC = yqg.c("UIHLH_HealthDatasActivity", list, i3, this.bg)) < 0) {
            z = z2;
        } else {
            LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"handleData lastIndexTemp ", Integer.valueOf(iC), " isLong ", Boolean.valueOf(z2)});
        }
        Date date = new Date();
        if (z) {
            this.aq.obtainMessage(11, null).sendToTarget();
            return;
        }
        date.setTime(System.currentTimeMillis());
        int iA = (i3 - 21) + a(jB);
        for (HiHealthData hiHealthData : list) {
            if (hiHealthData != null) {
                LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"handleData, time: ", new Date(hiHealthData.getStartTime()), ", type: ", String.valueOf(hiHealthData.getDouble("point_value"))});
            }
        }
        if (iA < 0) {
            int i4 = 0;
            while (i4 < Math.abs(iA)) {
                HiHealthData hiHealthData2 = new HiHealthData();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(list.get(0).getStartTime());
                i4++;
                calendar.add(10, i4 * 24);
                hiHealthData2.setStartTime(calendar.getTimeInMillis());
                list.add(0, hiHealthData2);
            }
        }
        if (iA < 0) {
            i3 -= iA;
        }
        Pair pairE = yqg.e("UIHLH_HealthDatasActivity", i2, list, i3);
        Message messageObtainMessage = this.aq.obtainMessage(11);
        messageObtainMessage.obj = String.valueOf(pairE.second);
        this.aq.sendMessage(messageObtainMessage);
    }

    private void c(int... iArr) {
        if (LanguageUtil.bc(getApplication())) {
            for (int i2 : iArr) {
                ImageView imageViewE = e(i2, R$id.record_arrow);
                if (imageViewE != null) {
                    imageViewE.setImageResource(R.drawable.common_ui_arrow_left);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private HealthSubHeader d(int i2, int i3) {
        return sdd.b(this, i2).findViewById(i3);
    }

    private static String d(Context context, double d2, int i2, int i3) {
        return LanguageUtil.n(context) ? String.valueOf(d2) : UnitUtil.d(d2, i2, i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(String str) {
        HashMap map = new HashMap(16);
        map.put("click", 1);
        mgw.a().d(getApplicationContext(), str, map, 0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private ImageView e(int i2, int i3) {
        return (ImageView) sdd.b(this, i2).findViewById(i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e(int i2) {
        double d2;
        LogUtil.e("UIHLH_HealthDatasActivity", new Object[]{"setFloorText floors = ", Integer.valueOf(i2)});
        if (i2 < 0) {
            i2 = 0;
        }
        this.ab.setVisibility(0);
        if (UnitUtil.f()) {
            d2 = UnitUtil.i(((double) i2) / 10.0d)[0];
            this.ab.setText(this.ae.getResources().getString(R$string.IDS_ft));
        } else {
            d2 = ((double) i2) / 10.0d;
            this.ab.setText(this.ae.getResources().getString(R$string.IDS_fitness_data_list_activity_meter_unit));
        }
        if (i2 > 0) {
            this.ad.setText(d(getApplication(), d2, 1, 1));
        } else if (mma.e() && CommonUtil.bg()) {
            this.ad.setText(d(getApplication(), d2, 1, 1));
        } else {
            this.ad.setText("--");
            this.ab.setVisibility(8);
        }
    }

    private void e(List<HiHealthData> list) {
        for (HiHealthData hiHealthData : list) {
            if (hiHealthData != null) {
                LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"sortHealthData, time: ", new Date(hiHealthData.getStartTime()), ", type: ", String.valueOf(hiHealthData.getDouble("point_value"))});
            }
        }
        if (list == null) {
            return;
        }
        Collections.reverse(list);
        for (int size = list.size() - 1; size > 0 && Math.abs(list.get(size).getDouble("point_value")) < 1.0E-6d; size--) {
            list.remove(size);
        }
        Collections.reverse(list);
    }

    private HiAggregateOption f() {
        HiAggregateOption hiAggregateOption = new HiAggregateOption();
        hiAggregateOption.setStartTime(0L);
        hiAggregateOption.setEndTime(HiDateUtil.h(System.currentTimeMillis()));
        hiAggregateOption.setAggregateType(1);
        hiAggregateOption.setGroupUnitType(3);
        hiAggregateOption.setGroupUnitSize(1);
        hiAggregateOption.setReadType(0);
        hiAggregateOption.setSortOrder(1);
        hiAggregateOption.setCount(1);
        hiAggregateOption.setType(new int[]{44102, 44103, 44101, 44001, 44002, 44108, 44105, 44109});
        hiAggregateOption.setConstantsKey(new String[]{"core_sleep_deep_key", "core_sleep_shallow_key", "core_sleep_wake_dream_key", "sleep_deep_key", "sleep_shallow_key", "sleep_core_sleep_noon_duration_key", "core_sleep_total_sleep_time_key", "data_session_manual_sleep_bed_time_key"});
        return hiAggregateOption;
    }

    private int h() {
        HiUserPreference userPreference = HiHealthManager.e(this.ae).getUserPreference("com.huawei.health.mc");
        if (userPreference == null || HiCommonUtil.c(userPreference.getValue())) {
            LogUtil.g("UIHLH_HealthDatasActivity", new Object[]{"MENSTRUAL_CONFIG_KEY is null!"});
            return 0;
        }
        try {
            return new JSONObject(userPreference.getValue()).getInt("masterSwitch");
        } catch (JSONException unused) {
            ReleaseLogUtil.a("UIHLH_HealthDatasActivity", new Object[]{"getMenstrualActiveState JSONException"});
            return 0;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private View j(int i2, int i3) {
        return sdd.b(this, i2).findViewById(i3);
    }

    private void k() {
        this.bw = a(R$id.health_data_item_step_layout, R$id.item_rl);
        this.ah = a(R$id.health_data_item_dis_layout, R$id.item_rl);
        this.ac = a(R$id.health_data_item_climb_layout, R$id.item_rl);
        this.x = a(R$id.health_data_item_calorie_layout, R$id.item_rl);
        this.y = (RelativeLayout) findViewById(R$id.health_data_item_bmp_layout);
        this.cl = (RelativeLayout) findViewById(R$id.health_data_item_weight_layout);
        this.bl = (RelativeLayout) findViewById(R$id.health_data_item_sleep_layout);
        this.p = (RelativeLayout) findViewById(R$id.health_data_item_bloodpresure_layout);
        this.s = (RelativeLayout) findViewById(R$id.health_data_item_bloodsugar_layout);
        this.ca = (RelativeLayout) findViewById(R$id.health_data_item_temperature_layout);
        this.bd = (RelativeLayout) findViewById(R$id.health_data_item_physiologicalCycle_layout);
        this.o = (RelativeLayout) findViewById(R$id.health_data_item_blood_oxygen_layout);
        this.bt = (RelativeLayout) findViewById(R$id.health_data_item_stress_layout);
        this.ce = a(R$id.health_data_item_vo2max_layout, R$id.item_rl);
        RelativeLayout relativeLayoutA = a(R$id.health_data_item_load_layout, R$id.item_rl);
        this.ay = relativeLayoutA;
        relativeLayoutA.setVisibility(8);
        e(R$id.health_data_item_load_layout, R$id.record_arrow).setVisibility(4);
        this.bf = a(R$id.health_data_item_running_layout, R$id.item_rl);
        this.bp = a(R$id.health_data_item_state_index_layout, R$id.item_rl);
        this.m = a(R$id.health_data_item_achievement_layout, R$id.item_rl);
        this.aw = (RelativeLayout) findViewById(R$id.health_data_item_lactic_layout);
        if (!gil.i()) {
            this.aw.setVisibility(8);
        }
        this.am = (RelativeLayout) findViewById(R$id.health_data_item_ftp_layout);
    }

    private void l() {
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"initClickViewMap begin"});
        this.ba = new HashMap<View, ClickViewCallback>(16) { // from class: com.huawei.ui.main.stories.health.activity.healthdata.HealthDatasActivity.2
            private static final long serialVersionUID = 6568366952987213870L;

            {
                put(HealthDatasActivity.this.bw, new l(HealthDatasActivity.this));
                put(HealthDatasActivity.this.ah, new i(HealthDatasActivity.this));
                put(HealthDatasActivity.this.ac, new f(HealthDatasActivity.this));
                put(HealthDatasActivity.this.x, new h(HealthDatasActivity.this));
                put(HealthDatasActivity.this.y, new g(HealthDatasActivity.this));
                put(HealthDatasActivity.this.cl, new q(HealthDatasActivity.this));
                put(HealthDatasActivity.this.bl, new o(HealthDatasActivity.this));
                put(HealthDatasActivity.this.p, new d(HealthDatasActivity.this));
                put(HealthDatasActivity.this.s, new b(HealthDatasActivity.this));
                put(HealthDatasActivity.this.bd, new c(HealthDatasActivity.this));
                put(HealthDatasActivity.this.ca, new t(HealthDatasActivity.this));
                put(HealthDatasActivity.this.ce, new p(HealthDatasActivity.this));
                put(HealthDatasActivity.this.bf, new m(HealthDatasActivity.this, (AnonymousClass4) null));
                put(HealthDatasActivity.this.bp, new n(HealthDatasActivity.this, (AnonymousClass4) null));
                put(HealthDatasActivity.this.m, new e(HealthDatasActivity.this, (AnonymousClass4) null));
                put(HealthDatasActivity.this.aw, new k(HealthDatasActivity.this));
                put(HealthDatasActivity.this.bt, new s(HealthDatasActivity.this));
                put(HealthDatasActivity.this.o, new a(HealthDatasActivity.this));
                put(HealthDatasActivity.this.am, new j(HealthDatasActivity.this));
            }
        };
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"initClickViewMap end"});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void m() {
        if (jhl.c()) {
            jgs.e("HealthDatasActivity", "0".equals(this.r));
        } else {
            HealthDataDetailActivity.b(this.ae, "BloodSugarCardConstructor", 8);
        }
    }

    private HiDataReadOption n() {
        HiDataReadOption hiDataReadOption = new HiDataReadOption();
        hiDataReadOption.setStartTime(0L);
        hiDataReadOption.setEndTime(HiDateUtil.h(System.currentTimeMillis()));
        hiDataReadOption.setType(new int[]{2034});
        hiDataReadOption.setSortOrder(1);
        hiDataReadOption.setCount(1);
        return hiDataReadOption;
    }

    private void o() {
        this.cc = c(R$id.health_data_item_vo2max_layout, R$id.record_data);
        this.cj = c(R$id.health_data_item_vo2max_layout, R$id.record_data_unit);
        c(R$id.health_data_item_vo2max_layout, R$id.content_title).setText(this.ae.getResources().getString(R$string.IDS_hwh_health_vo2max));
        e(R$id.health_data_item_vo2max_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_oxygen_uptake);
        this.cj.setText(this.ae.getResources().getString(R$string.IDS_hwh_health_vo2max_unit));
        this.bj = c(R$id.health_data_item_running_layout, R$id.record_data);
        this.bi = c(R$id.health_data_item_running_layout, R$id.record_data_unit);
        c(R$id.health_data_item_running_layout, R$id.content_title).setText(this.ae.getResources().getString(R$string.IDS_data_running_index_title_outside));
        e(R$id.health_data_item_running_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_rq_running);
        this.bn = c(R$id.health_data_item_state_index_layout, R$id.record_data);
        this.bo = c(R$id.health_data_item_state_index_layout, R$id.record_data_unit);
        c(R$id.health_data_item_state_index_layout, R$id.content_title).setText(this.ae.getResources().getString(R$string.IDS_data_state_index_title));
        e(R$id.health_data_item_state_index_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_rq_state_index);
        this.h = c(R$id.health_data_item_achievement_layout, R$id.record_data);
        this.f = c(R$id.health_data_item_achievement_layout, R$id.record_data_unit);
        HealthTextView healthTextViewC = c(R$id.health_data_item_achievement_layout, R$id.content_title);
        healthTextViewC.setText(this.ae.getResources().getString(R$string.IDS_hwh_health_vo2max_record_forecast));
        a(healthTextViewC);
        e(R$id.health_data_item_achievement_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_rq_achievement_prediction);
        this.h.setText("");
        this.f.setText("");
        this.au = c(R$id.health_data_item_lactic_layout, R$id.record_data);
        this.av = c(R$id.health_data_item_lactic_layout, R$id.record_data_unit);
        c(R$id.health_data_item_lactic_layout, R$id.content_title).setText(R$string.IDS_running_lactate_threshold);
        e(R$id.health_data_item_lactic_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_lactic);
        this.av.setText("");
        this.aj = c(R$id.health_data_item_ftp_layout, R$id.record_data);
        c(R$id.health_data_item_ftp_layout, R$id.content_title).setText(R$string.IDS_cycling_ftp);
        e(R$id.health_data_item_ftp_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ftp_item);
    }

    private void p() {
        this.v = c(R$id.health_data_item_bmp_layout, R$id.record_data);
        this.w = c(R$id.health_data_item_bmp_layout, R$id.record_data_unit);
        c((TextView) c(R$id.health_data_item_bmp_layout, R$id.content_title), this.ae.getResources().getString(R$string.IDS_main_heart_health_string));
        e(R$id.health_data_item_bmp_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_heart_rate);
        this.ck = c(R$id.health_data_item_weight_layout, R$id.record_data);
        c((TextView) c(R$id.health_data_item_weight_layout, R$id.content_title), health.compact.a.Utils.k() ? this.ae.getResources().getString(R$string.IDS_hw_show_set_weight) : this.ae.getResources().getString(R$string.IDS_health_weight_manager));
        e(R$id.health_data_item_weight_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_weight);
        this.bz = c(R$id.health_data_item_stress_layout, R$id.record_data);
        c((TextView) c(R$id.health_data_item_stress_layout, R$id.content_title), this.ae.getResources().getString(R$string.IDS_settings_one_level_menu_settings_item_text_id14));
        e(R$id.health_data_item_stress_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_pressure);
        this.bm = c(R$id.health_data_item_sleep_layout, R$id.record_data);
        c((TextView) c(R$id.health_data_item_sleep_layout, R$id.content_title), this.ae.getResources().getString(R$string.IDS_hw_show_main_home_page_sleep));
        e(R$id.health_data_item_sleep_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_sleep);
        this.q = c(R$id.health_data_item_bloodpresure_layout, R$id.record_data);
        c((TextView) c(R$id.health_data_item_bloodpresure_layout, R$id.content_title), this.ae.getResources().getString(R$string.IDS_hw_show_main_home_page_bloodpressure));
        e(R$id.health_data_item_bloodpresure_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_blood_pressure);
        s();
    }

    private void q() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.co.c(getApplication(), new long[]{0, jCurrentTimeMillis}, 20, this.ci);
        this.co.d(this.n);
        ad();
        ag();
        af();
        this.co.c(getApplication(), 0L, jCurrentTimeMillis, this.ao);
        z zVar = new z(this);
        this.cd = zVar;
        this.co.b(zVar);
        ThreadPoolManager.a().execute(new wja(this));
        ThreadPoolManager.a().execute(new wjb(this));
        ThreadPoolManager.a().execute(new wjd(this));
        qzt.d().a(new y(this));
        this.co.d(getApplication(), 0L, jCurrentTimeMillis, this.l);
        ah();
        ae();
    }

    private void r() {
        StepCounterSupportUtil.d(new StepCounterSupportUtil.StepCounterClassCallback() { // from class: com.huawei.ui.main.stories.health.activity.healthdata.HealthDatasActivity.4
            public void getDeviceClass(int i2) {
                ReleaseLogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"initStepCounterClass type", Integer.valueOf(i2)});
                HealthDatasActivity.this.bq = i2;
            }
        }, true);
    }

    private void s() {
        this.k = c(R$id.health_data_item_blood_oxygen_layout, R$id.record_data);
        c((TextView) c(R$id.health_data_item_blood_oxygen_layout, R$id.content_title), this.ae.getResources().getString(R$string.IDS_hw_health_blood_oxygen));
        e(R$id.health_data_item_blood_oxygen_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_blood_oxygen_saturation);
        this.t = c(R$id.health_data_item_bloodsugar_layout, R$id.record_data);
        c((TextView) c(R$id.health_data_item_bloodsugar_layout, R$id.content_title), this.ae.getResources().getString(R$string.IDS_hw_show_main_home_page_bloodsugar));
        e(R$id.health_data_item_bloodsugar_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_blood_sugar);
        this.cf = c(R$id.health_data_item_temperature_layout, R$id.record_data);
        c((TextView) c(R$id.health_data_item_temperature_layout, R$id.content_title), this.as ? this.ae.getResources().getString(R$string.IDS_health_skin_temperature) : this.ae.getResources().getString(R$string.IDS_settings_health_temperature));
        e(R$id.health_data_item_temperature_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_temperature);
        this.bh = c(R$id.health_data_item_physiologicalCycle_layout, R$id.record_data);
        c((TextView) c(R$id.health_data_item_physiologicalCycle_layout, R$id.content_title), this.ae.getResources().getString(R$string.IDS_hw_physiological_cycle));
        e(R$id.health_data_item_physiologicalCycle_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_slzq);
    }

    private void t() {
        this.az = c(R$id.health_data_item_load_layout, R$id.record_data);
        this.bc = c(R$id.health_data_item_load_layout, R$id.record_data_unit);
        c(R$id.health_data_item_load_layout, R$id.content_title).setText(this.ae.getResources().getString(R$string.IDS_hwh_health_load));
        e(R$id.health_data_item_load_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_load);
    }

    private void u() {
        this.bv = c(R$id.health_data_item_step_layout, R$id.record_data);
        this.bs = c(R$id.health_data_item_step_layout, R$id.record_data_unit);
        c(R$id.health_data_item_step_layout, R$id.content_title).setText(this.ae.getResources().getString(R$string.IDS_settings_steps));
        b(e(R$id.health_data_item_step_layout, R$id.content_icon), R.mipmap.ic_mydata_step);
        this.bs.setText(this.ae.getResources().getString(R$string.IDS_hw_show_main_home_page_steps));
        this.af = c(R$id.health_data_item_dis_layout, R$id.record_data);
        this.ag = c(R$id.health_data_item_dis_layout, R$id.record_data_unit);
        c(R$id.health_data_item_dis_layout, R$id.content_title).setText(this.ae.getResources().getString(R$string.IDS_sport_distance));
        e(R$id.health_data_item_dis_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_distance);
        this.ad = c(R$id.health_data_item_climb_layout, R$id.record_data);
        this.ab = c(R$id.health_data_item_climb_layout, R$id.record_data_unit);
        c(R$id.health_data_item_climb_layout, R$id.content_title).setText(this.ae.getResources().getString(R$string.IDS_motiontrack_climb_stairs_tip));
        b(e(R$id.health_data_item_climb_layout, R$id.content_icon), R.mipmap.ic_mydata_climbing_stairs);
        this.aa = c(R$id.health_data_item_calorie_layout, R$id.record_data);
        this.z = c(R$id.health_data_item_calorie_layout, R$id.record_data_unit);
        c(R$id.health_data_item_calorie_layout, R$id.content_title).setText(this.ae.getResources().getString(R$string.IDS_active_caloric));
        e(R$id.health_data_item_calorie_layout, R$id.content_icon).setBackgroundResource(R.mipmap.ic_mydata_kcal);
        this.z.setText(this.ae.getResources().getString(R$string.IDS_hw_health_show_calorie_unit));
        p();
        o();
        t();
        aj();
        k();
        b(R$id.health_data_item_calorie_layout);
        b(R$id.health_data_item_physiologicalCycle_layout);
        b(R$id.health_data_item_ftp_layout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void v() {
        x();
    }

    private void w() {
        u();
        findViewById(R$id.personal_info_scroll).setScrollViewVerticalDirectionEvent(true);
        this.af.setText(UnitUtil.d(0.0d, 1, 2));
        if (UnitUtil.f()) {
            this.ag.setText(this.ae.getResources().getString(R$string.IDS_band_data_sport_distance_unit_en));
        } else {
            this.ag.setText(this.ae.getResources().getString(R$string.IDS_hw_show_sport_distance_unit));
        }
        this.bw.setOnClickListener(this);
        this.ah.setOnClickListener(this);
        this.ac.setOnClickListener(this);
        this.x.setOnClickListener(this);
        this.y.setOnClickListener(this);
        this.cl.setOnClickListener(this);
        this.bl.setOnClickListener(this);
        this.p.setOnClickListener(this);
        this.s.setOnClickListener(this);
        this.bd.setOnClickListener(this);
        this.ca.setOnClickListener(this);
        this.ce.setOnClickListener(this);
        this.bf.setOnClickListener(this);
        this.bp.setOnClickListener(this);
        this.m.setOnClickListener(this);
        this.bt.setOnClickListener(this);
        this.o.setOnClickListener(this);
        this.aw.setOnClickListener(this);
        this.am.setOnClickListener(this);
        aa();
        if (this.as) {
            this.s.setVisibility(8);
        } else {
            LogUtil.e("UIHLH_HealthDatasActivity", new Object[]{"is not satisfy."});
        }
        l();
        y();
    }

    private void x() {
        qpa.a().launchActivity(BaseApplication.getContext(), new Intent(), new wjf(this));
    }

    private void y() {
        d(R$id.heanth_data_subheader_action, R$id.item_title).setHeadTitleText(this.ae.getResources().getString(R$string.IDS_settings_active_status));
        d(R$id.heanth_data_subheader_action, R$id.item_title).setSplitterVisible(8);
        d(R$id.heanth_data_subheader_condition, R$id.item_title).setHeadTitleText(this.ae.getResources().getString(R$string.IDS_settings_health_status));
        d(R$id.heanth_data_subheader_power, R$id.item_title).setHeadTitleText(this.ae.getResources().getString(R$string.IDS_data_rq_title));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public void z() {
        ab abVar = new ab(this);
        new kro().c(this, DateFormatUtil.c(mna.d(System.currentTimeMillis(), 2, 0)), DateFormatUtil.c(System.currentTimeMillis()), 300010, abVar);
    }

    public /* synthetic */ boolean a(Context context, Intent intent) {
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"InstallCallback"});
        ab();
        return false;
    }

    public /* synthetic */ void e(final TextView textView, String str) {
        if (textView.getParent() instanceof View) {
            final ViewGroup viewGroup = (ViewGroup) textView.getParent();
            float width = viewGroup.getWidth() - com.github.mikephil.charting.utils.Utils.convertDpToPixel(16.0f);
            float f = width - (width / 3.0f);
            float fMeasureText = textView.getPaint().measureText(str);
            if (fMeasureText < f) {
                textView.setText(str);
                return;
            }
            LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{Integer.valueOf(textView.hashCode()), ", textViewMaxWidth=", Float.valueOf(f), ", textViewWidth=", Float.valueOf(fMeasureText)});
            textView.setMaxWidth((int) f);
            textView.setText(str);
            textView.post(new Runnable() { // from class: com.huawei.ui.main.stories.health.activity.healthdata.HealthDatasActivity.1
                @Override // java.lang.Runnable
                public void run() {
                    TextView textView2 = textView;
                    if (textView2 == null || textView2.getLayout() == null) {
                        LogUtil.g("UIHLH_HealthDatasActivity", new Object[]{"setAutoWidthTextView textView is null"});
                        return;
                    }
                    int lineCount = textView.getLayout().getLineCount();
                    if (lineCount > 1 && (viewGroup.getParent() instanceof View)) {
                        ((ViewGroup) viewGroup.getParent()).getLayoutParams().height = -2;
                    }
                    float f2 = 0.0f;
                    for (int i2 = 0; i2 < lineCount; i2++) {
                        float lineWidth = textView.getLayout().getLineWidth(i2);
                        if (lineWidth > f2) {
                            f2 = lineWidth;
                        }
                    }
                    LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{Integer.valueOf(textView.hashCode()), ", childMaxWidth=", Float.valueOf(f2)});
                    textView.setMaxWidth((int) f2);
                }
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void i() {
        int iC = iwb.c(new Date(System.currentTimeMillis()));
        new kro().a(this, iC, iC, 0, new aa(this));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"onClick begin"});
        if (this.ba.get(view) != null) {
            this.ba.get(view).startActivity();
        } else {
            LogUtil.a("UIHLH_HealthDatasActivity", new Object[]{"mMapClickCallback.get(view) is NULL"});
        }
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"onClick end"});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.huawei.ui.commonui.base.BaseActivity
    public void onConfigurationChanged(Configuration configuration) {
        LanguageInstallHelper.updateResources(this);
        super.onConfigurationChanged(configuration);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.huawei.ui.commonui.base.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.health_data_listview_item);
        this.as = health.compact.a.Utils.k();
        this.ae = this;
        r();
        ai();
        this.aq = new v(this);
        this.ci = new ae(this, 0);
        this.n = new ae(this, 1);
        this.ao = new ae(this, 4);
        this.l = new ae(this, 7);
        this.by = new ae(this, 8);
        this.co = wqy.a();
        this.ax = new HashMap(8);
        w();
        this.ap = fuf.c(this.ae).c();
        zfy.a("900300050", new u(this));
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(103);
        HiHealthNativeApi.c(BaseApplication.getContext()).subscribeHiHealthData(arrayList, new w(this));
        qzt.d().b();
        ReleaseLogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"onCreate typeList ", arrayList});
    }

    @Override // com.huawei.ui.commonui.base.BaseActivity
    public void onDestroy() {
        super.onDestroy();
        Handler handler = this.aq;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (okt.c(this.cb)) {
            HiHealthNativeApi.c(BaseApplication.getContext()).unSubscribeHiHealthData(this.cb, new wiz());
        }
    }

    @Override // com.huawei.ui.commonui.base.BaseActivity
    public void onResume() {
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"onResume begin"});
        super.onResume();
        q();
        final ac acVar = new ac(this);
        ThreadPoolManager.a().execute(new Runnable() { // from class: com.huawei.ui.main.stories.health.activity.healthdata.HealthDatasActivity.3
            @Override // java.lang.Runnable
            public void run() {
                if (HealthDatasActivity.this.ap != null) {
                    HealthDatasActivity.this.ap.d(acVar);
                }
            }
        });
        LogUtil.d("UIHLH_HealthDatasActivity", new Object[]{"onResume end"});
    }
}
