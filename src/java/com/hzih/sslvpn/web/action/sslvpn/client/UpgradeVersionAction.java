package com.hzih.sslvpn.web.action.sslvpn.client;

import com.hzih.sslvpn.utils.FileUtil;
import com.hzih.sslvpn.utils.StringContext;
import com.hzih.sslvpn.entity.Version;
import com.hzih.sslvpn.utils.VersionUtils;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Administrator on 15-3-10.
 */
public class UpgradeVersionAction extends ActionSupport {
    private Logger logger = Logger.getLogger(UpgradeVersionAction.class);

    public String check() throws Exception {
        /**
         * 返回三个客户端文件名和版本信息
         */
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setCharacterEncoding("utf-8");
        String os = request.getParameter("os");
        String version = request.getParameter("version");
        String json = null;
        String  msg = null;
        if (os != null && version != null) {
            if (os.equals("android")) {
                String android_version = StringContext.systemPath + "/client/android" + "/version.xml";
                File android_info = new File(android_version);
                if (android_info.exists()) {
                    Version v = VersionUtils.readInfo(android_info);
                    if (v != null) {
                        String vs = v.getVersion();
                        int diff = VersionUtils.compareVersion(vs, version);
                          if (diff>0) {
                            long length = 0 ;
                            File file = new File(StringContext.systemPath + "/client/android" + "/" + v.getName());
                            if(file.exists())
                                length = file.length();
                            //需要更新
                             msg = "检测到新版本,是否更新?";
                            json = "{\"name\":\"" + v.getName() + "\",\"length\":"+String.valueOf(length)+",\"version\":\""+v.getVersion()+"\",\"flag\":true}";
                        } else {
                            json = "{\"name\":\"\",\"length\":0,\"version\":\"\",\"flag\":false}";
                        }
                    }
                }
            } else if (os.equals("x86")) {
                String win32_version = StringContext.systemPath + "/client/windows/x86" + "/version.xml";
                File win32_info = new File(win32_version);
                if (win32_info.exists()) {
                    Version v = VersionUtils.readInfo(win32_info);
                    if (v != null) {
                        String vs = v.getVersion();
                        int diff = VersionUtils.compareVersion(vs, version);
                        if (diff>0) {
                            long length = 0 ;
                            File file = new File(StringContext.systemPath + "/client/windows/x86" + "/" + v.getName());
                            if(file.exists())
                                length = file.length();
                            //需要更新
                             msg = "检测到新版本,是否更新?";
                            json = "{\"name\":\"" + v.getName() + "\",\"length\":"+String.valueOf(length)+",\"version\":\""+v.getVersion()+"\",\"flag\":true}";
                        } else {
                            json = "{\"name\":\"\",\"length\":0,\"version\":\"\",\"flag\":false}";
                        }
                    }
                }
            } else if (os.equals("x64")) {
                String win32_version = StringContext.systemPath + "/client/windows/x64" + "/version.xml";
                File win32_info = new File(win32_version);
                if (win32_info.exists()) {
                    Version v = VersionUtils.readInfo(win32_info);
                    if (v != null) {
                        String vs = v.getVersion();
                        int diff = VersionUtils.compareVersion(vs, version);
                          if (diff>0) {
                            long length = 0 ;
                            File file = new File(StringContext.systemPath + "/client/windows/x64" + "/" + v.getName());
                            if(file.exists())
                                length = file.length();
                            //需要更新
                            msg = "检测到新版本,是否更新?";
                            json = "{\"name\":\"" + v.getName() + "\",\"length\":"+String.valueOf(length)+",\"version\":\""+v.getVersion()+"\",\"flag\":true}";
                        } else {
                            json = "{\"name\":\"\",\"length\":0,\"version\":\"\",\"flag\":false}";
                        }
                    }
                }
            }else if (os.equals("linuxMobile")) {
                String win32_version = StringContext.systemPath + "/client/linux/linuxMobile" + "/version.xml";
                File win32_info = new File(win32_version);
                if (win32_info.exists()) {
                    Version v = VersionUtils.readInfo(win32_info);
                    if (v != null) {
                        String vs = v.getVersion();
                        int diff = VersionUtils.compareVersion(vs, version);
                        if (diff>0) {
                            long length = 0 ;
                            File file = new File(StringContext.systemPath + "/client/linux/linuxMobile" + "/" + v.getName());
                            if(file.exists())
                                length = file.length();
                            //需要更新
                            msg = "检测到新版本,是否更新?";
                            json = "{\"name\":\"" + v.getName() + "\",\"length\":"+String.valueOf(length)+",\"version\":\""+v.getVersion()+"\",\"flag\":true}";
                        } else {
                            json = "{\"name\":\"\",\"length\":0,\"version\":\"\",\"flag\":false}";
                        }
                    }
                }
            }else if (os.equals("linuxCz")) {
                String win32_version = StringContext.systemPath + "/client/linux/linuxCz" + "/version.xml";
                File win32_info = new File(win32_version);
                if (win32_info.exists()) {
                    Version v = VersionUtils.readInfo(win32_info);
                    if (v != null) {
                        String vs = v.getVersion();
                        int diff = VersionUtils.compareVersion(vs, version);
                        if (diff>0) {
                            long length = 0 ;
                            File file = new File(StringContext.systemPath + "/client/linux/linuxCz" + "/" + v.getName());
                            if(file.exists())
                                length = file.length();
                            //需要更新
                            msg = "检测到新版本,是否更新?";
                            json = "{\"name\":\"" + v.getName() + "\",\"length\":"+String.valueOf(length)+",\"version\":\""+v.getVersion()+"\",\"flag\":true}";
                        } else {
                            json = "{\"name\":\"\",\"length\":0,\"version\":\"\",\"flag\":false}";
                        }
                    }
                }
            }
        }
        PrintWriter writer = response.getWriter();
        writer.write(json);
        if(msg!=null){
            logger.info("客户端:"+request.getRemoteAddr()+",检测版本信息:"+msg+",时间:"+new Date());
        }
        writer.flush();
        writer.close();
        return null;
    }

    public String upgrade() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setCharacterEncoding("utf-8");
        String os = request.getParameter("os");
        try {
            String name = null;
            String path = null;
            if (os.equals("android")) {
                String android_version = StringContext.systemPath + "/client/android" + "/version.xml";
                File android_info = new File(android_version);
                if (android_info.exists()) {
                    Version v = VersionUtils.readInfo(android_info);
                    if (v != null) {
                        name = v.getName();
                    }
                }
                path = StringContext.systemPath + "/client/android" + "/" + name;
            } else if (os.equals("x86")) {
                String windows_version = StringContext.systemPath + "/client/windows/x86" + "/version.xml";
                File windows_info = new File(windows_version);
                if (windows_info.exists()) {
                    Version v = VersionUtils.readInfo(windows_info);
                    if (v != null) {
                        name = v.getName();
                    }
                }
                path = StringContext.systemPath + "/client/windows/x86" + "/" + name;
            } else if (os.equals("x64")) {
                String windows_version = StringContext.systemPath + "/client/windows/x64" + "/version.xml";
                File windows_info = new File(windows_version);
                if (windows_info.exists()) {
                    Version v = VersionUtils.readInfo(windows_info);
                    if (v != null) {
                        name = v.getName();
                    }
                }
                path = StringContext.systemPath + "/client/windows/x64" + "/" + name;
            }else if (os.equals("linuxMobile")) {
                String windows_version = StringContext.systemPath + "/client/linux/linuxMobile" + "/version.xml";
                File windows_info = new File(windows_version);
                if (windows_info.exists()) {
                    Version v = VersionUtils.readInfo(windows_info);
                    if (v != null) {
                        name = v.getName();
                    }
                }
                path = StringContext.systemPath + "/client/linux/linuxMobile" + "/" + name;
            }else if (os.equals("linuxCz")) {
                String windows_version = StringContext.systemPath + "/client/linux/linuxCz" + "/version.xml";
                File windows_info = new File(windows_version);
                if (windows_info.exists()) {
                    Version v = VersionUtils.readInfo(windows_info);
                    if (v != null) {
                        name = v.getName();
                    }
                }
                path = StringContext.systemPath + "/client/linux/linuxCz" + "/" + name;
            }
            File source = new File(path);
            if (source.exists()) {
                response = FileUtil.copy(source, response);
                logger.info("客户端:"+request.getRemoteAddr()+",更新版本,系统:"+os+",成功,时间:"+new Date());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
