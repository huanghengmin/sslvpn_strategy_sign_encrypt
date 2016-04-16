package com.hzih.sslvpn.web.action.sslvpn.client.check;

import com.hzih.sslvpn.dao.UserDao;
import com.hzih.sslvpn.domain.User;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Administrator on 15-5-26.
 */
public class CheckUploadViewAction extends ActionSupport {
    private Logger logger = Logger.getLogger(getClass());

    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public String checkView()throws Exception{
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String json = null;
        String msg = null;
        String serial = request.getParameter("serial");
        if (null != serial) {
            User user = userDao.findBySerialNumber(serial);
            if (null != user) {
                int view_flag = user.getView_flag();
                if(view_flag==1){
                    msg = "上报当前屏幕信息";
                    json = "{\"success\":true,\"msg\":\"" + msg + "\"}";
                    logger.info(serial+","+msg+",时间:"+new Date());
                }else {
                    msg = "不需要上报当前屏幕信息";
                    json = "{\"success\":false,\"msg\":\"" + msg + "\"}";
                    logger.info(serial+","+msg+",时间:"+new Date());
                }
            } else {
                msg = "检测截屏状态失败,未找到对应用户";
                json = "{\"success\":false,\"msg\":\"" + msg + "\"}";
                logger.info(serial+","+msg+",时间:"+new Date());
            }
        }
        writer.write(json);
        writer.close();
        return null;
    }
}
