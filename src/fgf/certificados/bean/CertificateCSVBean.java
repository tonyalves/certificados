package fgf.certificados.bean;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.UploadedFile;

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
	
	public void uploadCSV() {
		BufferedReader br = null;
		List<Certificate> certificates = new ArrayList<>();

		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		String path = servletContext.getRealPath("/") + "resources/";

		if (logo != null && logo.getFileName() != null && !logo.getFileName().isEmpty()) {
			try {
				logo.write(path + logo.getFileName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (signature != null && signature.getFileName() != null && !signature.getFileName().isEmpty()) {
			try {
				signature.write(path + signature.getFileName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			Reader reader = new InputStreamReader(csv.getInputstream());
			br = new BufferedReader(reader);
		} catch (IOException e) {
			e.printStackTrace();
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

				certificate.setPathLogo(path + logo.getFileName());
				certificate.setPathSignature(path + signature.getFileName());
				certificate.setPathToGenerate(pathGenerator);
				certificate.setText(this.certificate.getText());
				certificate.setEventDateStr(lineSplited[2]);
				certificate.setLecturer(lineSplited[1]);
				
				certificates.add(certificate);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CertificateReportGenerator report = null;
		for(Certificate c : certificates) {
			try {
				report = new CertificateReportGenerator(c);
				report.generateReport();
			} catch (FileNotFoundException | JRException e) {
				e.printStackTrace();
			}
		}

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
	
	
	
}
