package com.example.demo.vo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminBoard 
{
	public AdminBoard(int adnum) {
		this.adnum= adnum;
	}
	private int adnum;
	private String name;
	private String title;
	private String content;
	private java.sql.Timestamp date_admin;
	private int hit;
	private String author;
	private List<AdminAttachBoard> attList = new ArrayList<>();


}
