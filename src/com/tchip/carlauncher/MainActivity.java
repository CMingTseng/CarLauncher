package com.tchip.carlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private SharedPreferences sharedPreferences;
    private Button btnLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("RouteSetting",
                getApplicationContext().MODE_PRIVATE);

        switch (getStyle()) {
            case 0: // 拟物风格
                setContentView(R.layout.activity_main);
                break;
            case 1: // 磁贴风格
                setContentView(R.layout.activity_main_metro);
                break;
        }
        startSpeak("欢迎使用天启行车记录仪");


        btnLogo = (Button) findViewById(R.id.btnLogo);
        btnLogo.setOnClickListener(new MyOnClickListener());

        Button btnNavigation = (Button) findViewById(R.id.btnNavigation);
        btnNavigation.setOnClickListener(new MyOnClickListener());

        Button btnDriveRecord = (Button) findViewById(R.id.btnDriveRecord);
        btnDriveRecord.setOnClickListener(new MyOnClickListener());

        Button btnFileManager = (Button) findViewById(R.id.btnFileManager);
        btnFileManager.setOnClickListener(new MyOnClickListener());

        Button btnBrowser = (Button) findViewById(R.id.btnBrowser);
        btnBrowser.setOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLogo:
                    Intent intent = new Intent(MainActivity.this, SetBrandActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnNavigation:
                    Toast.makeText(getApplicationContext(), "启动导航", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnDriveRecord:
                    Toast.makeText(getApplicationContext(), "启动行车记录仪", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnFileManager:
                    Toast.makeText(getApplicationContext(), "启动文件管理器", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnBrowser:
                    Toast.makeText(getApplicationContext(), "启动浏览器", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void startSpeak(String content) {
        Intent intent = new Intent(this, SpeakService.class);
        intent.putExtra("content", content);
        startService(intent);
    }

    private int getLogo(int brand) {
        switch (brand) {
            case 0: // 设置品牌
                return R.drawable.set_logo;
            case 1: // 奥迪
                return R.drawable.logo_audi;
            case 2: // 宝骏
                return R.drawable.logo_baojun;
            case 3: // 宝马
                return R.drawable.logo_bmw;
            case 4: // 保时捷
                return R.drawable.logo_porsche;
            case 5: // 北汽
                return R.drawable.logo_beiqi;
            case 6: // 奔驰
                return R.drawable.logo_benz;
            case 7: // 本田
                return R.drawable.logo_honda;
            case 8: // 标致
                return R.drawable.logo_peugeot;
            case 9: // 比亚迪
                return R.drawable.logo_byd;
            case 10: // 别克
                return R.drawable.logo_buick;
            case 11: // 宾利
                return R.drawable.logo_bently;
            case 12: // 长安
                return R.drawable.logo_changan;
            case 13: // 长城
                return R.drawable.logo_changcheng;
            case 14: // 昌河
                return R.drawable.logo_changhe;
            case 15: // 传祺
                return R.drawable.logo_chuanqi;
            case 16: // 道奇
                return R.drawable.logo_dodge;
            case 17: // 大众
                return R.drawable.logo_das;
            case 18: // 帝豪
                return R.drawable.logo_dihao;
            case 19: // 东风
                return R.drawable.logo_dongfeng;
            case 20: // 东南
                return R.drawable.logo_dongnan;
            case 21: // 法拉利
                return R.drawable.logo_farrari;
            case 22: // 菲亚特
                return R.drawable.logo_fiat;
            case 23: // 丰田
                return R.drawable.logo_toyota;
            case 24: // 福特
                return R.drawable.logo_ford;
            case 25: // 海马
                return R.drawable.logo_haima;
            case 26: // 红旗
                return R.drawable.logo_hongqi;
            case 27: // 捷豹
                return R.drawable.logo_jaguar;
            case 28: // 吉利
                return R.drawable.logo_geely;
            case 29: // 江淮
                return R.drawable.logo_jac;
            case 30: // 吉普
                return R.drawable.logo_jeep;
            case 31: // 凯迪拉克
                return R.drawable.logo_cadillac;
            case 32: // 兰博基尼
                return R.drawable.logo_lamborghini;
            case 33: // 雷克萨斯
                return R.drawable.logo_lexus;
            case 34: // 雷诺
                return R.drawable.logo_renault;
            case 35: // 铃木
                return R.drawable.logo_suzuki;
            case 36: // 林肯
                return R.drawable.logo_lincoln;
            case 37: // 陆风
                return R.drawable.logo_lufeng;
            case 38: // 路虎
                return R.drawable.logo_landrover;
            case 39: // 迈巴赫
                return R.drawable.logo_maybach;
            case 40: // 玛莎拉蒂
                return R.drawable.logo_maserati;
            case 41: // 马自达
                return R.drawable.logo_mazda;
            case 42: // 纳智捷
                return R.drawable.logo_luxgen;
            case 43: // 讴歌
                return R.drawable.logo_acura;
            case 44: // 启辰
                return R.drawable.logo_qichen;
            case 45: // 奇瑞
                return R.drawable.logo_chery;
            case 46: // 起亚
                return R.drawable.logo_kia;
            case 47: // 日产
                return R.drawable.logo_nissan;
            case 48: // 荣威
                return R.drawable.logo_roewe;
            case 49: // 萨博
                return R.drawable.logo_saab;
            case 50: // 三菱
                return R.drawable.logo_mitsubishi;
            case 51: // 上汽
                return R.drawable.logo_saic;
            case 52: // 斯巴鲁
                return R.drawable.logo_subaru;
            case 53: // 斯柯达
                return R.drawable.logo_skoda;
            case 54: // 特斯拉
                return R.drawable.logo_tesla;
            case 55: // 沃尔沃
                return R.drawable.logo_volvo;
            case 56: // 五菱
                return R.drawable.logo_wuling;
            case 57: // 现代
                return R.drawable.logo_hyundai;
            case 58: // 雪佛兰
                return R.drawable.logo_chevrolet;
            case 59: // 雪铁龙
                return R.drawable.logo_citroen;
            case 60: // 英菲尼迪
                return R.drawable.logo_infiniti;
            case 61: // 中华
                return R.drawable.logo_zhonghua;
            case 62: // 众泰
                return R.drawable.logo_zhongtai;
            default: // 设置品牌
                return R.drawable.set_logo;
        }
    }

    public int getBrand() {
        return sharedPreferences.getInt("brand", 0);
    }

    public int getStyle() {
        return sharedPreferences.getInt("style", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnLogo.setBackgroundResource(getLogo(getBrand()));
    }
}
