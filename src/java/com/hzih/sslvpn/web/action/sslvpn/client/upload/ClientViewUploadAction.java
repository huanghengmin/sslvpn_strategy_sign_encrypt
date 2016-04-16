package com.hzih.sslvpn.web.action.sslvpn.client.upload;

import com.hzih.sslvpn.dao.UserDao;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Administrator on 15-5-26.
 */
public class ClientViewUploadAction extends ActionSupport{
    private Logger logger = Logger.getLogger(getClass());

    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public String uploadView() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String serial = request.getParameter("serial");
        String json = "{\"success\":false}";
        if(serial!=null){
            String toSrc = ServletActionContext.getServletContext().getRealPath("/upload/"+serial);
            File dir = new File(toSrc);
            File file_file=null;
            long max = 0;
            String type = "jpg";
            File file[] = dir.listFiles();
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile()){
                    String name = file[i].getName();
                    if(name.contains(".")){
                        String num = name.substring(0,name.lastIndexOf("."));
                        max = Long.parseLong(num);
                        file_file = file[i];
                    }
                }
            }
            if(null!=file_file){
                for (int i = 0; i < file.length; i++) {
                    if (file[i]!=file_file){
                        file[i].delete();
                    }
                }
            }
            if(max!=0){
                json = "{\"success\":true,\"url\":\""+"/upload/"+serial+"/"+max+"."+type+"\"}";
                logger.info(serial+",上报截屏信息成功"+",时间:"+new Date());
            }
        }
        writer.write(json);
        writer.close();
        return null;
    }
}
