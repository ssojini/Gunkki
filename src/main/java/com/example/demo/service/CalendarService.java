package com.example.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.mapper.CalendarMapper;
import com.example.demo.mapper.MapMapper;
import com.example.demo.vo.AttachCalendar;
import com.example.demo.vo.HCalendar;
import com.example.demo.vo.Schedule;
import com.example.demo.vo.nutrients;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalendarService 
{
	@Autowired
	private CalendarMapper cm;
	@Autowired
	private HttpSession session;
	
	@Autowired
	private CalendarMapper map;
	
	@Autowired
	private ConnectService cs;
	
	private final Path fileStorageLocation;

	@Autowired
	public CalendarService(Environment env) 
	{
		this.fileStorageLocation = Paths.get("./src/main/resources/static/images/calendar")
				.toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Could not create the directory where the uploaded files will be stored.", ex);
		}
	}
	public boolean add(MultipartFile[] mfiles,HttpServletRequest request,HCalendar cal,Schedule sc)
	{
		ServletContext context = request.getServletContext();
		String savePath = fileStorageLocation.toUri().getPath();
		
		String userid = (String)session.getAttribute("userid");
		cal.setUserid(userid);
		
		List<AttachCalendar>list = new ArrayList<>();
		
		try {
			if(!mfiles[0].isEmpty()) {
				
				for(int i=0;i<mfiles.length;i++) 
				{
					if(mfiles[0].getSize()==0) continue;
					mfiles[i].transferTo(new File(savePath+"/"+mfiles[i].getOriginalFilename()));
					
					AttachCalendar attcal = new AttachCalendar();
					
					String pname = mfiles[i].getOriginalFilename();
					String extension = pname.substring(pname.lastIndexOf(".")); //파일 확장자
					
					String fname = UUID.randomUUID() + extension;
					attcal.setPname(pname);
					attcal.setFname(fname);
					
					list.add(attcal);	
				}
			}
			int rows = cm.saveCalendar(cal);
			int ro = cm.saveSchedule(sc);
			int r =0;
			if(!list.isEmpty()) {				
				r = cm.saveAttach(list);
			}
			return rows>0 && ro>0;
			
		} catch (Exception e) {
		e.printStackTrace();
		
		}
		return false;
	}
	
	public Map<String, Object> getCalendar(LocalDate day) 
	{
		Map<String, Object> map = new HashMap<>();
		
		java.util.Calendar cDay = java.util.Calendar.getInstance();
		
		 if(day == null) {
			 day = LocalDate.now();
		 } else {
			 cDay.set(day.getYear(), day.getMonthValue()-1, day.getDayOfMonth()); // 오늘날짜에서 day값과 동일한 값으로...
		 }

		java.util.Calendar firstDay = cDay;
		firstDay.set(java.util.Calendar.DATE, 1); // cDay에서 일만 1일로 변경
	
		map.put("year", cDay.get(java.util.Calendar.YEAR));
		map.put("month", cDay.get(java.util.Calendar.MONTH)+1);	
		map.put("day", day);	//
		map.put("lastDay", cDay.getActualMaximum(java.util.Calendar.DATE));		
		map.put("today",LocalDate.now());
		map.put("firstDayOfWeek", firstDay.get(java.util.Calendar.DAY_OF_WEEK));
		map.put("todayday", LocalDate.now().getDayOfMonth());
		map.put("dayday", cDay.get(java.util.Calendar.DATE));
		
		return map;
	}
	
	public List<Map<String,Object>> listCalendar()
	{
		String userid = (String)session.getAttribute("userid");
		
		List<Map<String,Object>> mlist = cm.list(userid);	
		List<Map<String,Object>> list = new ArrayList<>();
	
		for (int i = 0; i < mlist.size(); i++) 
		{
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> m = mlist.get(i);
			
			HCalendar cal = new HCalendar();
			
			BigDecimal big = (java.math.BigDecimal)m.get("C_NUM"); 
			cal.setC_num(big.intValue());
			
			Timestamp time = (Timestamp) m.get("DATETIME");
			Date tdate = new Date(time.getTime());
			cal.setDatetime(tdate);
			
			map.put("tdate", tdate.getDate());
			map.put("tmonth", tdate.getMonth()+1);
			
			Schedule sch = new Schedule();
			
			sch.setWhen((String) m.get("WHEN"));
			BigDecimal sbig = (java.math.BigDecimal)m.get("S_NUM");
			sch.setS_num(sbig.intValue());
			BigDecimal big1 = (java.math.BigDecimal)m.get("S_PNUM");
			sch.setS_pnum(big1.intValue());
			sch.setContent((String)m.get("CONTENT"));
			
			boolean found = false;
			String sPname = (String) m.get("PNAME");
			if(sPname == null) {
				map.put("cal", cal);
				map.put("sch", sch);
				list.add(map);
				if(!found) continue;
			}
			String[] file = sPname.split(",");
			for (int j = 0; j < file.length; j++) {
				
				AttachCalendar att = new AttachCalendar();
				
				att.setFname((String) m.get("FNAME"));
				att.setPname(file[j]);
				
				sch.getAttlist().add(att);
			}
			map.put("cal", cal);
			map.put("sch", sch);
			
			if(!found) list.add(map);
		}
		
		return list;
	}
	
	public nutrients food_info(int num){
		nutrients food_info = cm.food_info(num);
		return food_info;
	}

	public List<Map<String,Object>> detailCalendar(int num)
	{
		List<Map<String,Object>> mlist = cm.detail(num);
		List<Map<String,Object>> list = new ArrayList<>();
		
		Map<String, Object> map = new HashMap<>();
		
		Map<String, Object> m = mlist.get(0);
		
		HCalendar cal = new HCalendar();
		
		BigDecimal big = (java.math.BigDecimal)m.get("C_NUM"); 
		cal.setC_num(big.intValue());
		
		Timestamp time = (Timestamp) m.get("DATETIME");
		Date tdate = new Date(time.getTime());
		cal.setDatetime(tdate);
		
		Schedule sch = new Schedule();
		
		BigDecimal sbig = (java.math.BigDecimal)m.get("S_NUM");
		sch.setS_num(sbig.intValue());
		
		BigDecimal big1 = (java.math.BigDecimal)m.get("S_PNUM");
		sch.setS_pnum(big1.intValue());
		
		sch.setWhen((String) m.get("WHEN"));
		sch.setContent((String)m.get("CONTENT"));
		
		for (int i = 0; i < mlist.size(); i++) 
		{
			Map<String, Object> amap = mlist.get(i);
			
			AttachCalendar att = new AttachCalendar();	
			
			String sPname = (String) amap.get("PNAME");
			if(sPname==null) continue;

			BigDecimal abig = (BigDecimal) amap.get("A_NUM");
			BigDecimal acbig = (BigDecimal) m.get("A_PNUM");
			att.setA_pnum(acbig.intValue());
			att.setFname((String)amap.get("FNAME"));
			att.setPname((String)amap.get("PNAME"));
			att.setA_num(abig.intValue());
			sch.getAttlist().add(att);
		}
			map.put("cal", cal);
			map.put("sch", sch);
			
			list.add(map);
		return list;
	}
	
	@Transactional
	public boolean updateFiles(MultipartFile[] mfiles,HttpServletRequest request,Schedule sch)
	{
		ServletContext context = request.getServletContext();
		String savePath = fileStorageLocation.toUri().getPath();
		List<AttachCalendar>list = new ArrayList<>();
		int brow = cm.updateContenet(sch);
		System.err.println("sch"+sch);
		try {
			boolean updated = false;
			
			if(!mfiles[0].isEmpty())//첨부파일 있으면
			{
				for(int i=0;i<mfiles.length;i++) 
				{
					if(mfiles[0].getSize()==0) continue;
					mfiles[i].transferTo(new File(savePath+"/"+mfiles[i].getOriginalFilename()));
					
					AttachCalendar attcal = new AttachCalendar();
					
					String pname = mfiles[i].getOriginalFilename();
					String extension = pname.substring(pname.lastIndexOf(".")); //파일 확장자
					
					attcal.setA_pnum(sch.getS_num());
					
					String fname = UUID.randomUUID() + extension;
					attcal.setPname(pname);
					attcal.setFname(fname);
					list.add(attcal);	
					System.err.println("list"+list);
				}
				int arow = cm.updateAttach(list);
				updated = brow>0 && arow>0;
				
				return updated;
			}else {
				updated = brow > 0;
				return updated;
			}
			
		} catch (Exception e) {
		e.printStackTrace();
		
		}
		return false;
	}
	
	public int delImg(int num) 
	{
		int delimg = cm.delImg(num);
		if(delimg >0 ? true : false);
		
		return delimg;
	}
	
	@Transactional
	public boolean deleteAll(int num, int anum)
	{
		int brow = cm.schdelete(num);
		int arow = cm.caldelete(num);
		
		AttachCalendar ac = new AttachCalendar();
		String fname = ac.getFname();
		
		if(fname!=null) {
			int crow = cm.attcaldelete(anum);
		}
		
		if(arow>0 && brow>0) return true;
		
		return false;
	}
	
	public ResponseEntity<String> food_info(MultipartFile[] mfiles,HCalendar cal, Schedule sc, HttpServletRequest request)
	{
		
		System.out.println(mfiles[0].getOriginalFilename());
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://localhost:7878/upload");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		String fname = mfiles[0].getOriginalFilename();
		
		String absolutePath = new File("").getAbsolutePath();
		String filePath = absolutePath + "/src/main/resources/static/images/calendar/" + fname;

		builder.addBinaryBody("image", new File(filePath), ContentType.APPLICATION_OCTET_STREAM,fname);
		HttpEntity multipart = builder.build();
		httpPost.setEntity(multipart);
		try {
			HttpResponse response = httpclient.execute(httpPost);
			System.out.println(response);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		} 
		
		HttpResponse response;
		try {
			response = httpclient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			String responseString = EntityUtils.toString(responseEntity, "UTF-8");
			System.out.println(responseString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		URL url;
		String responseBody= null;
		try {
			url = new URL("http://localhost:7878/food_info");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			

			con.setDoOutput(true);

			int responseCode = con.getResponseCode();
			InputStream inputStream = null;
			if (responseCode == HttpURLConnection.HTTP_OK) {
			    inputStream = con.getInputStream();
			} else {
			    inputStream = con.getErrorStream();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;
			StringBuffer responses = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    responses.append(inputLine);
			}
			in.close();
			responseBody = responses.toString();
			
			System.out.println(responseBody);
			con.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String jsonResponse = responseBody;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> responseMap;
		String food_name = null;
		try {
			responseMap = mapper.readValue(jsonResponse, Map.class);
			food_name = responseMap.get("food_info");

			System.out.println(food_name); // 출력: 새우초밥
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		nutrients nu = new nutrients();
		nu.setFood_name(food_name);
		
		String userid = (String)session.getAttribute("userid");
		cal.setUserid(userid);
		String when = sc.getWhen();
		map.addnut(food_name,userid,when);
		
		return null;
	
	}

}