package fgf.certificados.bean;

import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

@ManagedBean
@ViewScoped
public class CertificateBean {

	private Certificate certificate = new Certificate();
	private String html;
	private UploadedFile logo;
	private UploadedFile signature;
	
	@PostConstruct
	public void init() {
		Certificate certificate = (Certificate) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("certificate");

		if (certificate != null) {
			this.certificate = certificate;

			try {
				CertificateReportGenerator report = new CertificateReportGenerator(certificate);
				report.generateReport();
			} catch (FileNotFoundException | JRException e) {
				e.printStackTrace();
			}
		}

	}

	public String generateCertificate() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		String path = servletContext.getRealPath("/") + "resources/";
		
		if(logo != null && logo.getFileName() != null && !logo.getFileName().isEmpty()) {
			try {
				logo.write(path + logo.getFileName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(signature != null && signature.getFileName() != null && !signature.getFileName().isEmpty()) {
			try {
				signature.write(path + signature.getFileName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		certificate.setPathLogo(path + logo.getFileName());
		certificate.setPathSignature(path + signature.getFileName());
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

}
