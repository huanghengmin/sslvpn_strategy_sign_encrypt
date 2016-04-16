package com.hzih.sslvpn.web.action.sslvpn.control;

import com.hzih.sslvpn.dao.UserDao;
import com.hzih.sslvpn.domain.User;
import com.hzih.sslvpn.utils.StringContext;
import com.hzih.sslvpn.utils.VPNConfigUtil;
import com.inetec.common.util.Proc;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * User: hhm
 * Date: 12-11-29
 * Time: 上午10:36
 * To change this template use File | Settings | File Templates.
 */
public class ControlAction extends ActionSupport {

    private Logger logger = Logger.getLogger(ControlAction.class);

    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * enable user
     *
     * @return
     * @throws Exception
     */
    public String enable() throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String json = null;
        String msg = null;
        String cn = request.getParameter("cn");
        if (null != cn) {
            User user = userDao.findByCommonName(cn);
            logger.info(user);
            if (null != user) {
                user.setEnabled(1);
                userDao.enableUser(user.getId());
                VPNConfigUtil.configUser(user, StringContext.ccd);
                msg ="启用客户端"+ user.getCn()+"访问成功";
                logger.info(msg+",时间:"+new Date());
                json = "{\"success\":true,\"msg\":\"" + msg + "\"}";
            }else {
                msg ="启用用户访问失败,未找到对应用户"+cn;
                json = "{\"success\":false,\"msg\":\"" + msg + "\"}";
                logger.info(msg+",时间:"+new Date());
            }
        }
        writer.write(json);
        writer.close();
        return null;
    }

    /**
     * disable user
     *
     * @return
     * @throws Exception
     */
    public String disable() throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String json = null;
        String msg = null;
        String cn = request.getParameter("cn");
        if (null != cn) {
            User user = userDao.findByCommonName(cn);
            if (null != user) {
                user.setEnabled(0);
                userDao.disableUser(user.getId());
                Proc kill_proc = new Proc();
                String kill_command = "sh " + StringContext.systemPath + "/script/kill_user.sh " + user.getCn();
                kill_proc.exec(kill_command);
                VPNConfigUtil.configUser(user, StringContext.ccd);
                msg ="禁止客户端访问成功,通用名:"+user.getCn();
                logger.info(msg+",时间:"+new Date());
                json = "{\"success\":true,\"msg\":\"" + msg + "\"}";
            }else {
                msg ="禁止客户端访问失败"+cn;
                json = "{\"success\":false,\"msg\":\"" + msg + "\"}";
                logger.info(msg+",时间:"+new Date());
            }
        }
        writer.write(json);
        writer.close();
        return null;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public String kill_disable() throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String json = null;
        String msg = null;
        String cn = request.getParameter("cn");
        if (null != cn) {
            User user = userDao.findByCommonName(cn);
            if (null != user) {
                user.setEnabled(0);
                userDao.disableUser(user.getId());
                Proc kill_proc = new Proc();
                String kill_command = "sh " + StringContext.systemPath + "/script/kill_user.sh " + user.getCn();
                kill_proc.exec(kill_command);
                msg ="T除客户端成功,通用名:"+cn;
                logger.info(msg+",时间"+new Date());
                json = "{\"success\":true,\"msg\":\"" + msg + "\"}";
            }
        }
        writer.write(json);
        writer.close();
        return null;
    }

}
