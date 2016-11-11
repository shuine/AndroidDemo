package com.shine.demo.searchcity;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dingliang on 16-9-24.
 */
public class CityUtils {

    public final static int TYPE_CITY = 0;//0代表城市
    public final static int TYPE_DIVIDER = -1;//-１代表城市分割线

    public static class CityItem {
        public String sortLetters; // 显示数据拼音的首字母
        public String[] letters; //城市汉语拼音数组形式保存每一个字的拼音
        public String text;//城市
        public int index;//0代表城市　-１代表城市分割线
    }

    public static ArrayList<CityItem> sCityList = new ArrayList<CityItem>();
    public static HashMap<String, Integer> letterMap = new HashMap<>();
    /**
     * 获取城市列表
     *
     * @return
     */
    public static ArrayList<CityItem> getAllCityItems() {

        if (sCityList.size() > 1) {
            return sCityList;
        }
        sCityList.addAll(JSONArray.parseArray(getAllCities(), CityItem.class));
        CityItem item;
        for (int i = 0; i < sCityList.size(); i++) {
            item = sCityList.get(i);
            if (item.index == -1) {
                letterMap.put(item.sortLetters, i + 1);
            }
        }
        return sCityList;
    }

    /**
     * 热门城市
     *
     * @return
     */
    public static String[] getHotCities() {
        return new String[]{
                "北京", "上海", "广州", "深圳", "天津", "武汉"
        };
    }

    /**
     * 根据Key值搜索城市
     *　不支持中文和拼音混合搜索．第一个字符如果为中文则进行中文字符搜索，字符则根据汉语拼音搜索．
     * 搜索内容为第一个字符和后面连续相同字符类型的子字符串，如搜索"北jing"，则对"北"进行搜索
     * @param key
     * @return
     */
    public static List<String> getCityByKeyword(String key) {

        List<CityItem> arrayList;
        arrayList = getCityByKeyword(key, sCityList);
        List<String> vReuslt = new ArrayList<String>();
        if (arrayList != null && arrayList.size() > 0) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                vReuslt.add(arrayList.get(i).text);
            }
        }
        return vReuslt;
    }

    /**
     * 根据KEY值,在指定集合中搜索城市
     *
     * @param key
     * @param cityList
     * @return
     */
    public static List<CityItem> getCityByKeyword(String key, List<CityItem> cityList) {
        List<CityItem> vReuslt = new ArrayList<CityItem>();
        List<String> pattern = chinaSplite(key);
        String sub = pattern.get(0);
        CityItem item;
        if (!isChinese(sub.charAt(0))) {
            String first = sub.substring(0, 1).toUpperCase();
            if (!letterMap.containsKey(first)) {
                return vReuslt;
            }
            int start = letterMap.get(first);
            for (; start < cityList.size(); start++) {
                item = cityList.get(start);
                if (item.index == -1) {
                    break;
                }
                try {
                    if (isMatchPattern(sub, item.letters)) {
                        vReuslt.add(item);
                    }
                } catch (Exception err) {
                    String message = err.getMessage();
                    err.printStackTrace();
                }
            }
        } else {
            //汉子转化为拼音的包
            HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
            outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
            outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            try {
                String pinyin = PinyinHelper.toHanYuPinyinString(sub.substring(0, 1),
                        outputFormat, null, false);
                if (TextUtils.isEmpty(pinyin)) {
                    return vReuslt;
                }
                String first = pinyin.substring(0, 1).toUpperCase();
                if (!letterMap.containsKey(first)) {
                    return vReuslt;
                }
                int start = letterMap.get(first);
                for (; start < cityList.size(); start++) {
                    item = cityList.get(start);
                    if (item.index == -1) {
                        break;
                    }
                    if (item.text.contains(sub)) {
                        vReuslt.add(item);
                    }

                }
            } catch (BadHanyuPinyinOutputFormatCombination error) {

            }
        }
        return vReuslt;
    }

    /**
     * 判断字符sub是否属于container，搜索内容与城市汉语拼音匹配方法
     * bj  beijing true </br>
     * beij  beijing true </br>
     * bjing  beijing true </br>
     * bej  beijing false </br>
     * @param sub
     * @param container
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static boolean isMatchPattern(String sub, String[] container) throws IndexOutOfBoundsException {

        int j = 0;
        int i;
        int index = 0;
        String content = container[index];
        for (i = 0; i < sub.length(); i++) {
            if (j >= content.length()) {
                j = 0;
                index++;
                if (index >= container.length) {
                    return false;
                }
                content = container[index];
            }

            if (sub.charAt(i) == content.charAt(j)) {
                j++;
                continue;
            } else {
                if (j == 1) {
                    j = 0;
                    i--;
                    index++;
                    if (index >= container.length) {
                        return false;
                    } else {
                        content = container[index];
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 对搜索词按照中文和字符进行切割
     * @param sub
     * @return
     */
    public static List<String> chinaSplite(String sub) {
        ArrayList<String> result = new ArrayList<>();
        if (TextUtils.isEmpty(sub)) {
            return result;
        }
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+|[A-Za-z]+");
        Matcher m = p.matcher(sub);
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }

    /**
     * 判断是否为中文
     * @param c
     * @return
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 城市Json数据，读取直接转化为CityItem集合
     * @return
     */
    private static String getAllCities() {
        return "[{\"index\":-1,\"sortLetters\":\"A\",\"text\":\"A\"}," +
                "{\"index\":0,\"letters\":[\"a\",\"ba\",\"zang\",\"zu\",\"qiang\",\"zu\",\"zi\",\"zhi\",\"zhou\"]," +
                "\"sortLetters\":\"A\",\"text\":\"阿坝藏族羌族自治州\"}," +
                "{\"index\":0,\"letters\":[\"a\",\"ke\",\"su\",\"di\",\"qu\"],\"sortLetters\":\"A\",\"text\":\"阿克苏地区\"}," +
                "{\"index\":0,\"letters\":[\"a\",\"la\",\"shan\",\"meng\"],\"sortLetters\":\"A\",\"text\":\"阿拉善盟\"}," +
                "{\"index\":0,\"letters\":[\"a\",\"le\",\"tai\",\"di\",\"qu\"],\"sortLetters\":\"A\",\"text\":\"阿勒泰地区\"}," +
                "{\"index\":0,\"letters\":[\"a\",\"li\",\"di\",\"qu\"],\"sortLetters\":\"A\",\"text\":\"阿里地区\"}," +
                "{\"index\":0,\"letters\":[\"an\",\"kang\"],\"sortLetters\":\"A\",\"text\":\"安康\"},{\"index\":0,\"letters\":[\"an\",\"qing\"],\"sortLetters\":\"A\",\"text\":\"安庆\"}," +
                "{\"index\":0,\"letters\":[\"an\",\"shun\"],\"sortLetters\":\"A\",\"text\":\"安顺\"},{\"index\":0,\"letters\":[\"an\",\"yang\"],\"sortLetters\":\"A\",\"text\":\"安阳\"}," +
                "{\"index\":0,\"letters\":[\"an\",\"shan\"],\"sortLetters\":\"A\",\"text\":\"鞍山\"},{\"index\":-1,\"sortLetters\":\"B\",\"text\":\"B\"},{\"index\":0,\"letters\":[\"bei\",\"jing\"],\"sortLetters\":\"B\",\"text\":\"北京\"}," +
                "{\"index\":0,\"letters\":[\"ba\",\"yan\",\"nao\",\"er\"],\"sortLetters\":\"B\",\"text\":\"巴彦淖尔\"}," +
                "{\"index\":0,\"letters\":[\"ba\",\"yin\",\"guo\",\"leng\",\"meng\",\"gu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"B\",\"text\":\"巴音郭楞蒙古自治州\"}," +
                "{\"index\":0,\"letters\":[\"ba\",\"zhong\"],\"sortLetters\":\"B\",\"text\":\"巴中\"},{\"index\":0,\"letters\":[\"bai\",\"cheng\"],\"sortLetters\":\"B\",\"text\":\"白城\"}," +
                "{\"index\":0,\"letters\":[\"bai\",\"shan\"],\"sortLetters\":\"B\",\"text\":\"白山\"}" +
                ",{\"index\":0,\"letters\":[\"bai\",\"yin\"],\"sortLetters\":\"B\",\"text\":\"白银\"},{\"index\":0,\"letters\":[\"bai\",\"se\"],\"sortLetters\":\"B\",\"text\":\"百色\"}," +
                "{\"index\":0,\"letters\":[\"bang\",\"bu\"],\"sortLetters\":\"B\",\"text\":\"蚌埠\"},{\"index\":0,\"letters\":[\"bao\",\"tou\"],\"sortLetters\":\"B\",\"text\":\"包头\"}," +
                "{\"index\":0,\"letters\":[\"bao\",\"ji\"],\"sortLetters\":\"B\",\"text\":\"宝鸡\"},{\"index\":0,\"letters\":[\"bao\",\"ding\"],\"sortLetters\":\"B\",\"text\":\"保定\"}," +
                "{\"index\":0,\"letters\":[\"bao\",\"shan\"],\"sortLetters\":\"B\",\"text\":\"保山\"},{\"index\":0,\"letters\":[\"bei\",\"hai\"],\"sortLetters\":\"B\",\"text\":\"北海\"}," +
                "{\"index\":0,\"letters\":[\"ben\",\"xi\"],\"sortLetters\":\"B\",\"text\":\"本溪\"},{\"index\":0,\"letters\":[\"bi\",\"jie\",\"di\",\"qu\"],\"sortLetters\":\"B\",\"text\":\"毕节地区\"}," +
                "{\"index\":0,\"letters\":[\"bin\",\"zhou\"],\"sortLetters\":\"B\",\"text\":\"滨州\"},{\"index\":0,\"letters\":[\"bo\",\"er\",\"ta\",\"la\",\"meng\",\"gu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"B\",\"text\":\"博尔塔拉蒙古自治州\"}," +
                "{\"index\":-1,\"sortLetters\":\"C\",\"text\":\"C\"},{\"index\":0,\"letters\":[\"zhong\",\"qing\"],\"sortLetters\":\"C\",\"text\":\"重庆\"},{\"index\":0,\"letters\":[\"cang\",\"zhou\"],\"sortLetters\":\"C\",\"text\":\"沧州\"}," +
                "{\"index\":0,\"letters\":[\"chang\",\"dou\",\"di\",\"qu\"],\"sortLetters\":\"C\",\"text\":\"昌都地区\"},{\"index\":0,\"letters\":[\"chang\",\"ji\",\"hui\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"C\",\"text\":\"昌吉回族自治州\"}," +
                "{\"index\":0,\"letters\":[\"zhang\",\"chun\"],\"sortLetters\":\"C\",\"text\":\"长春\"},{\"index\":0,\"letters\":[\"zhang\",\"sha\"],\"sortLetters\":\"C\",\"text\":\"长沙\"},{\"index\":0,\"letters\":[\"zhang\",\"zhi\"],\"sortLetters\":\"C\",\"text\":\"长治\"}," +
                "{\"index\":0,\"letters\":[\"chang\",\"de\"],\"sortLetters\":\"C\",\"text\":\"常德\"}," +
                "{\"index\":0,\"letters\":[\"chang\",\"zhou\"],\"sortLetters\":\"C\",\"text\":\"常州\"},{\"index\":0,\"letters\":[\"chao\",\"hu\"],\"sortLetters\":\"C\",\"text\":\"巢湖\"},{\"index\":0,\"letters\":[\"chao\",\"yang\"],\"sortLetters\":\"C\",\"text\":\"朝阳\"},{\"index\":0,\"letters\":[\"chao\",\"zhou\"],\"sortLetters\":\"C\",\"text\":\"潮州\"}," +
                "{\"index\":0,\"letters\":[\"chen\",\"zhou\"],\"sortLetters\":\"C\",\"text\":\"郴州\"},{\"index\":0,\"letters\":[\"cheng\",\"dou\"],\"sortLetters\":\"C\",\"text\":\"成都\"}," +
                "{\"index\":0,\"letters\":[\"cheng\",\"de\"],\"sortLetters\":\"C\",\"text\":\"承德\"},{\"index\":0,\"letters\":[\"chi\",\"zhou\"],\"sortLetters\":\"C\",\"text\":\"池州\"},{\"index\":0,\"letters\":[\"chi\",\"feng\"],\"sortLetters\":\"C\",\"text\":\"赤峰\"},{\"index\":0,\"letters\":[\"chong\",\"zuo\"],\"sortLetters\":\"C\",\"text\":\"崇左\"},{\"index\":0,\"letters\":[\"chu\",\"zhou\"],\"sortLetters\":\"C\",\"text\":\"滁州\"}," +
                "{\"index\":0,\"letters\":[\"chu\",\"xiong\",\"yi\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"C\",\"text\":\"楚雄彝族自治州\"},{\"index\":-1,\"sortLetters\":\"D\",\"text\":\"D\"},{\"index\":0,\"letters\":[\"da\",\"zhou\"],\"sortLetters\":\"D\",\"text\":\"达州\"},{\"index\":0,\"letters\":[\"da\",\"li\",\"bai\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"D\",\"text\":\"大理白族自治州\"}," +
                "{\"index\":0,\"letters\":[\"da\",\"lian\"],\"sortLetters\":\"D\",\"text\":\"大连\"},{\"index\":0,\"letters\":[\"da\",\"qing\"],\"sortLetters\":\"D\",\"text\":\"大庆\"},{\"index\":0,\"letters\":[\"da\",\"tong\"],\"sortLetters\":\"D\",\"text\":\"大同\"},{\"index\":0,\"letters\":[\"da\",\"xing\",\"an\",\"ling\",\"di\",\"qu\"],\"sortLetters\":\"D\",\"text\":\"大兴安岭地区\"},{\"index\":0,\"letters\":[\"dan\",\"dong\"],\"sortLetters\":\"D\",\"text\":\"丹东\"}," +
                "{\"index\":0,\"letters\":[\"de\",\"hong\",\"dai\",\"zu\",\"jing\",\"po\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"D\",\"text\":\"德宏傣族景颇族自治州\"},{\"index\":0,\"letters\":[\"de\",\"yang\"],\"sortLetters\":\"D\",\"text\":\"德阳\"},{\"index\":0,\"letters\":[\"de\",\"zhou\"],\"sortLetters\":\"D\",\"text\":\"德州\"},{\"index\":0,\"letters\":[\"di\",\"qing\",\"zang\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"D\",\"text\":\"迪庆藏族自治州\"}," +
                "{\"index\":0,\"letters\":[\"ding\",\"xi\"],\"sortLetters\":\"D\",\"text\":\"定西\"},{\"index\":0,\"letters\":[\"dong\",\"guan\"],\"sortLetters\":\"D\",\"text\":\"东莞\"},{\"index\":0,\"letters\":[\"dong\",\"ying\"],\"sortLetters\":\"D\",\"text\":\"东营\"},{\"index\":-1,\"sortLetters\":\"E\",\"text\":\"E\"},{\"index\":0,\"letters\":[\"e\",\"er\",\"duo\",\"si\"],\"sortLetters\":\"E\",\"text\":\"鄂尔多斯\"}," +
                "{\"index\":0,\"letters\":[\"e\",\"zhou\"],\"sortLetters\":\"E\",\"text\":\"鄂州\"}," +
                "{\"index\":0,\"letters\":[\"en\",\"shi\",\"tu\",\"jia\",\"zu\",\"miao\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"E\",\"text\":\"恩施土家族苗族自治州\"},{\"index\":-1,\"sortLetters\":\"F\",\"text\":\"F\"},{\"index\":0,\"letters\":[\"fang\",\"cheng\",\"gang\"],\"sortLetters\":\"F\",\"text\":\"防城港\"}," +
                "{\"index\":0,\"letters\":[\"fo\",\"shan\"],\"sortLetters\":\"F\",\"text\":\"佛山\"},{\"index\":0,\"letters\":[\"fu\",\"zhou\"],\"sortLetters\":\"F\",\"text\":\"福州\"},{\"index\":0,\"letters\":[\"fu\",\"shun\"],\"sortLetters\":\"F\",\"text\":\"抚顺\"},{\"index\":0,\"letters\":[\"fu\",\"zhou\"],\"sortLetters\":\"F\",\"text\":\"抚州\"}," +
                "{\"index\":0,\"letters\":[\"fu\",\"xin\"],\"sortLetters\":\"F\",\"text\":\"阜新\"},{\"index\":0,\"letters\":[\"fu\",\"yang\"],\"sortLetters\":\"F\",\"text\":\"阜阳\"},{\"index\":-1,\"sortLetters\":\"G\",\"text\":\"G\"},{\"index\":0,\"letters\":[\"gan\",\"nan\",\"zhou\"],\"sortLetters\":\"G\",\"text\":\"甘南州\"},{\"index\":0,\"letters\":[\"gan\",\"zi\",\"zang\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"G\",\"text\":\"甘孜藏族自治州\"}," +
                "{\"index\":0,\"letters\":[\"gan\",\"zhou\"],\"sortLetters\":\"G\",\"text\":\"赣州\"},{\"index\":0,\"letters\":[\"gu\",\"yuan\"],\"sortLetters\":\"G\",\"text\":\"固原\"},{\"index\":0,\"letters\":[\"guang\",\"an\"],\"sortLetters\":\"G\",\"text\":\"广安\"},{\"index\":0,\"letters\":[\"guang\",\"yuan\"],\"sortLetters\":\"G\",\"text\":\"广元\"}," +
                "{\"index\":0,\"letters\":[\"guang\",\"zhou\"],\"sortLetters\":\"G\",\"text\":\"广州\"}," +
                "{\"index\":0,\"letters\":[\"gui\",\"gang\"],\"sortLetters\":\"G\",\"text\":\"贵港\"},{\"index\":0,\"letters\":[\"gui\",\"yang\"],\"sortLetters\":\"G\",\"text\":\"贵阳\"}," +
                "{\"index\":0,\"letters\":[\"gui\",\"lin\"],\"sortLetters\":\"G\",\"text\":\"桂林\"},{\"index\":0,\"letters\":[\"guo\",\"luo\",\"zang\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"G\",\"text\":\"果洛藏族自治州\"},{\"index\":-1,\"sortLetters\":\"H\",\"text\":\"H\"},{\"index\":0,\"letters\":[\"ha\",\"er\",\"bin\"],\"sortLetters\":\"H\",\"text\":\"哈尔滨\"},{\"index\":0,\"letters\":[\"ha\",\"mi\",\"di\",\"qu\"],\"sortLetters\":\"H\",\"text\":\"哈密地区\"},{\"index\":0,\"letters\":[\"hai\",\"bei\",\"zang\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"海北藏族自治州\"},{\"index\":0,\"letters\":[\"hai\",\"dong\",\"di\",\"qu\"],\"sortLetters\":\"H\",\"text\":\"海东地区\"},{\"index\":0,\"letters\":[\"hai\",\"kou\"],\"sortLetters\":\"H\",\"text\":\"海口\"},{\"index\":0,\"letters\":[\"hai\",\"nan\",\"zang\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"海南藏族自治州\"},{\"index\":0,\"letters\":[\"hai\",\"xi\",\"meng\",\"gu\",\"zu\",\"zang\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"海西蒙古族藏族自治州\"},{\"index\":0,\"letters\":[\"han\",\"dan\"],\"sortLetters\":\"H\",\"text\":\"邯郸\"},{\"index\":0,\"letters\":[\"han\",\"zhong\"],\"sortLetters\":\"H\",\"text\":\"汉中\"},{\"index\":0,\"letters\":[\"hang\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"杭州\"}," +
                "{\"index\":0,\"letters\":[\"hao\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"毫州\"},{\"index\":0,\"letters\":[\"he\",\"fei\"],\"sortLetters\":\"H\",\"text\":\"合肥\"},{\"index\":0,\"letters\":[\"he\",\"tian\",\"di\",\"qu\"],\"sortLetters\":\"H\",\"text\":\"和田地区\"},{\"index\":0,\"letters\":[\"he\",\"chi\"],\"sortLetters\":\"H\",\"text\":\"河池\"},{\"index\":0,\"letters\":[\"he\",\"yuan\"],\"sortLetters\":\"H\",\"text\":\"河源\"},{\"index\":0,\"letters\":[\"he\",\"ze\"],\"sortLetters\":\"H\",\"text\":\"菏泽\"},{\"index\":0,\"letters\":[\"he\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"贺州\"},{\"index\":0,\"letters\":[\"he\",\"bi\"],\"sortLetters\":\"H\",\"text\":\"鹤壁\"}," +
                "{\"index\":0,\"letters\":[\"he\",\"gang\"],\"sortLetters\":\"H\",\"text\":\"鹤岗\"},{\"index\":0,\"letters\":[\"hei\",\"he\"],\"sortLetters\":\"H\",\"text\":\"黑河\"},{\"index\":0,\"letters\":[\"heng\",\"shui\"],\"sortLetters\":\"H\",\"text\":\"衡水\"},{\"index\":0,\"letters\":[\"heng\",\"yang\"],\"sortLetters\":\"H\",\"text\":\"衡阳\"},{\"index\":0,\"letters\":[\"hong\",\"he\",\"ha\",\"ni\",\"zu\",\"yi\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"红河哈尼族彝族自治州\"},{\"index\":0,\"letters\":[\"hu\",\"he\",\"hao\",\"te\"],\"sortLetters\":\"H\",\"text\":\"呼和浩特\"},{\"index\":0,\"letters\":[\"hu\",\"lun\",\"bei\",\"er\"],\"sortLetters\":\"H\",\"text\":\"呼伦贝尔\"},{\"index\":0,\"letters\":[\"hu\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"湖州\"},{\"index\":0,\"letters\":[\"hu\",\"lu\",\"dao\"],\"sortLetters\":\"H\",\"text\":\"葫芦岛\"},{\"index\":0,\"letters\":[\"huai\",\"hua\"],\"sortLetters\":\"H\",\"text\":\"怀化\"},{\"index\":0,\"letters\":[\"huai\",\"an\"],\"sortLetters\":\"H\",\"text\":\"淮安\"},{\"index\":0,\"letters\":[\"huai\",\"bei\"],\"sortLetters\":\"H\",\"text\":\"淮北\"},{\"index\":0,\"letters\":[\"huai\",\"nan\"],\"sortLetters\":\"H\",\"text\":\"淮南\"},{\"index\":0,\"letters\":[\"huang\",\"gang\"],\"sortLetters\":\"H\",\"text\":\"黄冈\"},{\"index\":0,\"letters\":[\"huang\",\"nan\",\"zang\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"黄南藏族自治州\"},{\"index\":0,\"letters\":[\"huang\",\"shan\"],\"sortLetters\":\"H\",\"text\":\"黄山\"},{\"index\":0,\"letters\":[\"huang\",\"shi\"],\"sortLetters\":\"H\",\"text\":\"黄石\"},{\"index\":0,\"letters\":[\"hui\",\"zhou\"],\"sortLetters\":\"H\",\"text\":\"惠州\"},{\"index\":-1,\"sortLetters\":\"J\",\"text\":\"J\"},{\"index\":0,\"letters\":[\"ji\",\"xi\"],\"sortLetters\":\"J\",\"text\":\"鸡西\"},{\"index\":0,\"letters\":[\"ji\",\"an\"],\"sortLetters\":\"J\",\"text\":\"吉安\"},{\"index\":0,\"letters\":[\"ji\",\"lin\"],\"sortLetters\":\"J\",\"text\":\"吉林\"},{\"index\":0,\"letters\":[\"ji\",\"nan\"],\"sortLetters\":\"J\",\"text\":\"济南\"},{\"index\":0,\"letters\":[\"ji\",\"ning\"],\"sortLetters\":\"J\",\"text\":\"济宁\"},{\"index\":0,\"letters\":[\"jia\",\"mu\",\"si\"],\"sortLetters\":\"J\",\"text\":\"佳木斯\"},{\"index\":0,\"letters\":[\"jia\",\"xing\"],\"sortLetters\":\"J\",\"text\":\"嘉兴\"},{\"index\":0,\"letters\":[\"jia\",\"yu\",\"guan\"],\"sortLetters\":\"J\",\"text\":\"嘉峪关\"},{\"index\":0,\"letters\":[\"jiang\",\"men\"],\"sortLetters\":\"J\",\"text\":\"江门\"},{\"index\":0,\"letters\":[\"jiao\",\"zuo\"],\"sortLetters\":\"J\",\"text\":\"焦作\"},{\"index\":0,\"letters\":[\"jie\",\"yang\"],\"sortLetters\":\"J\",\"text\":\"揭阳\"},{\"index\":0,\"letters\":[\"jin\",\"chang\"],\"sortLetters\":\"J\",\"text\":\"金昌\"},{\"index\":0,\"letters\":[\"jin\",\"hua\"],\"sortLetters\":\"J\",\"text\":\"金华\"},{\"index\":0,\"letters\":[\"jin\",\"zhou\"],\"sortLetters\":\"J\",\"text\":\"锦州\"},{\"index\":0,\"letters\":[\"jin\",\"cheng\"],\"sortLetters\":\"J\",\"text\":\"晋城\"},{\"index\":0,\"letters\":[\"jin\",\"zhong\"],\"sortLetters\":\"J\",\"text\":\"晋中\"},{\"index\":0,\"letters\":[\"jing\",\"men\"],\"sortLetters\":\"J\",\"text\":\"荆门\"},{\"index\":0,\"letters\":[\"jing\",\"zhou\"],\"sortLetters\":\"J\",\"text\":\"荆州\"},{\"index\":0,\"letters\":[\"jing\",\"de\",\"zhen\"],\"sortLetters\":\"J\",\"text\":\"景德镇\"},{\"index\":0,\"letters\":[\"jiu\",\"jiang\"],\"sortLetters\":\"J\",\"text\":\"九江\"},{\"index\":0,\"letters\":[\"jiu\",\"quan\"],\"sortLetters\":\"J\",\"text\":\"酒泉\"},{\"index\":-1,\"sortLetters\":\"K\",\"text\":\"K\"},{\"index\":0,\"letters\":[\"ka\",\"shen\",\"di\",\"qu\"],\"sortLetters\":\"K\",\"text\":\"喀什地区\"},{\"index\":0,\"letters\":[\"kai\",\"feng\"],\"sortLetters\":\"K\",\"text\":\"开封\"},{\"index\":0,\"letters\":[\"ke\",\"la\",\"ma\",\"yi\"],\"sortLetters\":\"K\",\"text\":\"克拉玛依\"},{\"index\":0,\"letters\":[\"ke\",\"zi\",\"le\",\"su\",\"ke\",\"er\",\"ke\",\"zi\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"K\",\"text\":\"克孜勒苏柯尔克孜自治州\"},{\"index\":0,\"letters\":[\"kun\",\"ming\"],\"sortLetters\":\"K\",\"text\":\"昆明\"},{\"index\":-1,\"sortLetters\":\"L\",\"text\":\"L\"},{\"index\":0,\"letters\":[\"la\",\"sa\"],\"sortLetters\":\"L\",\"text\":\"拉萨\"},{\"index\":0,\"letters\":[\"lai\",\"bin\"],\"sortLetters\":\"L\",\"text\":\"来宾\"},{\"index\":0,\"letters\":[\"lai\",\"wu\"],\"sortLetters\":\"L\",\"text\":\"莱芜\"}," +
                "{\"index\":0,\"letters\":[\"lan\",\"zhou\"],\"sortLetters\":\"L\",\"text\":\"兰州\"},{\"index\":0,\"letters\":[\"lang\",\"fang\"],\"sortLetters\":\"L\",\"text\":\"廊坊\"},{\"index\":0,\"letters\":[\"le\",\"shan\"],\"sortLetters\":\"L\",\"text\":\"乐山\"},{\"index\":0,\"letters\":[\"li\",\"jiang\"],\"sortLetters\":\"L\",\"text\":\"丽江\"},{\"index\":0,\"letters\":[\"li\",\"shui\"],\"sortLetters\":\"L\",\"text\":\"丽水\"},{\"index\":0,\"letters\":[\"lian\",\"yun\",\"gang\"],\"sortLetters\":\"L\",\"text\":\"连云港\"},{\"index\":0,\"letters\":[\"liang\",\"shan\",\"yi\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"L\",\"text\":\"凉山彝族自治州\"},{\"index\":0,\"letters\":[\"liao\",\"yang\"],\"sortLetters\":\"L\",\"text\":\"辽阳\"},{\"index\":0,\"letters\":[\"liao\",\"yuan\"],\"sortLetters\":\"L\",\"text\":\"辽源\"},{\"index\":0,\"letters\":[\"liao\",\"cheng\"],\"sortLetters\":\"L\",\"text\":\"聊城\"},{\"index\":0,\"letters\":[\"lin\",\"zhi\",\"di\",\"qu\"],\"sortLetters\":\"L\",\"text\":\"林芝地区\"},{\"index\":0,\"letters\":[\"lin\",\"cang\"],\"sortLetters\":\"L\",\"text\":\"临沧\"},{\"index\":0,\"letters\":[\"lin\",\"fen\"],\"sortLetters\":\"L\",\"text\":\"临汾\"},{\"index\":0,\"letters\":[\"lin\",\"xia\",\"zhou\"],\"sortLetters\":\"L\",\"text\":\"临夏州\"},{\"index\":0,\"letters\":[\"lin\",\"yi\"],\"sortLetters\":\"L\",\"text\":\"临沂\"},{\"index\":0,\"letters\":[\"liu\",\"zhou\"],\"sortLetters\":\"L\",\"text\":\"柳州\"},{\"index\":0,\"letters\":[\"liu\",\"an\"],\"sortLetters\":\"L\",\"text\":\"六安\"},{\"index\":0,\"letters\":[\"liu\",\"pan\",\"shui\"],\"sortLetters\":\"L\",\"text\":\"六盘水\"},{\"index\":0,\"letters\":[\"long\",\"yan\"],\"sortLetters\":\"L\",\"text\":\"龙岩\"},{\"index\":0,\"letters\":[\"long\",\"nan\"],\"sortLetters\":\"L\",\"text\":\"陇南\"},{\"index\":0,\"letters\":[\"lou\",\"di\"],\"sortLetters\":\"L\",\"text\":\"娄底\"},{\"index\":0,\"letters\":[\"lu\",\"zhou\"],\"sortLetters\":\"L\",\"text\":\"泸州\"},{\"index\":0,\"letters\":[\"lv\",\"liang\"],\"sortLetters\":\"L\",\"text\":\"吕梁\"},{\"index\":0,\"letters\":[\"luo\",\"yang\"],\"sortLetters\":\"L\",\"text\":\"洛阳\"},{\"index\":0,\"letters\":[\"luo\",\"he\"],\"sortLetters\":\"L\",\"text\":\"漯河\"},{\"index\":-1,\"sortLetters\":\"M\",\"text\":\"M\"},{\"index\":0,\"letters\":[\"ma\",\"an\",\"shan\"],\"sortLetters\":\"M\",\"text\":\"马鞍山\"},{\"index\":0,\"letters\":[\"mao\",\"ming\"],\"sortLetters\":\"M\",\"text\":\"茂名\"},{\"index\":0,\"letters\":[\"mei\",\"shan\"],\"sortLetters\":\"M\",\"text\":\"眉山\"},{\"index\":0,\"letters\":[\"mei\",\"zhou\"],\"sortLetters\":\"M\",\"text\":\"梅州\"},{\"index\":0,\"letters\":[\"mian\",\"yang\"],\"sortLetters\":\"M\",\"text\":\"绵阳\"},{\"index\":0,\"letters\":[\"mu\",\"dan\",\"jiang\"],\"sortLetters\":\"M\",\"text\":\"牡丹江\"},{\"index\":-1,\"sortLetters\":\"N\",\"text\":\"N\"},{\"index\":0,\"letters\":[\"nei\",\"jiang\"],\"sortLetters\":\"N\",\"text\":\"内江\"},{\"index\":0,\"letters\":[\"nei\",\"qu\",\"di\",\"qu\"],\"sortLetters\":\"N\",\"text\":\"那曲地区\"},{\"index\":0,\"letters\":[\"nan\",\"chang\"],\"sortLetters\":\"N\",\"text\":\"南昌\"},{\"index\":0,\"letters\":[\"nan\",\"chong\"],\"sortLetters\":\"N\",\"text\":\"南充\"},{\"index\":0,\"letters\":[\"nan\",\"jing\"],\"sortLetters\":\"N\",\"text\":\"南京\"},{\"index\":0,\"letters\":[\"nan\",\"ning\"],\"sortLetters\":\"N\",\"text\":\"南宁\"},{\"index\":0,\"letters\":[\"nan\",\"ping\"],\"sortLetters\":\"N\",\"text\":\"南平\"},{\"index\":0,\"letters\":[\"nan\",\"tong\"],\"sortLetters\":\"N\",\"text\":\"南通\"},{\"index\":0,\"letters\":[\"nan\",\"yang\"],\"sortLetters\":\"N\",\"text\":\"南阳\"},{\"index\":0,\"letters\":[\"ning\",\"bo\"],\"sortLetters\":\"N\",\"text\":\"宁波\"},{\"index\":0,\"letters\":[\"ning\",\"de\"],\"sortLetters\":\"N\",\"text\":\"宁德\"},{\"index\":0,\"letters\":[\"nu\",\"jiang\",\"li\",\"su\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"N\",\"text\":\"怒江傈僳族自治州\"},{\"index\":-1,\"sortLetters\":\"P\",\"text\":\"P\"},{\"index\":0,\"letters\":[\"pan\",\"zhi\",\"hua\"],\"sortLetters\":\"P\",\"text\":\"攀枝花\"},{\"index\":0,\"letters\":[\"pan\",\"jin\"],\"sortLetters\":\"P\",\"text\":\"盘锦\"},{\"index\":0,\"letters\":[\"ping\",\"ding\",\"shan\"],\"sortLetters\":\"P\",\"text\":\"平顶山\"},{\"index\":0,\"letters\":[\"ping\",\"liang\"],\"sortLetters\":\"P\",\"text\":\"平凉\"},{\"index\":0,\"letters\":[\"ping\",\"xiang\"],\"sortLetters\":\"P\",\"text\":\"萍乡\"}," +
                "{\"index\":0,\"letters\":[\"pu\",\"tian\"],\"sortLetters\":\"P\",\"text\":\"莆田\"},{\"index\":0,\"letters\":[\"pu\",\"yang\"],\"sortLetters\":\"P\",\"text\":\"濮阳\"},{\"index\":0,\"letters\":[\"pu\",\"er\"],\"sortLetters\":\"P\",\"text\":\"普洱\"},{\"index\":-1,\"sortLetters\":\"Q\",\"text\":\"Q\"},{\"index\":0,\"letters\":[\"qi\",\"tai\",\"he\"],\"sortLetters\":\"Q\",\"text\":\"七台河\"},{\"index\":0,\"letters\":[\"qi\",\"qi\",\"ha\",\"er\"],\"sortLetters\":\"Q\",\"text\":\"齐齐哈尔\"},{\"index\":0,\"letters\":[\"qian\",\"dong\",\"nan\",\"miao\",\"zu\",\"dong\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"Q\",\"text\":\"黔东南苗族侗族自治州\"}," +
                "{\"index\":0,\"letters\":[\"qian\",\"nan\",\"bu\",\"yi\",\"zu\",\"miao\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"Q\",\"text\":\"黔南布依族苗族自治州\"},{\"index\":0,\"letters\":[\"qian\",\"xi\",\"nan\",\"bu\",\"yi\",\"zu\",\"miao\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"Q\",\"text\":\"黔西南布依族苗族自治州\"}," +
                "{\"index\":0,\"letters\":[\"qin\",\"zhou\"],\"sortLetters\":\"Q\",\"text\":\"钦州\"},{\"index\":0,\"letters\":[\"qin\",\"huang\",\"dao\"],\"sortLetters\":\"Q\",\"text\":\"秦皇岛\"},{\"index\":0,\"letters\":[\"qing\",\"dao\"],\"sortLetters\":\"Q\",\"text\":\"青岛\"},{\"index\":0,\"letters\":[\"qing\",\"yuan\"],\"sortLetters\":\"Q\",\"text\":\"清远\"},{\"index\":0,\"letters\":[\"qing\",\"yang\"],\"sortLetters\":\"Q\",\"text\":\"庆阳\"},{\"index\":0,\"letters\":[\"qu\",\"jing\"],\"sortLetters\":\"Q\",\"text\":\"曲靖\"},{\"index\":0,\"letters\":[\"qu\",\"zhou\"],\"sortLetters\":\"Q\",\"text\":\"衢州\"},{\"index\":0,\"letters\":[\"quan\",\"zhou\"],\"sortLetters\":\"Q\",\"text\":\"泉州\"},{\"index\":-1,\"sortLetters\":\"R\",\"text\":\"R\"},{\"index\":0,\"letters\":[\"ri\",\"zhao\"],\"sortLetters\":\"R\",\"text\":\"日照\"},{\"index\":0,\"letters\":[\"ri\",\"ka\",\"ze\",\"di\",\"qu\"],\"sortLetters\":\"R\",\"text\":\"日喀则地区\"},{\"index\":-1,\"sortLetters\":\"S\",\"text\":\"S\"},{\"index\":0,\"letters\":[\"shang\",\"hai\"],\"sortLetters\":\"S\",\"text\":\"上海\"},{\"index\":0,\"letters\":[\"san\",\"men\",\"xia\"],\"sortLetters\":\"S\",\"text\":\"三门峡\"},{\"index\":0,\"letters\":[\"san\",\"ming\"],\"sortLetters\":\"S\",\"text\":\"三明\"},{\"index\":0,\"letters\":[\"san\",\"ya\"],\"sortLetters\":\"S\",\"text\":\"三亚\"},{\"index\":0,\"letters\":[\"shan\",\"nan\",\"di\",\"qu\"],\"sortLetters\":\"S\",\"text\":\"山南地区\"},{\"index\":0,\"letters\":[\"shan\",\"tou\"],\"sortLetters\":\"S\",\"text\":\"汕头\"},{\"index\":0,\"letters\":[\"shan\",\"wei\"],\"sortLetters\":\"S\",\"text\":\"汕尾\"},{\"index\":0,\"letters\":[\"shang\",\"luo\"],\"sortLetters\":\"S\",\"text\":\"商洛\"},{\"index\":0,\"letters\":[\"shang\",\"qiu\"],\"sortLetters\":\"S\",\"text\":\"商丘\"},{\"index\":0,\"letters\":[\"shang\",\"rao\"],\"sortLetters\":\"S\",\"text\":\"上饶\"},{\"index\":0,\"letters\":[\"shao\",\"guan\"],\"sortLetters\":\"S\",\"text\":\"韶关\"},{\"index\":0,\"letters\":[\"shao\",\"yang\"],\"sortLetters\":\"S\",\"text\":\"邵阳\"},{\"index\":0,\"letters\":[\"shao\",\"xing\"],\"sortLetters\":\"S\",\"text\":\"绍兴\"},{\"index\":0,\"letters\":[\"shen\",\"zhen\"],\"sortLetters\":\"S\",\"text\":\"深圳\"},{\"index\":0,\"letters\":[\"shen\",\"yang\"],\"sortLetters\":\"S\",\"text\":\"沈阳\"},{\"index\":0,\"letters\":[\"shi\",\"yan\"],\"sortLetters\":\"S\",\"text\":\"十堰\"},{\"index\":0,\"letters\":[\"shi\",\"jia\",\"zhuang\"],\"sortLetters\":\"S\",\"text\":\"石家庄\"},{\"index\":0,\"letters\":[\"shi\",\"zui\",\"shan\"],\"sortLetters\":\"S\",\"text\":\"石嘴山\"},{\"index\":0,\"letters\":[\"shuang\",\"ya\",\"shan\"],\"sortLetters\":\"S\",\"text\":\"双鸭山\"},{\"index\":0,\"letters\":[\"shuo\",\"zhou\"],\"sortLetters\":\"S\",\"text\":\"朔州\"},{\"index\":0,\"letters\":[\"si\",\"ping\"],\"sortLetters\":\"S\",\"text\":\"四平\"},{\"index\":0,\"letters\":[\"song\",\"yuan\"],\"sortLetters\":\"S\",\"text\":\"松原\"},{\"index\":0,\"letters\":[\"su\",\"zhou\"],\"sortLetters\":\"S\",\"text\":\"苏州\"},{\"index\":0,\"letters\":[\"su\",\"qian\"],\"sortLetters\":\"S\",\"text\":\"宿迁\"},{\"index\":0,\"letters\":[\"su\",\"zhou\"],\"sortLetters\":\"S\",\"text\":\"宿州\"},{\"index\":0,\"letters\":[\"sui\",\"hua\"],\"sortLetters\":\"S\",\"text\":\"绥化\"},{\"index\":0,\"letters\":[\"sui\",\"zhou\"],\"sortLetters\":\"S\",\"text\":\"随州\"},{\"index\":0,\"letters\":[\"sui\",\"ning\"],\"sortLetters\":\"S\",\"text\":\"遂宁\"},{\"index\":-1,\"sortLetters\":\"T\",\"text\":\"T\"},{\"index\":0,\"letters\":[\"tian\",\"jin\"],\"sortLetters\":\"T\",\"text\":\"天津\"},{\"index\":0,\"letters\":[\"ta\",\"cheng\",\"di\",\"qu\"],\"sortLetters\":\"T\",\"text\":\"塔城地区\"},{\"index\":0,\"letters\":[\"tai\",\"zhou\"],\"sortLetters\":\"T\",\"text\":\"台州\"},{\"index\":0,\"letters\":[\"tai\",\"yuan\"],\"sortLetters\":\"T\",\"text\":\"太原\"},{\"index\":0,\"letters\":[\"tai\",\"an\"],\"sortLetters\":\"T\",\"text\":\"泰安\"},{\"index\":0,\"letters\":[\"tai\",\"zhou\"],\"sortLetters\":\"T\",\"text\":\"泰州\"},{\"index\":0,\"letters\":[\"tang\",\"shan\"],\"sortLetters\":\"T\",\"text\":\"唐山\"},{\"index\":0,\"letters\":[\"tian\",\"shui\"],\"sortLetters\":\"T\",\"text\":\"天水\"},{\"index\":0,\"letters\":[\"tie\",\"ling\"],\"sortLetters\":\"T\",\"text\":\"铁岭\"},{\"index\":0,\"letters\":[\"tong\",\"hua\"],\"sortLetters\":\"T\",\"text\":\"通化\"},{\"index\":0,\"letters\":[\"tong\",\"liao\"],\"sortLetters\":\"T\",\"text\":\"通辽\"},{\"index\":0,\"letters\":[\"tong\",\"chuan\"],\"sortLetters\":\"T\",\"text\":\"铜川\"},{\"index\":0,\"letters\":[\"tong\",\"ling\"],\"sortLetters\":\"T\",\"text\":\"铜陵\"},{\"index\":0,\"letters\":[\"tong\",\"ren\"],\"sortLetters\":\"T\",\"text\":\"铜仁\"},{\"index\":0,\"letters\":[\"tu\",\"lu\",\"fan\",\"di\",\"qu\"],\"sortLetters\":\"T\",\"text\":\"吐鲁番地区\"}," +
                "{\"index\":-1,\"sortLetters\":\"W\",\"text\":\"W\"},{\"index\":0,\"letters\":[\"wei\",\"hai\"],\"sortLetters\":\"W\",\"text\":\"威海\"},{\"index\":0,\"letters\":[\"wei\",\"fang\"],\"sortLetters\":\"W\",\"text\":\"潍坊\"},{\"index\":0,\"letters\":[\"wei\",\"nan\"],\"sortLetters\":\"W\",\"text\":\"渭南\"},{\"index\":0,\"letters\":[\"wen\",\"zhou\"],\"sortLetters\":\"W\",\"text\":\"温州\"},{\"index\":0,\"letters\":[\"wen\",\"shan\",\"zhuang\",\"zu\",\"miao\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"W\",\"text\":\"文山壮族苗族自治州\"},{\"index\":0,\"letters\":[\"wu\",\"hai\"],\"sortLetters\":\"W\",\"text\":\"乌海\"},{\"index\":0,\"letters\":[\"wu\",\"lan\",\"cha\",\"bu\"],\"sortLetters\":\"W\",\"text\":\"乌兰察布\"},{\"index\":0,\"letters\":[\"wu\",\"lu\",\"mu\",\"qi\"],\"sortLetters\":\"W\",\"text\":\"乌鲁木齐\"},{\"index\":0,\"letters\":[\"wu\",\"xi\"],\"sortLetters\":\"W\",\"text\":\"无锡\"},{\"index\":0,\"letters\":[\"wu\",\"zhong\"],\"sortLetters\":\"W\",\"text\":\"吴忠\"},{\"index\":0,\"letters\":[\"wu\",\"hu\"],\"sortLetters\":\"W\",\"text\":\"芜湖\"},{\"index\":0,\"letters\":[\"wu\",\"zhou\"],\"sortLetters\":\"W\",\"text\":\"梧州\"},{\"index\":0,\"letters\":[\"wu\",\"han\"],\"sortLetters\":\"W\",\"text\":\"武汉\"},{\"index\":0,\"letters\":[\"wu\",\"wei\"],\"sortLetters\":\"W\",\"text\":\"武威\"},{\"index\":-1,\"sortLetters\":\"X\",\"text\":\"X\"},{\"index\":0,\"letters\":[\"xi\",\"an\"],\"sortLetters\":\"X\",\"text\":\"西安\"},{\"index\":0,\"letters\":[\"xi\",\"ning\"],\"sortLetters\":\"X\",\"text\":\"西宁\"},{\"index\":0,\"letters\":[\"xi\",\"shuang\",\"ban\",\"na\",\"dai\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"X\",\"text\":\"西双版纳傣族自治州\"},{\"index\":0,\"letters\":[\"xi\",\"lin\",\"guo\",\"le\",\"meng\"],\"sortLetters\":\"X\",\"text\":\"锡林郭勒盟\"},{\"index\":0,\"letters\":[\"sha\",\"men\"],\"sortLetters\":\"X\",\"text\":\"厦门\"},{\"index\":0,\"letters\":[\"xian\",\"ning\"],\"sortLetters\":\"X\",\"text\":\"咸宁\"},{\"index\":0,\"letters\":[\"xian\",\"yang\"],\"sortLetters\":\"X\",\"text\":\"咸阳\"},{\"index\":0,\"letters\":[\"xiang\",\"tan\"],\"sortLetters\":\"X\",\"text\":\"湘潭\"},{\"index\":0,\"letters\":[\"xiang\",\"xi\",\"tu\",\"jia\",\"zu\",\"miao\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"X\",\"text\":\"湘西土家族苗族自治州\"},{\"index\":0,\"letters\":[\"xiang\",\"fan\"],\"sortLetters\":\"X\",\"text\":\"襄樊\"},{\"index\":0,\"letters\":[\"xiao\",\"gan\"],\"sortLetters\":\"X\",\"text\":\"孝感\"},{\"index\":0,\"letters\":[\"xin\",\"zhou\"],\"sortLetters\":\"X\",\"text\":\"忻州\"},{\"index\":0,\"letters\":[\"xin\",\"xiang\"],\"sortLetters\":\"X\",\"text\":\"新乡\"}," +
                "{\"index\":0,\"letters\":[\"xin\",\"yu\"],\"sortLetters\":\"X\",\"text\":\"新余\"},{\"index\":0,\"letters\":[\"xin\",\"yang\"],\"sortLetters\":\"X\",\"text\":\"信阳\"},{\"index\":0,\"letters\":[\"xing\",\"an\",\"meng\"],\"sortLetters\":\"X\",\"text\":\"兴安盟\"},{\"index\":0,\"letters\":[\"xing\",\"tai\"],\"sortLetters\":\"X\",\"text\":\"邢台\"},{\"index\":0,\"letters\":[\"xu\",\"zhou\"],\"sortLetters\":\"X\",\"text\":\"徐州\"},{\"index\":0,\"letters\":[\"xu\",\"chang\"],\"sortLetters\":\"X\",\"text\":\"许昌\"},{\"index\":-1,\"sortLetters\":\"Y\",\"text\":\"Y\"},{\"index\":0,\"letters\":[\"xuan\",\"cheng\"],\"sortLetters\":\"Y\",\"text\":\"宣城\"},{\"index\":0,\"letters\":[\"ya\",\"an\"],\"sortLetters\":\"Y\",\"text\":\"雅安\"},{\"index\":0,\"letters\":[\"yan\",\"tai\"],\"sortLetters\":\"Y\",\"text\":\"烟台\"},{\"index\":0,\"letters\":[\"yan\",\"an\"],\"sortLetters\":\"Y\",\"text\":\"延安\"},{\"index\":0,\"letters\":[\"yan\",\"bian\",\"chao\",\"xian\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"Y\",\"text\":\"延边朝鲜族自治州\"},{\"index\":0,\"letters\":[\"yan\",\"cheng\"],\"sortLetters\":\"Y\",\"text\":\"盐城\"},{\"index\":0,\"letters\":[\"yang\",\"zhou\"],\"sortLetters\":\"Y\",\"text\":\"扬州\"},{\"index\":0,\"letters\":[\"yang\",\"jiang\"],\"sortLetters\":\"Y\",\"text\":\"阳江\"},{\"index\":0,\"letters\":[\"yang\",\"quan\"],\"sortLetters\":\"Y\",\"text\":\"阳泉\"},{\"index\":0,\"letters\":[\"yi\",\"chun\"],\"sortLetters\":\"Y\",\"text\":\"伊春\"},{\"index\":0,\"letters\":[\"yi\",\"li\",\"ha\",\"sa\",\"ke\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"Y\",\"text\":\"伊犁哈萨克自治州\"},{\"index\":0,\"letters\":[\"yi\",\"bin\"],\"sortLetters\":\"Y\",\"text\":\"宜宾\"},{\"index\":0,\"letters\":[\"yi\",\"chang\"],\"sortLetters\":\"Y\",\"text\":\"宜昌\"},{\"index\":0,\"letters\":[\"yi\",\"chun\"],\"sortLetters\":\"Y\",\"text\":\"宜春\"},{\"index\":0,\"letters\":[\"yi\",\"yang\"],\"sortLetters\":\"Y\",\"text\":\"益阳\"},{\"index\":0,\"letters\":[\"yin\",\"chuan\"],\"sortLetters\":\"Y\",\"text\":\"银川\"},{\"index\":0,\"letters\":[\"ying\",\"tan\"],\"sortLetters\":\"Y\",\"text\":\"鹰潭\"},{\"index\":0,\"letters\":[\"ying\",\"kou\"],\"sortLetters\":\"Y\",\"text\":\"营口\"},{\"index\":0,\"letters\":[\"yong\",\"zhou\"],\"sortLetters\":\"Y\",\"text\":\"永州\"},{\"index\":0,\"letters\":[\"yu\",\"lin\"],\"sortLetters\":\"Y\",\"text\":\"榆林\"},{\"index\":0,\"letters\":[\"yu\",\"lin\"],\"sortLetters\":\"Y\",\"text\":\"玉林\"},{\"index\":0,\"letters\":[\"yu\",\"shu\",\"zang\",\"zu\",\"zi\",\"zhi\",\"zhou\"],\"sortLetters\":\"Y\",\"text\":\"玉树藏族自治州\"},{\"index\":0,\"letters\":[\"yu\",\"xi\"],\"sortLetters\":\"Y\",\"text\":\"玉溪\"},{\"index\":0,\"letters\":[\"yue\",\"yang\"],\"sortLetters\":\"Y\",\"text\":\"岳阳\"},{\"index\":0,\"letters\":[\"yun\",\"fu\"],\"sortLetters\":\"Y\",\"text\":\"云浮\"},{\"index\":0,\"letters\":[\"yun\",\"cheng\"],\"sortLetters\":\"Y\",\"text\":\"运城\"},{\"index\":-1,\"sortLetters\":\"Z\",\"text\":\"Z\"},{\"index\":0,\"letters\":[\"zao\",\"zhuang\"],\"sortLetters\":\"Z\",\"text\":\"枣庄\"},{\"index\":0,\"letters\":[\"zhan\",\"jiang\"],\"sortLetters\":\"Z\",\"text\":\"湛江\"},{\"index\":0,\"letters\":[\"zhang\",\"jia\",\"jie\"],\"sortLetters\":\"Z\",\"text\":\"张家界\"},{\"index\":0,\"letters\":[\"zhang\",\"jia\",\"kou\"],\"sortLetters\":\"Z\",\"text\":\"张家口\"},{\"index\":0,\"letters\":[\"zhang\",\"ye\"],\"sortLetters\":\"Z\",\"text\":\"张掖\"},{\"index\":0,\"letters\":[\"zhang\",\"zhou\"],\"sortLetters\":\"Z\",\"text\":\"漳州\"},{\"index\":0,\"letters\":[\"zhao\",\"tong\"],\"sortLetters\":\"Z\",\"text\":\"昭通\"},{\"index\":0,\"letters\":[\"zhao\",\"qing\"],\"sortLetters\":\"Z\",\"text\":\"肇庆\"},{\"index\":0,\"letters\":[\"zhen\",\"jiang\"],\"sortLetters\":\"Z\",\"text\":\"镇江\"},{\"index\":0,\"letters\":[\"zheng\",\"zhou\"],\"sortLetters\":\"Z\",\"text\":\"郑州\"},{\"index\":0,\"letters\":[\"zhong\",\"shan\"],\"sortLetters\":\"Z\",\"text\":\"中山\"},{\"index\":0,\"letters\":[\"zhong\",\"wei\"],\"sortLetters\":\"Z\",\"text\":\"中卫\"},{\"index\":0,\"letters\":[\"zhou\",\"shan\"],\"sortLetters\":\"Z\",\"text\":\"舟山\"},{\"index\":0,\"letters\":[\"zhou\",\"kou\"],\"sortLetters\":\"Z\",\"text\":\"周口\"},{\"index\":0,\"letters\":[\"zhu\",\"zhou\"],\"sortLetters\":\"Z\",\"text\":\"株洲\"},{\"index\":0,\"letters\":[\"zhu\",\"hai\"],\"sortLetters\":\"Z\",\"text\":\"珠海\"},{\"index\":0,\"letters\":[\"zhu\",\"ma\",\"dian\"],\"sortLetters\":\"Z\",\"text\":\"驻马店\"},{\"index\":0,\"letters\":[\"zi\",\"yang\"],\"sortLetters\":\"Z\",\"text\":\"资阳\"},{\"index\":0,\"letters\":[\"zi\",\"bo\"],\"sortLetters\":\"Z\",\"text\":\"淄博\"},{\"index\":0,\"letters\":[\"zi\",\"gong\"],\"sortLetters\":\"Z\",\"text\":\"自贡\"},{\"index\":0,\"letters\":[\"zun\",\"yi\"],\"sortLetters\":\"Z\",\"text\":\"遵义\"}]";
    }
}
