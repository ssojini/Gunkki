package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.vo.MapInfo;


@Mapper
public interface MapMapper {
	
	public List<MapInfo> center();

	public List<MapInfo>center_search_detail(String area);
}
