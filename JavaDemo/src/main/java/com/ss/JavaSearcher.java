package com.ss;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JavaSearcher {

    public static void main(String[] args) {
        Map<String,User> s=searchByName("D:\\doc\\2020网络春晚\\latest\\data",
                //"陈瑶", "宋秀云","陈丽霞","马小亚","周浪","贤娟","邱月","姜运月"
                //"苏云明","苏晓攀","宋晓霞","宋晓芬",""
                //"18685138148","18286239900","15885618028","18885236838","17385527992","13595172020","15519596099"
                //"13312451779"
                //"13595227918","15085172671","15985268077","13984159356"
                //"18885236838"
                //"13595227918"
                //"15885618028","18286239900","18885236838","17385527992","13595172020","15519596099","18685138148","18786192186"
                "13793088684","15810134741"
        );
        Iterator<Map.Entry<String,User>> iterator=s.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,User> entry=iterator.next();
            User user=entry.getValue();
            StringBuilder sb=new StringBuilder();
            //sb.append("姓名：").append(user.name).append("\n")
              sb.append("电话：").append(user.phone).append("\n")
                    //.append("地址：").append(user.address).append("\n")
                    .append("礼品列表("+user.gifts.size()+")：").append("\n");
            for (String g:user.gifts
                 ) {
                sb.append(g).append(";\n");
            }
            ;

            System.out.println(sb.toString());

        }
    }

    public static Map<String,User> searchByName(String dir, String... phones){
        Map<String,User> map=new HashMap<>();
        //ArrayList<User> list=new ArrayList<>();
        File file=new File(dir);
        File[] files = file.listFiles();
        for (File f:files
             ) {
            try {
                try {
                    FileReader fr = new FileReader(f);
                    BufferedReader bf = new BufferedReader(fr);
                    String str;
                    // 按行读取字符串
                    while ((str = bf.readLine()) != null) {
                        String sp="|";
                        String t="None";

                        if (str!=null && !str.equals("") && str.contains(sp)){
                            String[] ele=str.split("\\|");
                            String cname=ele[0];
                            String phone=ele[1];
                            String address=ele[2];
                            List<String> ps= Arrays.asList(phones);
                            if (ps.indexOf(phone)!=-1){
                                String fname=getFileNameNoExtension(f.getAbsolutePath());
                                //System.out.println(fname);
                                String[] ff=fname.split("-");
                                int x=Integer.parseInt(ff[0]);
                                int y=Integer.parseInt(ff[1]);
                                String gift=gs[x][y];

                                User user=map.get(phone);
                                if (user==null){
                                    user=new User();
                                }
                                user.name=cname;
                                user.address=address;
                                user.phone=phone;
                                List<String> list=user.gifts;
                                list.add(gift);
                                user.gifts=list;
                                map.put(phone,user);
                                //list.add(user);
                            }
                        }
                    }
                    bf.close();
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }

    public static class User{
        String name = "";
        String phone ="";
        String address ="";
        List<String> gifts=new ArrayList<>();

    }

    public static String getFileNameNoExtension(final String filePath) {
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    protected static String[][] gs={{"老干妈全家福礼品盒","欣扬农业NFC刺梨果汁","长顺县顺智绿壳鸡蛋","“皇金苔”古树红茶","松桃福农鑫三山谷茶油","嘉禾香农业紫云红薯","鸿源生态农业红心猕猴桃","贵茶中国红礼盒","梵真坊口服山茶油","茅台（集团）生态蓝莓精酿酒","茅台（集团）生态蓝莓果汁"},
            {"贵茶中国红礼盒","黔金果有机山茶油","中康农业“猕天大圣”猕猴桃","哆吉栗小王子手提袋","贵阳“山地贵爽”黔菜集礼盒","贵三红辣都四鲜","宏财刺力王精致刺梨原液","龙大哥辣子鸡","茅台（集团）生态蓝莓精酿酒","茅台（集团）生态蓝莓果汁"},
            {"宏财刺力王刺梨原液","梵真坊口服山茶油","正山堂普安红黔小茶","贵州省生态渔业公司花鲢","乌蒙腾菌业天荪荟膳食汤包","金源泰达百香果果干","长顺县顺智绿壳鸡蛋","茅台（集团）生态蓝莓精酿酒","茅台（集团）生态蓝莓果汁"},
            {"宏财刺力王刺梨饮料","遵义茶业集团遵义红茶叶","黔金果有机山茶油","陈二平油辣椒礼盒","遵义供销电商糊辣椒面","思南县青杠坝幸福食品冰糖大蒜","鸿源生态农业红心猕猴桃","金源泰达百香果果干","“皇金苔”古树红茶","正山堂普安红黔小茶","茅台（集团）生态蓝莓精酿酒","茅台（集团）生态蓝莓果汁"},
            {"欣扬农业NFC刺梨果汁","宏财刺力王刺梨饮料","开阳富硒生态白茶","水城红心猕猴桃汁","册亨县贵兴农业糯米蕉","贞丰一品手工红糖，花糯米饭","贞丰一品灰粽","遵义供销电商香辣牛肉酱","“皇金苔”古树红茶","茅台（集团）生态蓝莓精酿酒","茅台（集团）生态蓝莓果汁","御知康山茶果精华油"}
    };

}
