package fgf.certificados.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.Exporter;

@ManagedBean
@ViewScoped
public class CertificateBean {
	private Certificate certificate = new Certificate();
	private String html;
	@PostConstruct
	public void init() {
		Certificate certificate = (Certificate) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap()
				.get("certificate");
		
		if(certificate != null) {
			this.certificate = certificate;
		}
	}
	
	public void renderResponse() {
		try {
		    OutputStream file = new FileOutputStream(new File("/home/tony/certificado_"+certificate.getLecturer()+".pdf"));
		    Document document = new Document();
		    PdfWriter writer = PdfWriter.getInstance(document, file);
		    document.open();
		    InputStream is = new ByteArrayInputStream(html.getBytes());
		    XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
		    document.close();
		    file.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	public void generateReport() throws JRException, FileNotFoundException {

	    Map<String, Object> parametros = new HashMap<String, Object>();
	    
	    JasperPrint print = JasperFillManager.fillReport("certificate.jasper", parametros);

	    JRExporter exporter = new JRPdfExporter();
	    exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
	    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream("/home/tony/gasto_por_mes.pdf"));
	    exporter.exportReport();

	}
	
	public String generateCertificate() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("certificate", certificate);
		
		return "certificate.xhtml?faces-redirect=true";
	}

	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
}
