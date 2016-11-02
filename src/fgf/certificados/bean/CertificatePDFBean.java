package fgf.certificados.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class CertificatePDFBean {
	private String pdfFileName;

	@PostConstruct
	public void init() {
		String fileName = (String) FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("certificatePDFName");
		
		if(fileName != null) {
			this.pdfFileName = fileName;
		}
	}
	
	public String getPdfFileName() {
		return pdfFileName;
	}
}
