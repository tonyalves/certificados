package fgf.certificados.bean;

import java.util.Date;

import fgf.certificados.util.DateUtil;


public class Certificate {
	private String lecturer;
	private String event;
	private String text;
	private Date eventDate;
	private String signature;
	private String office;
	private String eventDateStr;
	private String pathLogo;
	private String pathSignature;
	
	public String getEventDateStr() {
		if(eventDate == null)
			return null;
		
		eventDateStr = new DateUtil().formattedDate(eventDate, " de ");
		
		return eventDateStr;
	}

	public void setEventDateStr(String eventDateStr) {
		this.eventDateStr = eventDateStr;
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
		if(text == null) {
			text = "Ministrou a atividade intitulada: “NOME_DO_EVENTO”, realizada na "
			+"LOCAL_DO_EVENTO, em DATA_DO_EVENTO.";
		}
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

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getPathLogo() {
		return pathLogo;
	}

	public void setPathLogo(String pathLogo) {
		this.pathLogo = pathLogo;
	}

	public String getPathSignature() {
		return pathSignature;
	}

	public void setPathSignature(String pathSignature) {
		this.pathSignature = pathSignature;
	}
}