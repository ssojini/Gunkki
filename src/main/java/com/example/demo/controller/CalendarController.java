package com.example.demo.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.mapper.CalendarMapper;
import com.example.demo.service.CalendarService;
import com.example.demo.service.ConnectService;
import com.example.demo.vo.HCalendar;
import com.example.demo.vo.Schedule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/calen")
@Slf4j
public class CalendarController
{
	@Autowired
	private CalendarService cs;
	@Autowired
	private CalendarMapper cm;
	@Autowired
	private HttpSession session;
	
	@GetMapping("/getCalendar")
	public String getCalendar(@RequestParam(value="day",required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate day, 
			Model model,String datetime, @SessionAttribute(name="userid", required = false)String userid)
	{
		Map<String, Object> map = cs.getCalendar(day);
		
		model.addAttribute("year",map.get("year")); //년
		model.addAttribute("month",map.get("month")); // 월
		model.addAttribute("day", map.get("day")); //일
		model.addAttribute("lastDay", map.get("lastDay")); // 마지막 일
		model.addAttribute("today", map.get("today"));
		model.addAttribute("firstDayOfWeek", map.get("firstDayOfWeek"));
		
		if(userid!=null) {			
			model.addAttribute("list",cs.listCalendar());
		}
		
	 	return "html/calendar/Calendar";
	}
	
	@GetMapping("/showCalen")
	public String showCalendarAdd(String day,Model model) 
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d"); 
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd"); 
		
		String beforeDate = day;
		String afterDate = "";
		
		try {
			Date date = dateFormat.parse(beforeDate); // 기존 string을 date 클래스로 변환
			afterDate = dateFormat2.format(date); // 변환한 값의 format 변경
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("day",afterDate);
		
		return "html/calendar/calendarAdd";
	}
	
	@PostMapping("/add")
	@ResponseBody
	public Map<String,Object> Add(@RequestParam("files")MultipartFile[] mfiles, HCalendar cal, Schedule sc, HttpServletRequest request, Model m) 
	{	
		
		Map<String,Object> map = new HashMap<>();
		map.put("add", cs.add(mfiles, request, cal, sc));
		cs.food_info(mfiles,cal, sc, request);
		return map;
	}
	
	@GetMapping("/detail/{num}")
	public String calenDetail(@PathVariable("num") int num, Model model)
	{
		model.addAttribute("food_info",cs.food_info(num));
		System.out.println("food_info: "+ cs.food_info(num));
		model.addAttribute("mlist",cs.detailCalendar(num));
		return "html/calendar/CalendarDetail";
	}
	
	@GetMapping("/edit/{num}")
	public String edit(@PathVariable("num")int num, Model model)
	{
		model.addAttribute("mlist",cs.detailCalendar(num));
		return "html/calendar/calendarEdit";
	}
	
	@PostMapping("/editCal/{spnum}")
	@ResponseBody
	public Map<String,Object> editCal(@PathVariable("spnum")int num,
			@RequestParam("files")MultipartFile[] mfiles,Model model,HttpServletRequest request
			,Schedule sch)
	{
		Map<String,Object> map = new HashMap<>();
		map.put("update", cs.updateFiles(mfiles, request, sch));
		return map;
	}
	
	@PostMapping("/delimg")
	@ResponseBody
	public Map<String,Object> delImg(int num) 
	{
		Map<String, Object> map = new HashMap<>();
		map.put("deleted", cs.delImg(num));
		return map;
	}
	
	@PostMapping("/delete")
	@ResponseBody
	public Map<String,Object> deleteById(int num,int anum)
	{
		Map<String,Object> map = new HashMap<>();
		map.put("deleted", cs.deleteAll(num,anum));
		return map;
	}

}

