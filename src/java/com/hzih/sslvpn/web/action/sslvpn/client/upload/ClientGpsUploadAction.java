package com.hzih.sslvpn.web.action.sslvpn.client.upload;

import com.hzih.sslvpn.dao.UserDao;
import com.hzih.sslvpn.dao.UserGpsDao;
import com.hzih.sslvpn.domain.User;
import com.hzih.sslvpn.domain.UserGps;
import com.opensymphony.xwork2.ActionSupport;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by hhm on 2014/12/16.
 */
public class ClientGpsUploadAction extends ActionSupport {

    private Logger logger = Logger.getLogger(ClientGpsUploadAction.class);

    private UserDao userDao;

    private UserGpsDao userGpsDao;

    public UserGpsDao getUserGpsDao() {
        return userGpsDao;
    }

    public void setUserGpsDao(UserGpsDao userGpsDao) {
        this.userGpsDao = userGpsDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public String uploadGps() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String msg = "保存GPS信息失败";
        String json = "{success:false,msg:'" + msg + "'}";
        String data = request.getParameter("data");
        if (data != null && data.length() > 0) {
            JSONObject jsonObject = JSONObject.fromObject(data);
            String cn = jsonObject.get("cn").toString();
            String datetime = jsonObject.get("datetime").toString();
            try {
                User user = userDao.findByCommonName(cn);
                if(user!=null) {
                    JSONObject gps_obj = jsonObject.getJSONObject("gps");
                    if(gps_obj!=null) {
                        String longitude = gps_obj.get("longitude").toString();
                        String latitude = gps_obj.get("latitude").toString();
                        UserGps userGps = new UserGps();
                        userGps.setLatitude(latitude);
                        userGps.setUser(user);
                        userGps.setLongitude(longitude);
                        userGps.setInsertTime(new Date());
                        userGps.setReadTime(datetime);
                        userGpsDao.add(userGps);
                        msg = "保存GPS信息成功,时间:" + datetime + ",经度:" + longitude + ",纬度:" + latitude;
                        logger.info("客户端通用名:"+cn+",信息:"+msg);
                        json = "{\"success\":true,\"msg\":'" + msg + "'}";
                        writer.write(json);
                        writer.flush();
                        writer.close();
                    }
                }else {
                    msg = "服务器未找到对应用户:" ;
                    json = "{\"success\":false,\"msg\":'" + msg + "'}";
                    logger.info("客户端通用名:"+cn+",信息:"+msg+",地址："+request.getRemoteAddr());
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writer.write(json);
        writer.flush();
        writer.close();
        /**
         * gps信息数据格式
         *证书名称
         *身份证
         * 省
         * 市
         * 组织
         * 机构
         * 电话
         * 地址
         * 邮件
         String gps_msg = "{'cn':'','idCard':'','province':'','city':'city','organization':'','institution':'','phone':'','address':'','email':'','gps':{'longitude':'','latitude':''},'datetime':''}";
         */
        return null;
    }

    public static void main(String args[]) throws Exception {
//        String ss = "{'cn':'cnxx','idCard':'idxxx','gps':{'longitude':'longxx','latitude':'latituxx'},'datetime':'datxxx'}";
        String sl = "{'cn':'cnxx','idCard':'idxxx','userlists':[{'username':'username1','terminal':{'terminalid':'tid1','terminalname':'tname1'}},{'username':'u2','terminal':{'terminalid':'tid2','terminalname':'tname2'}}],'datetime':'dxxx'}";
        JSONObject jsonObject = JSONObject.fromObject(sl);
        JSONArray gpsobj = jsonObject.getJSONArray("userlists");
        JSONObject jsonObject1 = gpsobj.getJSONObject(0);
        JSONObject jsonObject2 = jsonObject1.getJSONObject("terminal");
//        JSONObject jsonObject = JSONUtils.toJSONObject(ss);
        System.out.print(jsonObject2.get("terminalid").toString());
    }

}
