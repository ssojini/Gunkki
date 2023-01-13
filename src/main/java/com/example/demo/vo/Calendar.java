package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data 
@ToString
@EqualsAndHashCode(exclude= {})
@AllArgsConstructor 
@NoArgsConstructor
public class Calendar 
{
	//시퀀스
	private int calen_num;
	
	private java.sql.Date date;

	private String m_contents;
	private String l_contents;
	private String e_contents;
	private String snack_contents;
}