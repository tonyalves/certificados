package fgf.certificados.bean;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
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
public class CertificateBean {

	private Certificate certificate = new Certificate();
	
	private UploadedFile logo;
	private UploadedFile signature;
	
	@ManagedProperty(value = "#{certificateService}")
	private CertificateService service;

	@PostConstruct
	public void init() {
	}

	public String validateData() {
		if(certificate.getLecturer() == null || certificate.getLecturer().isEmpty())
			return "Palestrante";
		else if(certificate.getSignature() == null || certificate.getSignature().isEmpty())
			return "Assinatura";
		else if(certificate.getOffice() == null || certificate.getOffice().isEmpty())
			return "Cargo";
		else 
			return null;
	}
	
	public String generateCertificate() {
		String field = validateData();
		if(field  != null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Você não preencheu o campo "+field));
			return null;
		}
		
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		
		URL resource = null;
		try {
			resource	= servletContext.getResource("resources");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String path = resource.getPath();
		
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

		certificate.setPathLogo(path + logo.getFileName());
		certificate.setPathSignature(path + signature.getFileName());
		certificate.setPathToGenerate(path);
		certificate.setLogoFGF(path + "logo-top.png");
		CertificateReportGenerator report = null;
		report = new CertificateReportGenerator(certificate);
		try {
			report.generateReport();
		} catch (JRException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String pathPDF = report.getFilePDFName();
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("certificatePDFName", pathPDF);
		return "certificatePDF.xhtml?faces-redirect=true";
	}


	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
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
}
