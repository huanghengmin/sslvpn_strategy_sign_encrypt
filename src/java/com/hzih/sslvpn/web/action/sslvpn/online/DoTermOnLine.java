package com.hzih.sslvpn.web.action.sslvpn.online;

import com.hzih.sslvpn.dao.UserDao;
import com.hzih.sslvpn.domain.User;
import com.hzih.sslvpn.utils.StringContext;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-5-30
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
public class DoTermOnLine extends HttpServlet {
    private Logger logger = Logger.getLogger(DoTermOnLine.class);
    private UserDao userDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * <p>
     * 在Servlet中注入对象的步骤:
     * 1.取得ServletContext
     * 2.利用Spring的工具类WebApplicationContextUtils得到WebApplicationContext
     * 3.WebApplicationContext就是一个BeanFactory,其中就有一个getBean方法
     * 4.有了这个方法就可像平常一样为所欲为了,哈哈!
     * </p>
     */
    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = this.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        userDao = (UserDao) ctx.getBean("userDao");
    }

    public Date getParseDate(String date) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
        return format.parse(date);
    }

    public List<String> getShellFileLine() throws Exception {
        Proc proc = new Proc();
        String command = null;
        if (OSInfo.getOSInfo().isWin()) {
            command = StringContext.systemPath + "/script/status_online.bat ";
        } else {
            command = StringContext.systemPath + "/script/status_online.sh ";
        }
        proc.exec(command);
        List<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(proc.getOutput());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lines.add(line);
        }
        return lines;
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        StringBuilder sb = new StringBuilder();
        PrintWriter writer = response.getWriter();
        String command = request.getHeader("command");
        String beginno = request.getHeader("beginno");  //开始页号
        String endno = request.getHeader("endno");      //结束页号
        String pagesize = request.getHeader("pagesize");   //页面大小
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int count = 0;
        if (null != command && "onvpn".equals(command)) {
            int start_page = Integer.parseInt(beginno);
            int limit_page = Integer.parseInt(endno);
            int pageSize = Integer.parseInt(pagesize);
            int start = start_page * pageSize;
            int limit = limit_page * pageSize;

            List<User> online_user = new ArrayList<>();
            List<String> lines = null;
            try {
                lines = getShellFileLine();
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            for (String s : lines) {
                String[] cols = s.split(",");
                if (cols.length == 5 && !s.startsWith("Common Name")) {
                    User user = new User();
                    user.setCn(cols[0]);
                    user.setReal_address(cols[1].split(":")[0]);
                    user.setByte_received(Long.parseLong(cols[2]));
                    user.setByte_send(Long.parseLong(cols[3]));
                    try {
                        user.setConnected_since(getParseDate(cols[4]));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    online_user.add(user);
                }
                if (cols.length == 4 && !s.startsWith("Virtual Address")) {
                    for (User u : online_user) {
                        if (u.getCn().equals(cols[1])) {
                            if (!cols[0].contains("/")) {
                                u.setVirtual_address(cols[0]);
                                try {
                                    u.setLast_ref(getParseDate(cols[3]));
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
            int end = 0;
            if (null != online_user && online_user.size() > 0) {
                end = start + limit;
                end = end > online_user.size() ? online_user.size() : end;
                for (int i = start; i < end; i++) {
                    User user = online_user.get(i);
                    User sqlUser = null;
                    try {
                        sqlUser = userDao.findByCommonName(user.getCn());
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    if (null != user && null != sqlUser) {
                        sb.append("{");
                        String username = sqlUser.getCn();
                        sb.append("\"cn\":\"" + username + "\",");
                        sb.append("\"userIp\":\"" + user.getReal_address() + "\",");
                        sb.append("\"serialNumber\":\"" + sqlUser.getSerial_number() + "\",");
                        sb.append("\"in_flux\":\"" + user.getByte_received() + "\",");
                        sb.append("\"out_flux\":\"" + user.getByte_send() + "\",");
                        if (null != user.getVirtual_address()) {
                            sb.append("\"connTime\":\"" + format.format(user.getConnected_since()) + "\",");
                            sb.append("\"virtualIp\":\"" + user.getVirtual_address() + "\",");
                            sb.append("\"lastTime\":\"" + format.format(user.getLast_ref()) + "\"");
                        } else {
                            sb.append("\"connTime\":\"" + format.format(user.getConnected_since()) + "\",");
                            sb.append("\"virtualIp\":\"\",");
                            sb.append("\"lastTime\":\"\"");
                        }
                        sb.append("},");
                    } else {
                        sb.append("{");
                        sb.append("\"cn\":\"" + user.getCn() + "\",");
                        sb.append("\"userIp\":\"" + user.getReal_address() + "\",");
                        sb.append("\"serialNumber\":\"" + user.getSerial_number() + "\",");
                        sb.append("\"in_flux\":\"" + user.getByte_received() + "\",");
                        sb.append("\"out_flux\":\"" + user.getByte_send() + "\",");
                        if (null != user.getVirtual_address()) {
                            sb.append("\"connTime\":\"" + format.format(user.getConnected_since()) + "\",");
                            sb.append("\"virtualIp\":\"" + user.getVirtual_address() + "\",");
                            sb.append("\"lastTime\":\"" + format.format(user.getLast_ref()) + "\"");
                        } else {
                            sb.append("\"connTime\":\"" + format.format(user.getConnected_since()) + "\",");
                            sb.append("\"virtualIp\":\"\",");
                            sb.append("\"lastTime\":\"\"");
                        }
                        sb.append("},");
                    }
                }
            }
        }
        String ss = "[" + sb.toString() + "{\"total\":" + count + ",\"beginno\":\"" + beginno + "\",\"endno\":\"" + endno + "\",\"pagesize\":" + pagesize + "}]";
        writer.write(ss);
        logger.info("客户端地址:"+request.getRemoteAddr()+"获取SSLVPN在线用户信息成功.时间:"+new Date());
        writer.close();
    }
}