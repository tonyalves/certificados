package fgf.certificados.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil {
	
	public String formattedDate(Date date, String separator) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String month = getMonth(calendar.get(Calendar.MONTH));
		int year = calendar.get(Calendar.YEAR);
		return day+separator+month+separator+year;
	}
	
	public String getMonth(int month) {
		List<String> months = Arrays.asList("Janeiro", "Fevereiro", "Março", "Abril",
				"Maio", "Junho", "Julho", "Agosto", "Setembro",
				"Outubro", "Novembro", "Dezembro");
		
		return months.get(month);
	}
}
