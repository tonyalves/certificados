package fgf.certificados.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import fgf.certificados.service.CertificateService;
import fgf.certificados.util.DateUtil;
import fgf.certificate.model.Certificate;
import net.sf.jasperreports.engine.JRException;

@ManagedBean
@ViewScoped
public class CertificateCSVBean {

	private Certificate certificate = new Certificate();
	private StreamedContent zipDownload;
	private UploadedFile csv;
	private String preText;
	private UploadedFile logo;
	private UploadedFile signature;
	private String eventLocation;

	@ManagedProperty(value = "#{certificateService}")
	private CertificateService service;

	public String validateData() {
		if (eventLocation == null || eventLocation.isEmpty())
			return "Local do evento";
		else if (certificate.getSignature() == null || certificate.getSignature().isEmpty())
			return "Assinatura";
		else if (certificate.getOffice() == null || certificate.getOffice().isEmpty())
			return "Cargo";
		else
			return null;
	}

	public StreamedContent uploadCSV() {
		String field = validateData();
		if(field  != null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não preencheu o campo "+field));
			return null;
		}
		
		BufferedReader br = null;
		List<Certificate> certificates = new ArrayList<>();

		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		
		URL resource = null;
		try {
			resource	= servletContext.getResource("resources");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		String path = resource.getPath();

		if(csv.getFileName() == null || csv.getFileName().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não escolheu o arquivo CSV."));
			return null;
		}
		
		if(logo.getFileName() == null || logo.getFileName().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não escolheu a logo do evento."));
			return null;
		}
		
		if(signature.getFileName() == null || signature.getFileName().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não escolheu a assinatura."));
			return null;
		}
		service.writeImageToFile(logo, path);
		service.writeImageToFile(signature, path);
		
		try {
			Reader reader = new InputStreamReader(csv.getInputstream());
			br = new BufferedReader(reader);
		} catch (IOException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha", "Não foi possível gerar certificados: "+e.getMessage()));
			return null;
		}
		String s = null;

		try {
			br.readLine();
			while (br.ready()) {
				s = br.readLine();
				if(s == null || s.isEmpty())
					continue;
				String[] lineSplited = s.split(";");
				if(lineSplited.length < 3) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha", 
								"Não foi possível gerar certificados: O Arquivo CSV precisa ter 3 colunas separadas por ; (ponto e vírgula)"));
					return null;
				}
				
				Certificate certificate = new Certificate();

				certificate.setSignature(this.certificate.getSignature());
				certificate.setOffice(this.certificate.getOffice());
				certificate.setPathLogo(path + logo.getFileName());
				certificate.setPathSignature(path + signature.getFileName());
				certificate.setLogoFGF(path + "logo-top.png");

				certificate.setLecturer(lineSplited[1]);
				certificate.setText(preText, lineSplited[0], lineSplited[2], eventLocation);
				certificates.add(certificate);
			}
		} catch (IOException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha", "Não foi possível gerar certificados: "+e.getMessage()));
			return null;
		}
		
		FileInputStream zipFile = putOnZipFIle(certificates);
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Certificados baixados."));
		DateUtil util = new DateUtil();
		String dateForm = util.formattedDate(new Date(), "_");
		zipDownload = new DefaultStreamedContent(zipFile, "document/zip", "certificados_"+dateForm+"_"+System.currentTimeMillis()+".zip");
		
		return zipDownload;
	}

	private FileInputStream putOnZipFIle(List<Certificate> certificates) {
		CertificateReportGenerator report;
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		
		URL resource = null;
		try {
			resource	= servletContext.getResource("resources");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		String path = resource.getPath();
		FileOutputStream zipFile = null;
		try {
			zipFile = new FileOutputStream (path+"certificates.zip");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		byte[] buffer = new byte[1024];
		ZipOutputStream zos = new ZipOutputStream(zipFile);
	
		for(Certificate c : certificates) {
			try {
				report = new CertificateReportGenerator(c);
				File pdfFile = report.generateReport();
				
				ZipEntry entry = new ZipEntry(pdfFile.getName());
				zos.putNextEntry(entry);
				FileInputStream in = new FileInputStream(pdfFile);
				int len;
	    		while ((len = in.read(buffer)) > 0) {
	    			zos.write(buffer, 0, len);
	    		}
	    		zos.closeEntry();
	    		in.close();
	    		pdfFile.delete();
	    		
			} catch (FileNotFoundException | JRException e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha", "Não foi possível gerar certificados: "+e.getMessage()));
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(path+"certificates.zip");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return inStream;
	}

	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public UploadedFile getCsv() {
		return csv;
	}

	public void setCsv(UploadedFile csv) {
		this.csv = csv;
	}

	public UploadedFile getLogo() {
		return logo;
	}

	public void setLogo(UploadedFile logo) {
		this.logo = logo;
	}

	public UploadedFile getSignature() {
		return signature;
	}

	public void setSignature(UploadedFile signature) {
		this.signature = signature;
	}

	public CertificateService getService() {
		return service;
	}

	public void setService(CertificateService service) {
		this.service = service;
	}

	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	public String getPreText() {
		return preText;
	}

	public void setPreText(String preText) {
		this.preText = preText;
	}

	public StreamedContent getZipDownload() {
		return zipDownload;
	}

	public void setZipDownload(StreamedContent zipDownload) {
		this.zipDownload = zipDownload;
	}
}
