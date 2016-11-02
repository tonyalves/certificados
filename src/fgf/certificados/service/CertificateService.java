package fgf.certificados.service;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.primefaces.model.UploadedFile;

@ManagedBean
@ApplicationScoped
public class CertificateService {

	public void generatePDF() {
	
	}
	
	public void writeImageToFile(UploadedFile image, String path) {
		if (image != null && image.getFileName() != null && !image.getFileName().isEmpty()) {
			try {
				image.write(path + image.getFileName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
