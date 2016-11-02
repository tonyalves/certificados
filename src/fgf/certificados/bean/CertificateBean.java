package fgf.certificados.bean;

import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;
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


	public String generateCertificate() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		String path = servletContext.getRealPath("/") + "resources/";

		service.writeImageToFile(logo, path);
		service.writeImageToFile(signature, path);

		certificate.setPathLogo(path + logo.getFileName());
		certificate.setPathSignature(path + signature.getFileName());
		certificate.setPathToGenerate(path);
		CertificateReportGenerator report = null;
		try {
			report = new CertificateReportGenerator(certificate);
			report.generateReport();
		} catch (FileNotFoundException | JRException e) {
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
