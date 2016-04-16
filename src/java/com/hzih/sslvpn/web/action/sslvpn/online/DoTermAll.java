package com.hzih.sslvpn.web.action.sslvpn.online;

import cn.collin.commons.domain.PageResult;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-5-30
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
public class DoTermAll extends HttpServlet {
    private Logger logger = Logger.getLogger(DoTermAll.class);
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
        int count = 0;
        if (null != command && "allvpn".equals(command)) {
            int start_page = Integer.parseInt(beginno);
            int limit_page = Integer.parseInt(endno);
            int pageSize = Integer.parseInt(pagesize);
            int start = start_page * pageSize;
            int limit = limit_page * pageSize;
            try {
                PageResult pageResult = userDao.findByPages(null, -1, start, limit);
                if (pageResult != null) {
                    List<User> list = pageResult.getResults();
                    count = pageResult.getAllResultsAmount();
                    if (list != null) {
                        Iterator<User> raUserIterator = list.iterator();
                        while (raUserIterator.hasNext()) {
                            User log = raUserIterator.next();
                            if (raUserIterator.hasNext()) {
                                sb.append("{" +
                                        "\"id\":\"" + log.getId() +
                                        "\",\"cn\":\"" + log.getCn() +
                                        "\",\"serial_number\":\"" + log.getSerial_number() +
                                        "\",\"enabled\":\"" + log.getEnabled() +
                                        "\",\"net_id\":\"" + log.getNet_id() +
                                        "\",\"terminal_id\":\"" + log.getTerminal_id() + "\"" +
                                        "},");
                            } else {
                                sb.append("{" +
                                        "\"id\":\"" + log.getId() +
                                        "\",\"cn\":\"" + log.getCn() +
                                        "\",\"serial_number\":\"" + log.getSerial_number() +
                                        "\",\"enabled\":\"" + log.getEnabled() +
                                        "\",\"net_id\":\"" + log.getNet_id() +
                                        "\",\"terminal_id\":\"" + log.getTerminal_id() + "\"" +
                                        "}");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String ss = "[" + sb.toString() + "{\"total\":" + count + ",\"beginno\":\"" + beginno + "\",\"endno\":\"" + endno + "\",\"pagesize\":" + pagesize + "}]";
        writer.write(ss);
        logger.info("客户端地址:"+request.getRemoteAddr()+"获取SSLVPN在线用户信息成功.时间:"+new Date());
        writer.close();
    }
}