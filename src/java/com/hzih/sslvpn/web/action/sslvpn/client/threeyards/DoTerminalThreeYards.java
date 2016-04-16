package com.hzih.sslvpn.web.action.sslvpn.client.threeyards;

import com.hzih.sslvpn.dao.UserDao;
import com.hzih.sslvpn.domain.User;
import com.hzih.sslvpn.web.action.sslvpn.client.strategy.StrategyXMLUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by hhm on 2014/12/16.
 */
public class DoTerminalThreeYards extends HttpServlet {
    private UserDao userDao;
    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = this.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        userDao = (UserDao) ctx.getBean("userDao");
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String serial = request.getParameter("serial");
        String terminalId = request.getParameter("terminalId");
        String simId = request.getParameter("simId");
        String json = null;
        String msg = null;
        if (serial != null && !"".equals(serial) && terminalId != null && !"".equals(terminalId) && simId != null && !"".equals(simId)) {
            if (("1").equalsIgnoreCase(StrategyXMLUtils.getValue(StrategyXMLUtils.threeyards))) {
                try {
                    User user = userDao.findBySerialNumber(serial);
                    if (user != null) {
                        if (user.getTerminal_id() != null && !"".equals(user.getTerminal_id()) && user.getNet_id() != null && !"".equals(user.getNet_id())) {
                            if (!user.getTerminal_id().equalsIgnoreCase(terminalId) || !user.getNet_id().equalsIgnoreCase(simId)) {
                                msg = "客户端三码校验不匹配.请更换成为原有TF卡和SIM卡后尝试连接.";
                                json = "{\"success\":false,\"msg\":\"" + msg +"\"}";
                                logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                                writer.write(json);
                                writer.flush();
                                writer.close();
                                return;
                            } else {
                                if (user.getEnabled() == 1) {
                                    msg = "客户端三码校验匹配成功.";
                                    json = "{\"success\":true,\"msg\":\"" + msg +"\"}";
                                    logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                                    writer.write(json);
                                    writer.flush();
                                    writer.close();
                                    return;
                                } else {
                                    msg = "用户已被禁止拨号.";
                                    json = "{\"success\":false,\"msg\":\"" + msg +"\"}";
                                    logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                                    writer.write(json);
                                    writer.flush();
                                    writer.close();
                                    return;
                                }
                            }
                        } else {
                            if (user.getEnabled() == 1) {
                                    User simUser = userDao.findSimId(simId);
                                    if(simUser!=null) {
                                        msg = "客户端三码校验不匹配,SIM卡号已被绑定.请更换成为原有TF卡和SIM卡后尝试连接.";
                                        json = "{\"success\":false,\"msg\":\"" + msg + "\"}";
                                        logger.info("客户端序列号：" + serial + ",终端编号:" + terminalId + ",电话卡编号：" + simId + "," + msg + ",时间:" + new Date());
                                        writer.write(json);
                                        writer.flush();
                                        writer.close();
                                        return;
                                    }

                                    User terminalUser = userDao.findTerminalId(terminalId);
                                    if(terminalUser!=null){
                                        msg = "客户端三码校验不匹配.请更换成为原有TF卡和SIM卡后尝试连接.";
                                        json = "{\"success\":false,\"msg\":\"" + msg +"\"}";
                                        logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                                        writer.write(json);
                                        writer.flush();
                                        writer.close();
                                        return;
                                    }

                                    if(simUser==null&&terminalUser==null) {
                                        user.setTerminal_id(terminalId);
                                        user.setNet_id(simId);
                                        userDao.modify(user);
                                        msg = "客户端三码信息保存成功.";
                                        json = "{\"success\":true,\"msg\":\"" + msg +"\"}";
                                        logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                                        writer.write(json);
                                        writer.flush();
                                        writer.close();
                                        return;
                                    }
                            } else {
                                msg = "用户已被禁止拨号.";
                                json = "{\"success\":false,\"msg\":\"" + msg +"\"}";
                                logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                                writer.write(json);
                                writer.flush();
                                writer.close();
                                return;
                            }
                        }
                    } else {
                        msg = "服务器未找到对应用户.";
                        json = "{\"success\":true,\"msg\":\"" + msg +"\"}";
                        logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                        writer.write(json);
                        writer.flush();
                        writer.close();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = "服务器未找到对应用户.";
                    json = "{\"success\":true,\"msg\":\"" + msg +"\"}";
                    logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                    writer.write(json);
                    writer.flush();
                    writer.close();
                    return;
                }
            } else {
                try {
                    User user = userDao.findBySerialNumber(serial);
                    if (user != null) {
                        if (user.getEnabled() == 1) {
                            msg = "服务器未启用三码合一校验.";
                            json = "{\"success\":true,\"msg\":\"" + msg +"\"}";
                            logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                            writer.write(json);
                            writer.flush();
                            writer.close();
                            return;
                        } else {
                            msg = "用户已被禁止拨号";
                            json = "{\"success\":false,\"msg\":\"" + msg +"\"}";
                            logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                            writer.write(json);
                            writer.flush();
                            writer.close();
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = "服务器未找到对应用户.";
                    json = "{\"success\":true,\"msg\":\"" + msg +"\"}";
                    logger.info("客户端序列号："+serial+",终端编号:"+terminalId+",电话卡编号："+simId+","+msg+",时间:"+new Date());
                    writer.write(json);
                    writer.flush();
                    writer.close();
                    return;
                }
            }
        }
    }
}
