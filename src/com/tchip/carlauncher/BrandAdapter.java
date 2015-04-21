package com.tchip.carlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AlexZhou on 2015/4/10.
 * 10:05
 */
public class BrandAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Map<String, Object>> mData;
    private Map<String, Object> adapterMap;

    public BrandAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        InitListData();

    }

    private void InitListData() {
        mData = new ArrayList<Map<String, Object>>();
        // A
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_audi_small);
        adapterMap.put("brandName", "奥迪"); // 1
        mData.add(adapterMap);

        // B
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_baojun_small);
        adapterMap.put("brandName", "宝骏"); // 2
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_bmw_small);
        adapterMap.put("brandName", "宝马"); // 3
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_porsche_small);
        adapterMap.put("brandName", "保时捷"); // 4
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_beiqi_small);
        adapterMap.put("brandName", "北汽"); // 5
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_benz_small);
        adapterMap.put("brandName", "奔驰"); // 6
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_honda_small);
        adapterMap.put("brandName", "本田"); // 7
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_peugeot_small);
        adapterMap.put("brandName", "标致"); // 8
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_byd_small);
        adapterMap.put("brandName", "比亚迪"); // 9
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_buick_small);
        adapterMap.put("brandName", "别克"); // 10
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_bently_small);
        adapterMap.put("brandName", "宾利"); // 11
        mData.add(adapterMap);

        // C
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_changan_small);
        adapterMap.put("brandName", "长安"); // 12
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_changcheng_small);
        adapterMap.put("brandName", "长城"); // 13
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_changhe_small);
        adapterMap.put("brandName", "昌河"); // 14
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_chuanqi_small);
        adapterMap.put("brandName", "传祺"); // 15
        mData.add(adapterMap);

        // D
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_dodge_small);
        adapterMap.put("brandName", "道奇"); // 16
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_das_small);
        adapterMap.put("brandName", "大众"); // 17
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_dihao_small);
        adapterMap.put("brandName", "帝豪"); // 18
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_dongfeng_small);
        adapterMap.put("brandName", "东风"); // 19
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_dongnan_small);
        adapterMap.put("brandName", "东南"); // 20
        mData.add(adapterMap);

        // F
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_farrari_small);
        adapterMap.put("brandName", "法拉利"); // 21
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_fiat_small);
        adapterMap.put("brandName", "菲亚特"); // 22
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_toyota_small);
        adapterMap.put("brandName", "丰田"); // 23
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_ford_small);
        adapterMap.put("brandName", "福特"); // 24
        mData.add(adapterMap);

        // H
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_haima_small);
        adapterMap.put("brandName", "海马"); // 25
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_hongqi_small);
        adapterMap.put("brandName", "红旗"); // 26
        mData.add(adapterMap);

        // J
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_jaguar_small);
        adapterMap.put("brandName", "捷豹"); // 27
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_geely_small);
        adapterMap.put("brandName", "吉利"); // 28
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_jac_small);
        adapterMap.put("brandName", "江淮"); // 29
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_jeep_small);
        adapterMap.put("brandName", "吉普"); // 30
        mData.add(adapterMap);

        // K
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_cadillac_small);
        adapterMap.put("brandName", "凯迪拉克"); // 31
        mData.add(adapterMap);

        // L
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_lamborghini_small);
        adapterMap.put("brandName", "兰博基尼"); // 32
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_lexus_small);
        adapterMap.put("brandName", "雷克萨斯"); // 33
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_renault_small);
        adapterMap.put("brandName", "雷诺"); // 34
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_suzuki_small);
        adapterMap.put("brandName", "铃木"); // 35
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_lincoln_small);
        adapterMap.put("brandName", "林肯"); // 36
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_lufeng_small);
        adapterMap.put("brandName", "陆风"); // 37
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_landrover_small);
        adapterMap.put("brandName", "路虎"); // 38
        mData.add(adapterMap);

        // M
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_maybach_small);
        adapterMap.put("brandName", "迈巴赫"); // 39
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_maserati_small);
        adapterMap.put("brandName", "玛莎拉蒂"); // 40
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_mazda_small);
        adapterMap.put("brandName", "马自达"); // 41
        mData.add(adapterMap);

        // N
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_luxgen_small);
        adapterMap.put("brandName", "纳智捷"); // 42
        mData.add(adapterMap);

        // O
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_acura_small);
        adapterMap.put("brandName", "讴歌"); // 43
        mData.add(adapterMap);

        // Q
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_qichen_small);
        adapterMap.put("brandName", "启辰"); // 44
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_chery_small);
        adapterMap.put("brandName", "奇瑞"); // 45
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_kia_small);
        adapterMap.put("brandName", "起亚"); // 46
        mData.add(adapterMap);

        // R
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_nissan_small);
        adapterMap.put("brandName", "日产"); // 47
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_roewe_small);
        adapterMap.put("brandName", "荣威"); // 48
        mData.add(adapterMap);

        // S
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_saab_small);
        adapterMap.put("brandName", "萨博"); // 49
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_mitsubishi_small);
        adapterMap.put("brandName", "三菱"); // 50
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_saic_small);
        adapterMap.put("brandName", "上汽"); // 51
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_subaru_small);
        adapterMap.put("brandName", "斯巴鲁"); // 52
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_skoda_small);
        adapterMap.put("brandName", "斯柯达"); // 53
        mData.add(adapterMap);

        // T
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_tesla_small);
        adapterMap.put("brandName", "特斯拉"); // 54
        mData.add(adapterMap);

        // W
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_volvo_small);
        adapterMap.put("brandName", "沃尔沃"); // 55
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_wuling_small);
        adapterMap.put("brandName", "五菱"); // 56
        mData.add(adapterMap);

        // X
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_hyundai_small);
        adapterMap.put("brandName", "现代"); // 57
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_chevrolet_small);
        adapterMap.put("brandName", "雪佛兰"); // 58
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_citroen_small);
        adapterMap.put("brandName", "雪铁龙"); // 59
        mData.add(adapterMap);

        // Y
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_infiniti_small);
        adapterMap.put("brandName", "英菲尼迪"); // 60
        mData.add(adapterMap);

        // Z
        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_zhonghua_small);
        adapterMap.put("brandName", "中华"); // 61
        mData.add(adapterMap);

        adapterMap = new HashMap<String, Object>();
        adapterMap.put("brandLogo", R.drawable.logo_zhongtai_small);
        adapterMap.put("brandName", "众泰"); // 62
        mData.add(adapterMap);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        // convertView为null的时候初始化convertView。
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.set_brand_list_item, null);
            holder.brandLogo = (ImageView) convertView.findViewById(R.id.brandImage);
            holder.brandName = (TextView) convertView.findViewById(R.id.brandName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.brandLogo.setBackgroundResource((Integer) mData.get(position).get(
                "brandLogo"));
        holder.brandName.setText(mData.get(position).get("brandName").toString());
        return convertView;
    }

    public final class ViewHolder {
        public ImageView brandLogo;
        public TextView brandName;
    }
}
