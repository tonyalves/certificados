package fgf.certificados.bean;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.UploadedFile;

import fgf.certificados.service.CertificateService;
import fgf.certificate.model.Certificate;
import net.sf.jasperreports.engine.JRException;

@ManagedBean
@ViewScoped
public class CertificateCSVBean {

	private Certificate certificate = new Certificate();
	private UploadedFile csv;
	private String pathGenerator;
	private UploadedFile logo;
	private UploadedFile signature;
	private String eventLocation;
	
	@ManagedProperty(value = "#{certificateService}")
	private CertificateService service;
	
	public String validateData() {
		if(pathGenerator == null || pathGenerator.isEmpty())
			return "Gerar em";
		else if (eventLocation == null || eventLocation.isEmpty())
			return "Local do evento";
		else if(certificate.getSignature() == null || certificate.getSignature().isEmpty())
			return "Assinatura";
		else if(certificate.getOffice() == null || certificate.getOffice().isEmpty())
			return "Cargo";
		else 
			return null;
	}
	public void uploadCSV() {
		String field = validateData();
		if(field  != null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não preencheu o campo "+field));
			return;
		}
		
		BufferedReader br = null;
		List<Certificate> certificates = new ArrayList<>();

		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		String path = servletContext.getRealPath("/") + "resources/";

		if(csv.getFileName() == null || csv.getFileName().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não escolheu o arquivo CSV."));
			return ;
		}
		
		if(logo.getFileName() == null || logo.getFileName().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não escolheu a logo do evento."));
			return ;
		}
		
		if(signature.getFileName() == null || signature.getFileName().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não escolheu a assinatura."));
			return ;
		}
		service.writeImageToFile(logo, path);
		service.writeImageToFile(signature, path);
		
		try {
			Reader reader = new InputStreamReader(csv.getInputstream());
			br = new BufferedReader(reader);
		} catch (IOException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha", "Não foi possível gerar certificados: "+e.getMessage()));
			return;
		}
		String s = null;

		try {
			br.readLine();
			while (br.ready()) {
				s = br.readLine();
				if(s == null || s.isEmpty())
					continue;
				String[] lineSplited = s.split(";");
				Certificate certificate = new Certificate();

				certificate.setSignature(this.certificate.getSignature());
				certificate.setOffice(this.certificate.getOffice());
				certificate.setPathLogo(path + logo.getFileName());
				certificate.setPathSignature(path + signature.getFileName());
				certificate.setPathToGenerate(pathGenerator);
				certificate.setLogoFGF(path + "logo-top.png");

				certificate.setLecturer(lineSplited[1]);
				certificate.setText(lineSplited[0], lineSplited[2], eventLocation);
				certificates.add(certificate);
			}
		} catch (IOException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha", "Não foi possível gerar certificados: "+e.getMessage()));
			return;
		}
		
		CertificateReportGenerator report = null;
		for(Certificate c : certificates) {
			try {
				report = new CertificateReportGenerator(c);
				report.generateReport();
			} catch (FileNotFoundException | JRException e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha", "Não foi possível gerar certificados: "+e.getMessage()));
				return;
			}
		}

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Certificados salvos em: "+pathGenerator));
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

	public String getPathGenerator() {
		return pathGenerator;
	}

	public void setPathGenerator(String pathGenerator) {
		this.pathGenerator = pathGenerator;
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
}
