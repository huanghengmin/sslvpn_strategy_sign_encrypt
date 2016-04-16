package com.hzih.sslvpn.web.action.sslvpn.client.strategy;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by hhm on 2014/12/11.
 */
public class ClientStrategyAction extends ActionSupport {
    private Logger logger = Logger.getLogger(ClientStrategyAction.class);


    /**
     * 返回JSON数据格式
     * @param sb
     */
    private void jsonResult(StringBuilder sb) {
        sb.append("{");
        /**
         *
         */
        sb.append("\"gps\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.gps))+"\",");
        sb.append("\"gprs\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.gprs))+"\",");
        sb.append("\"wifi\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.wifi))+"\",");
        sb.append("\"bluetooth\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.bluetooth))+"\",");
        sb.append("\"gps_interval\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.gps_interval))+"\",");
        sb.append("\"threeyards\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.threeyards))+"\",");
        sb.append("\"strategy_interval\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.strategy_interval))+"\",");
        sb.append("\"view\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.view))+"\",");
        sb.append("\"view_interval\":\""+ isNULL(StrategyXMLUtils.getValue(StrategyXMLUtils.view_interval))+"\"");
        sb.append("}");
    }

    public String isNULL(Object o){
        if(o==null){
            return "";
        } else {
            return o.toString();
        }
    }


    /**
     * 查找
     * @return
     */
     public String findStrategy()throws Exception{
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        int totalCount =0;
        StringBuilder sb = new StringBuilder();
        jsonResult(sb);
        totalCount = totalCount+1;
        StringBuilder json=new StringBuilder("{\"totalCount\":"+totalCount+",\"root\":[");
        json.append(sb.toString());
        json.append("]}");
         logger.info("remoteIp:"+request.getRemoteAddr()+",读取策略配置信息完成。");
        writer.write(json.toString());
        writer.flush();
        writer.close();
        return null;
    }

}
