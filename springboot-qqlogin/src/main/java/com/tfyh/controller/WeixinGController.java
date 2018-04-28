package com.tfyh.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;





import com.qq.connect.QQConnectException;
import com.qq.connect.utils.http.HttpClient;
import com.qq.connect.utils.http.Response;
import com.qq.connect.utils.json.JSONException;
import com.qq.connect.utils.json.JSONObject;
import com.tfyh.util.ShaUtil;
/**
 * 微信公众号
 * @author {author name}
 *
 */
@Controller
public class WeixinGController {
	public static Logger logger = LoggerFactory.getLogger(WeixinGController.class);
	
	public  static  String ACCESS_TOKEN="9_TqDa2eNkFs6W9dUehESia8ilI4qP4TKfi51SGPhJHWFKUVIzajPeB4-Hge41ambQlzg9H5V1vI6G_m9n6hzUIk19HGAdoFg_HUPsT8d7hgfhYUpNtLf6cmW2mHXml_ks1K9G6-JazKN89R-mNGDfAGAYRO";
	
	@Value("${weixin.appid}")
	private String appId;
	
	@Value("${weixin.appsecret}")
	private String appSecret;

	/**
	 * 获取access_token
	 * @throws QQConnectException 
	 * @throws JSONException 
	 */
	
	@RequestMapping(value="/getAccessToken")
	@ResponseBody
	public String getAccessToken() throws QQConnectException, JSONException{
		HttpClient httpClient = new HttpClient();
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appSecret;
		Response response = httpClient.get(url);
		JSONObject jsonObject = response.asJSONObject();		
		System.out.println("-=-=-=jsonObject-=-="+jsonObject.toString());
		String access_token = jsonObject.getString("access_token");
		/*得到access_Token 注意 这么的token有效时间只有两个小时  
		 *可以写个定时器 每小时通过appid 和appSecret刷新access_token
		 *刷新后 5分钟内 旧 的还可以使用
		 */
		ACCESS_TOKEN = access_token;
		System.out.println("-=-=accessToken-=-=="+access_token);
		return access_token;
	}
	
	@RequestMapping(value="/handleInfo",method=RequestMethod.POST)
	@ResponseBody
	public String handleInfo(HttpServletRequest request) throws IOException{
		ServletInputStream inputStream = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = reader.readLine())!=null){
			sb.append(line);
		}
		
		return sb.toString();
		
	}
	
	/**
	 * 创建菜单接口
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/addMenu")
	public String addMenu() throws Exception{
		//post 请求
		String urlString = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+ACCESS_TOKEN;
		
		return "";
	}
	
	
	/**
	 * 微信公众平台校验
	 * @throws IOException 
	 */
	@RequestMapping(value="/checkWx",method=RequestMethod.POST)
	@ResponseBody
	public String checkWx(HttpServletRequest request) throws IOException{
//		logger.info("进入公众号接入校验。。。。");
//		String signature = request.getParameter("signature");//微信加密签名
//		String timestamp = request.getParameter("timestamp");//时间戳
//		String nonce = request.getParameter("nonce");//随机数
//		String echostr = request.getParameter("echostr");//随机字符串
//		logger.info("请求参数为，signature={}，timestamp={},nonce={},echostr={}",signature,timestamp,nonce,echostr);
//		String result = CheckSignature(signature,timestamp,nonce,echostr);
		
		ServletInputStream inputStream = request.getInputStream();
		if(inputStream == null){
			return null;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String result = reader.readLine();
		
		return result;
	}
	/**
	 * 将三个参数字符串拼接成一个字符串进行 sha1加密
	 * @param str
	 * @return
	 */
	private static String CheckSignature(String signature,String timestamp,String nonce,String echostr){
        //第一步中填写的token一致
        String token="wangfeifei";

        ArrayList<String> list=new ArrayList<String>();
        list.add(nonce);
        list.add(timestamp);
        list.add(token);

        //字典序排序
        Collections.sort(list);
        //SHA1加密
        String checksignature=ShaUtil.SHA1(list.get(0)+list.get(1)+list.get(2));
        System.out.println(signature);
        System.out.println(checksignature);
        	
        if(checksignature.equals(signature)){//校验成功后 输出随机字符串 
        	logger.info("check success。。。。echostr={}",echostr);
        	return echostr;
        }
        logger.info("check fail。。");
        return null;    
    }
	

}
