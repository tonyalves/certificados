package fgf.certificados.bean;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class Certificate {
	private String lecturer;
	private String event;
	private String text;
	private Date eventDate;

	public String getEventDateStr() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(eventDate);

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String month = getMonth(calendar.get(Calendar.MONTH));
		int year = calendar.get(Calendar.YEAR);
		
		return day+" de "+month+" de "+year;
	}

	public String getMonth(int month) {
		List<String> months = Arrays.asList("Janeiro", "Fevereiro", "Março", "Abril",
				"Maio", "Junho", "Julho", "Agosto", "Setembro",
				"Outubro", "Novembro", "Dezembro");
		
		return months.get(month - 1);
	}
	
	public Certificate() {
	}

	public String getLecturer() {
		return lecturer;
	}

	public void setLecturer(String lecturer) {
		this.lecturer = lecturer;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	
}